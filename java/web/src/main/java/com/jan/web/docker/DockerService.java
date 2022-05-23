package com.jan.web.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Service class for building a Docker container.
 * Depending on settings it could map the container to localhost port (random one or chosen).
 * In production configuration no port mapping is taking place, containers are distinguished by names.
 */
@Service
public class DockerService
{
    private static final StringKeyGenerator GENERATOR = KeyGenerators.string();

    @Value("${jan.bindContainerToLocalhost}")
    private boolean bindContainerToLocalhost;

    @Value("${jan.dockerFilePath}")
    private String dockerFilePath;

    @Value("${jan.dockerImageName}")
    private String dockerImageName;

    @Value("${jan.containerLocalhostPort}")
    private Integer containerLocalhostPort;

    @Value("${jan.mapToRandomPort}")
    private boolean shouldContainerBeMappedToRandomPort;

    @Value("${jan.useRandomSuffixInContainerName}")
    private boolean useRandomSuffix;

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
                         Integer containerLocalhostPort,
                         boolean shouldContainerBeMappedToRandomPort,
                         boolean useRandomSuffix)
    {
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.dockerClient = dockerClient;
        this.dockerFilePath = dockerFilePath;
        this.dockerImageName = dockerImageName;
        this.bindContainerToLocalhost = bindContainerToLocalhost;
        this.containerLocalhostPort = containerLocalhostPort;
        this.shouldContainerBeMappedToRandomPort = shouldContainerBeMappedToRandomPort;
        this.useRandomSuffix = useRandomSuffix;
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

        String containerName = provideContainerName(userId);
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(dockerImageName)
                .withName(containerName)
                .withHostName(containerName)
                .withExposedPorts(ExposedPort.tcp(PYTHON_SERVER_PORT))
                .withHostConfig(new HostConfig().withNetworkMode("web_runner_agents"));

        containerEntity.setContainerName(containerName);
        containerEntity.setConnectionString("http://" + containerName + ":" + PYTHON_SERVER_PORT);
        if(bindContainerToLocalhost)
        {
            if(shouldContainerBeMappedToRandomPort)
            {
                containerLocalhostPort = generatePortNumber();
            }
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
        String containerName = "container-user-" + userId.toString();
        if(useRandomSuffix)
        {
            containerName += "-" + GENERATOR.generateKey();
        }
        return containerName;
    }

    private boolean containerHasBeenAlreadyBuilt(Long userId)
    {
        return containerRepository.existsByUserId(userId);
    }

    private void startContainerForTheUser(Long userId)
    {
        dockerClient.startContainerCmd(containerRepository.findByUserId(userId).get().getContainerName());
    }

    private Ports createPortBindings(long hostPortNumber)
    {
        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(PYTHON_SERVER_PORT), Ports.Binding.bindPort((int)hostPortNumber));
        return portBindings;
    }
}
