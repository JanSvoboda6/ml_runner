package com.jan.web.fileservice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.DockerService;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ContainerFileServiceTest
{
    private final long USER_ID = 999L;
    private final long CONTAINER_ID = 15000L;
    String CONTAINER_NAME = "container-user-" + USER_ID;
    String CONTAINER_NAME_WITH_ADDED_SLASH = "/" + CONTAINER_NAME;
    private DockerService dockerService;
    private ContainerRepository containerRepository;
    private DockerClient dockerClient;
    private UserRepository userRepository;
    private Long containerId;

    @Autowired
    ContainerFileService fileService;

    @BeforeEach
    void before()
    {
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        dockerClient = DockerClientBuilder.getInstance(config.build()).build();
        validateThatDockerIsRunning();

        containerRepository = Mockito.mock(ContainerRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        User user = new User(USER_ID, "user@domain.com", "password");
        Mockito.when(userRepository.getById(USER_ID)).thenReturn(user);

        ContainerEntity containerEntity = new ContainerEntity(CONTAINER_ID, user);

        Mockito.when(containerRepository.save(Mockito.any())).thenReturn(containerEntity);

        dockerService = new DockerService(containerRepository, userRepository);

        dockerService.buildDockerContainer(USER_ID);
    }

    @Test
    public void whenCreatingDirectory_thenDirectoryIsCreatedInContainer()
    {
        final String directoryName = "test_directory";
        fileService.createDirectory(directoryName, CONTAINER_ID);

        Assertions.fail("Test case not implemented.");
    }

    @Test
    public void whenUploadingFiles_thenFilesAreStoredInContainer()
    {
        Assertions.fail("Test case not implemented.");
    }

    @Test
    public void whenGettingAllFiles_thenProperFileInformationListIsReturned()
    {
        Assertions.fail("Test case not implemented.");
    }

    @AfterEach
    void after()
    {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        String idOfContainerCreatedInTest = "";
        for (Container container : containers)
        {
            for (String name : container.getNames())
            {
                if (CONTAINER_NAME_WITH_ADDED_SLASH.equals(name))
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

    private void validateThatDockerIsRunning()
    {
        Assertions.assertThatCode(() -> dockerClient.pingCmd().exec())
                .withFailMessage("Docker services cannot be reached. Make sure that Docker engine is running.")
                .doesNotThrowAnyException();
    }
}