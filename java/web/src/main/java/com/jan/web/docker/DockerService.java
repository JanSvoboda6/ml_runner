package com.jan.web.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

@Component
public class DockerService
{
    Logger logger = LoggerFactory.getLogger(DockerService.class);

    @Value("${jan.bindContainerToLocalhost}")
    private boolean bindContainerToLocalhost;

    @Value("${jan.dockerFilePath}")
    private String dockerFilePath;

    @Value("${jan.dockerImageName}")
    private String dockerImageName;

    @Value("${jan.containerLocalhostPort}")
    private Integer containerLocalhostPort;

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

    public DockerService(ContainerRepository containerRepository,
                         UserRepository userRepository,
                         DockerClient dockerClient,
                         String dockerFilePath,
                         String dockerImageName,
                         boolean bindContainerToLocalhost,
                         Integer containerLocalhostPort)
    {
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.dockerClient = dockerClient;
        this.dockerFilePath = dockerFilePath;
        this.dockerImageName = dockerImageName;
        this.bindContainerToLocalhost = bindContainerToLocalhost;
        this.containerLocalhostPort = containerLocalhostPort;
    }

    public long buildDockerContainer(Long userId)
    {
        if (containerHasBeenAlreadyBuilt(userId))
        {
            startContainerForTheUser(userId);
            return containerRepository.findByUserId(userId).get().getId();
        }

        ContainerEntity containerEntity = createContainerEntity(userId);

        dockerClient.buildImageCmd()
                .withDockerfile(new File(dockerFilePath))
                .withPull(true)
                .withTags(Set.of(dockerImageName))
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        List<Network> networks = dockerClient.listNetworksCmd().withNameFilter("web_runner_agents").exec();
        if(networks.isEmpty())
        {
            dockerClient.createNetworkCmd().withName("web_runner_agents").withInternal(true).exec();
        }

        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(dockerImageName)
                .withName(provideContainerName(userId))
                .withHostName(provideContainerName(userId))
                .withExposedPorts(ExposedPort.tcp(PYTHON_SERVER_PORT))
                .withHostConfig(new HostConfig().withNetworkMode("web_runner_agents"));

        containerEntity.setConnectionString("http://" + provideContainerName(userId) + ":" + PYTHON_SERVER_PORT);
        if(bindContainerToLocalhost)
        {
            Ports portBindings = createPortBindings(containerLocalhostPort);
            containerCmd.withHostConfig(new HostConfig().withPortBindings(portBindings));
            containerEntity.setConnectionString("http://localhost:" + containerLocalhostPort);
        }

        String containerId = containerCmd.exec().getId();
        dockerClient.startContainerCmd(containerId).exec();
        return containerRepository.save(containerEntity).getId();
    }

    private int generatePortNumber()
    {
        int maxPortNumber = 20000;
        int minPortNumber = 10000;
        return new Random().nextInt(maxPortNumber - minPortNumber) + minPortNumber;
    }

    private ContainerEntity createContainerEntity(Long userId)
    {
        User user = userRepository.getById(userId);
        return new ContainerEntity(user);
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
