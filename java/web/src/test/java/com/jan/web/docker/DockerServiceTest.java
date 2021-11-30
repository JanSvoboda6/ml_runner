package com.jan.web.docker;

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
    private final int HOST_PORT = 15000;
    String CONTAINER_NAME = "/container-user-" + USER_ID;
    private DockerService dockerService;
    private ContainerRepository containerRepository;
    private DockerClient dockerClient;
    private UserRepository userRepository;

    @BeforeEach
    void before()
    {
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        dockerClient = DockerClientBuilder.getInstance(config.build()).build();
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
        Mockito.when(containerRepository.save(Mockito.any())).thenReturn(new ContainerEntity(user, HOST_PORT));
        dockerService.buildDockerContainer(USER_ID);
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

        boolean isContainerCreated = false;

        for (Container container : containers)
        {
            for (String name : container.getNames())
            {
                if (CONTAINER_NAME.equals(name))
                {
                    isContainerCreated = true;
                    break;
                }
            }

            if(isContainerCreated)
            {
                break;
            }
        }
        Assertions.assertTrue(isContainerCreated);
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
                if (CONTAINER_NAME.equals(name))
                {
                    idOfContainerCreatedInTest = container.getId();
                    break;
                }
            }

            if(idOfContainerCreatedInTest.length() > 0)
            {
                break;
            }
        }

        if(idOfContainerCreatedInTest.length() > 0)
        {
            dockerClient.removeContainerCmd(idOfContainerCreatedInTest).withForce(true).exec();
        }
    }
}