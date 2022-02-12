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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DockerServiceTest
{
    private final long USER_ID = 999L;
    private final long HOST_PORT = 15000L;
    String CONTAINER_NAME = "container-user-" + USER_ID;
    String CONTAINER_NAME_WITH_ADDED_SLASH = "/" + CONTAINER_NAME;
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
        dockerService = new DockerService(containerRepository, userRepository);
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
        DockerService dockerServiceWithMockedDockerClient = new DockerService(containerRepository, userRepository, dockerClient);

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