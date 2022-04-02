package com.jan.web.runner;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.DockerService;
import com.jan.web.fileservice.ContainerFileService;
import com.jan.web.fileservice.Keys;
import com.jan.web.project.ClassificationLabel;
import com.jan.web.project.ClassificationLabelRepository;
import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(properties = { "jan.bindContainerToLocalhost=true" , "jan.containerLocalhostPort=9999"})
public class RunningFlowTest
{
    private String email;
    private static final String PASSWORD = "StrongPassword_999";

    @Autowired
    private DockerService dockerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContainerFileService containerFileService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContainerProjectRunner containerProjectRunner;

    @Autowired
    private RunnerRepository runnerRepository;

    @Autowired
    private RunnerService runnerService;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClassificationLabelRepository classificationLabelRepository;

    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeEach
    public void before() throws InterruptedException
    {
        User user = new User();
        email = "user" + new Random().nextInt() + "@email.com";
        user.setUsername(email);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(roleRepository.findByName(RoleType.ROLE_USER).get()));
        userRepository.save(user);
        buildDockerContainerAndWaitForTheServerToStart(userRepository.findByUsername(email).get().getId());
    }

    @Test
    @Timeout(value = 60)
    public void whenRequestForRunningRunnerWithValidParameters_thenRunnerRunsAndReturnsResult() throws IOException, JSONException, InterruptedException
    {
        Optional<ContainerEntity> containerOptional = containerRepository.findByUserId(userRepository.findByUsername(email).get().getId());
        if(containerOptional.isEmpty())
        {
            Assertions.fail("Container record has not been found in the DB!");
        }
        long containerId = containerOptional.get().getId();

        containerFileService.createFolder("test_folder/", containerId);
        containerFileService.createFolder("test_folder/first_class/", containerId);
        containerFileService.createFolder("test_folder/second_class/", containerId);
        MultipartFile firstLabelFile1 = new MockMultipartFile("feature_vector_first_class_1.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_1.npy")));
        MultipartFile firstLabelFile2 = new MockMultipartFile("feature_vector_first_class_2.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_2.npy")));
        MultipartFile firstLabelFile3 = new MockMultipartFile("feature_vector_first_class_3.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_3.npy")));
        MultipartFile secondLabelFile1 = new MockMultipartFile("feature_vector_second_class_1.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_1.npy")));
        MultipartFile secondLabelFile2 = new MockMultipartFile("feature_vector_second_class_2.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_2.npy")));
        MultipartFile secondLabelFile3 = new MockMultipartFile("feature_vector_second_class_3.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_3.npy")));

        Keys keys = new Keys();
        keys.setKeys(List.of("test_folder/first_class/feature_vector_first_class_1.npy",
                "test_folder/first_class/feature_vector_first_class_2.npy",
                "test_folder/first_class/feature_vector_first_class_3.npy",
                "test_folder/second_class/feature_vector_second_class_1.npy",
                "test_folder/second_class/feature_vector_second_class_2.npy",
                "test_folder/second_class/feature_vector_second_class_3.npy"));

        containerFileService.uploadFiles(keys, List.of(firstLabelFile1, firstLabelFile2, firstLabelFile3, secondLabelFile1, secondLabelFile2, secondLabelFile3), containerId);

        List<ClassificationLabel> classificationLabels = List.of(
                new ClassificationLabel("first_label", "test_folder/first_class/"),
                new ClassificationLabel("second_label", "test_folder/second_class/")
        );
        classificationLabelRepository.saveAll(classificationLabels);

        Project project = new Project(userRepository.findByUsername(email).get(),
                "test_project",
                "Support Vector Machines",
                classificationLabels);

        Long projectId = projectRepository.save(project).getId();

        Runner runner = new Runner();
        runner.setProject(project);
        runner.setCParameter(1.0);
        runner.setGammaParameter(10.0);
        runnerRepository.save(runner);

        containerProjectRunner.run(runner, containerOptional.get());

        boolean isFinished = false;

        while(!isFinished)
        {
            waiter.await(1, TimeUnit.SECONDS);
            isFinished = runnerService.isFinished(containerId, projectId, runner.getId());
        }

        Assertions.assertThat(runnerService.getResult(containerId, projectId, runner.getId())).isPresent();
        Assertions.assertThat(runnerService.getResult(containerId, projectId, runner.getId()).get().getFirstLabelResult()).isNotNull();
        Assertions.assertThat(runnerService.getResult(containerId, projectId, runner.getId()).get().getSecondLabelResult()).isNotNull();
    }

    @Test
    @Timeout(value = 60)
    public void whenRequestForRunningRunnerWithValidParameters_thenRunnerRunsAndAllStatusesHaveBeenExecuted() throws IOException, JSONException, InterruptedException
    {
        Optional<ContainerEntity> containerOptional = containerRepository.findByUserId(userRepository.findByUsername(email).get().getId());
        if(containerOptional.isEmpty())
        {
            Assertions.fail("Container record has not been found in the DB!");
        }
        long containerId = containerOptional.get().getId();

        containerFileService.createFolder("test_folder/", containerId);
        containerFileService.createFolder("test_folder/first_class/", containerId);
        containerFileService.createFolder("test_folder/second_class/", containerId);
        MultipartFile firstLabelFile1 = new MockMultipartFile("feature_vector_first_class_1.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_1.npy")));
        MultipartFile firstLabelFile2 = new MockMultipartFile("feature_vector_first_class_2.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_2.npy")));
        MultipartFile firstLabelFile3 = new MockMultipartFile("feature_vector_first_class_3.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class_3.npy")));
        MultipartFile secondLabelFile1 = new MockMultipartFile("feature_vector_second_class_1.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_1.npy")));
        MultipartFile secondLabelFile2 = new MockMultipartFile("feature_vector_second_class_2.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_2.npy")));
        MultipartFile secondLabelFile3 = new MockMultipartFile("feature_vector_second_class_3.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class_3.npy")));

        Keys keys = new Keys();
        keys.setKeys(List.of("test_folder/first_class/feature_vector_first_class_1.npy",
                "test_folder/first_class/feature_vector_first_class_2.npy",
                "test_folder/first_class/feature_vector_first_class_3.npy",
                "test_folder/second_class/feature_vector_second_class_1.npy",
                "test_folder/second_class/feature_vector_second_class_2.npy",
                "test_folder/second_class/feature_vector_second_class_3.npy"));

        containerFileService.uploadFiles(keys, List.of(firstLabelFile1, firstLabelFile2, firstLabelFile3, secondLabelFile1, secondLabelFile2, secondLabelFile3), containerId);

        List<ClassificationLabel> classificationLabels = List.of(
                new ClassificationLabel("first_label", "test_folder/first_class/"),
                new ClassificationLabel("second_label", "test_folder/second_class/")
        );
        classificationLabelRepository.saveAll(classificationLabels);

        Project project = new Project(userRepository.findByUsername(email).get(),
                "test_project",
                "Support Vector Machines",
                classificationLabels);

        Long projectId = projectRepository.save(project).getId();

        Runner runner = new Runner();
        runner.setProject(project);
        runner.setCParameter(1.0);
        runner.setGammaParameter(10.0);
        runner.setStatus(RunnerStatus.INITIAL);
        runnerRepository.save(runner);

        containerProjectRunner.run(runner, containerOptional.get());

        boolean isFinished = false;

        while(!isFinished)
        {
            waiter.await(1, TimeUnit.SECONDS);
            isFinished = runnerService.isFinished(containerId, projectId, runner.getId());
            runnerService.getStatus(containerId, runner.getId());
        }

        Runner runnerAfterFlowExecution = runnerRepository.findById(runner.getId()).get();
        Assertions.assertThat(runnerAfterFlowExecution.getChronologicalStatuses()).contains("INITIAL,PREPARING_DATA,TRAINING,PREDICTING,FINISHED");
        Assertions.assertThat(runnerAfterFlowExecution.getStatus()).isEqualTo(RunnerStatus.FINISHED);
    }

    private void buildDockerContainerAndWaitForTheServerToStart(long userId) throws InterruptedException
    {
        dockerService.buildDockerContainer(userId);
        waiter.await(5, TimeUnit.SECONDS);
    }

    @AfterEach
    void after()
    {
        deleteContainerOfUser(userRepository.findByUsername(email).get().getId());
    }

    private void deleteContainerOfUser(long userId)
    {
        String containerName = "container-user-" + userId;
        String containerNameWithAddedSlash = "/" + containerName;

        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config.build()).build();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        String idOfContainerCreatedInTest = "";
        for (Container container : containers)
        {
            for (String name : container.getNames())
            {
                if (containerNameWithAddedSlash.equals(name))
                {
                    idOfContainerCreatedInTest = container.getId();
                    break;
                }
            }
            if (idOfContainerCreatedInTest.length() > 0)
            {
                break;
            }
        }

        if (idOfContainerCreatedInTest.length() > 0)
        {
            dockerClient.removeContainerCmd(idOfContainerCreatedInTest).withForce(true).exec();
        }
    }
}
