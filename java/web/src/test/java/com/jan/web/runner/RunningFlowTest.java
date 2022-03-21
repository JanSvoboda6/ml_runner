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
import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.security.authentication.AuthenticationController;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8080")
public class RunningFlowTest
{
    private static final String EMAIL = "user@email.com";
    private static final String PASSWORD = "StrongPassword_999";

    @Autowired
    private AuthenticationController authenticationController;

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

    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeEach
    public void before() throws InterruptedException
    {
        User user = new User();
        user.setUsername(EMAIL);
        user.setPassword(PASSWORD);
        user.setRoles(Set.of(roleRepository.findByName(RoleType.ROLE_USER).get()));
        userRepository.save(user);
        buildDockerContainerAndWaitForTheServerToStart(userRepository.findByUsername(EMAIL).get().getId());
    }

    @Test
    @Timeout(value = 60)
    public void whenRequestForRunningRunnerWithValidParameters_thenRunnerRunsAndReturnsResult() throws IOException, JSONException, InterruptedException
    {
        Optional<ContainerEntity> containerIdOptional = containerRepository.findByUserId(userRepository.findByUsername(EMAIL).get().getId());
        if(containerIdOptional.isEmpty())
        {
            Assertions.fail("Container record has not been found in the DB!");
        }
        long containerId = containerIdOptional.get().getId();

        containerFileService.createFolder("test_folder/", containerId);
        containerFileService.createFolder("test_folder/first_class/", containerId);
        containerFileService.createFolder("test_folder/second_class/", containerId);
        MultipartFile firstFile = new MockMultipartFile("feature_vector_first_class.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_first_class.npy")));
        MultipartFile secondFile = new MockMultipartFile("feature_vector_second_class.npy", Files.readAllBytes(Path.of("src/test/java/com/jan/web/resources/feature_vector_second_class.npy")));

        Keys keys = new Keys();
        keys.setKeys(List.of("test_folder/first_class/feature_vector_first_class.npy", "test_folder/second_class/feature_vector_second_class.npy"));
        containerFileService.uploadFiles(keys, List.of(firstFile, secondFile), containerId);

        Project project = new Project(userRepository.findByUsername(EMAIL).get(),
                "test_project",
                "first_label",
                "second_label",
                "test_folder/first_class/",
                "test_folder/second_class/",
                "Support Vector Machines");

        Long projectId = projectRepository.save(project).getId();

        Runner runner = new Runner();
        runner.setProject(project);
        runner.setCParameter(1.0);
        runner.setGammaParameter(10.0);
        runnerRepository.save(runner);

        containerProjectRunner.run(runner, containerId);

        boolean isFinished = false;

        while(!isFinished)
        {
            waiter.await(1, TimeUnit.SECONDS);
            isFinished = runnerService.isFinished(containerId, projectId, runner.getId());
        }

        Assertions.assertThat(runnerService.getResult(containerId, projectId, runner.getId())).isPresent();
    }

    private void buildDockerContainerAndWaitForTheServerToStart(long userId) throws InterruptedException
    {
        dockerService.buildDockerContainer(userId);
        waiter.await(5, TimeUnit.SECONDS);
    }

    @AfterEach
    void after()
    {
        deleteContainerOfUser(userRepository.findByUsername(EMAIL).get().getId());
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