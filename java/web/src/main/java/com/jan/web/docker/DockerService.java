package com.jan.web.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

@Component
public class DockerService
{
    @Value("${jan.dockerFilePath}")
    private String dockerFilePath;

    @Value("${jan.dockerImageName}")
    private String dockerImageName;

    private final int PYTHON_SERVER_PORT = 9999;
    private final DockerClient dockerClient;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;

    @Autowired
    public DockerService(ContainerRepository containerRepository, UserRepository userRepository)
    {
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        dockerClient = DockerClientBuilder.getInstance(config.build()).build();
    }

    public DockerService(ContainerRepository containerRepository, UserRepository userRepository, DockerClient dockerClient, String dockerFilePath, String dockerImageName)
    {
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.dockerClient = dockerClient;
        this.dockerFilePath = dockerFilePath;
        this.dockerImageName = dockerImageName;
    }

    public long buildDockerContainer(Long userId)
    {
        if (containerHasBeenAlreadyBuilt(userId))
        {
            startContainerForTheUser(userId);
            return  containerRepository.findIdByUserId(userId);
        }

        ContainerEntity containerEntity = createContainerEntity(userId);
        Ports portBindings = createPortBindings(containerEntity.getId());
        dockerClient.buildImageCmd()
                .withDockerfile(new File(dockerFilePath))
                .withPull(true)
                .withTags(Set.of(dockerImageName))
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        String containerId = dockerClient.createContainerCmd(dockerImageName)
                .withName(provideContainerName(userId))
                .withHostName(provideContainerName(userId))
                .withExposedPorts(ExposedPort.tcp(PYTHON_SERVER_PORT))
                .withHostConfig(new HostConfig().withPortBindings(portBindings))
                .exec()
                .getId();

        dockerClient.startContainerCmd(containerId).exec();
        return containerEntity.getId();
    }

    private ContainerEntity createContainerEntity(Long userId)
    {
        User user = userRepository.getById(userId);
        return containerRepository.save(new ContainerEntity(user));
    }

    private String provideContainerName(Long userId)
    {
        return "container-user-" + userId.toString();
    }

    private boolean containerHasBeenAlreadyBuilt(Long userId)
    {
        return containerRepository.existsByUserId(userId);
    }

    private void startContainerForTheUser(Long userId)
    {
        dockerClient.startContainerCmd(provideContainerName(userId));
    }

    private Ports createPortBindings(long hostPortNumber)
    {
        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(PYTHON_SERVER_PORT), Ports.Binding.bindPort((int)hostPortNumber));
        return portBindings;
    }
}
