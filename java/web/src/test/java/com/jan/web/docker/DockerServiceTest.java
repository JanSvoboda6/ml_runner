package com.jan.web.docker;

import org.assertj.core.api.Assertions;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class DockerServiceTest
{
    private static final long USER_ID = 999L;
    private static final long HOST_PORT = 15000L;
    private static final String CONTAINER_NAME = "container-user-" + USER_ID;
    private static final String CONTAINER_NAME_WITH_ADDED_SLASH = "/" + CONTAINER_NAME;
    private DockerService dockerService;
    private ContainerRepository containerRepository;
    private DockerClient dockerClient;
    private UserRepository userRepository;

    @BeforeEach
    void before()
    {
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        dockerClient = DockerClientBuilder.getInstance(config.build()).build();
        validateThatDockerIsRunning();

        containerRepository = Mockito.mock(ContainerRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        String dockerFilePath = FileSystems.getDefault().getPath("../../python/Dockerfile").normalize().toAbsolutePath().toString();

        dockerService = new DockerService(containerRepository,
                userRepository,
                dockerClient,
                dockerFilePath,
                "python_server");
    }

    @Test
    public void whenContainerHasNotBeenBuiltYet_thenNewContainerWillBeCreated()
    {
        Mockito.when(containerRepository.existsByUserId(USER_ID)).thenReturn(false);
        User user = new User(USER_ID, "user@domain.com", "password");
        Mockito.when(userRepository.getById(USER_ID)).thenReturn(user);
        Mockito.when(containerRepository.save(Mockito.any())).thenReturn(new ContainerEntity(HOST_PORT, user));

        dockerService.buildDockerContainer(USER_ID);
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        boolean isContainerCreated = false;

        for (Container container : containers)
        {
            for (String name : container.getNames())
            {
                if (CONTAINER_NAME_WITH_ADDED_SLASH.equals(name))
                {
                    isContainerCreated = true;
                    break;
                }
            }

            if (isContainerCreated)
            {
                break;
            }
        }
        Assertions.assertThat(isContainerCreated).isTrue();
    }

    @Test
    public void whenContainerHasBeenAlreadyBuilt_thenContainerWillBeStarted()
    {
        DockerClient dockerClient = Mockito.mock(DockerClient.class);
        Mockito.when(containerRepository.existsByUserId(USER_ID)).thenReturn(true);
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(container.getId()).thenReturn(999L);
        Mockito.when(containerRepository.findByUserId(USER_ID)).thenReturn(Optional.of(container));
        DockerService dockerServiceWithMockedDockerClient = new DockerService(containerRepository,
                userRepository,
                dockerClient,
                "Random path to docker file",
                "Random name of Docker image");

        dockerServiceWithMockedDockerClient.buildDockerContainer(USER_ID);

        Mockito.verify(dockerClient).startContainerCmd(CONTAINER_NAME);
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