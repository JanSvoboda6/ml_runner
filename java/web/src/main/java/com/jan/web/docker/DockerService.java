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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

@Component
public class DockerService
{
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

    public DockerService(ContainerRepository containerRepository, UserRepository userRepository, DockerClient dockerClient)
    {
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.dockerClient = dockerClient;
    }

    public void buildDockerContainer(Long userId)
    {
        if (containerHasBeenAlreadyBuilt(userId))
        {
            startContainerForTheUser(userId);
            return;
        }

        ContainerEntity containerEntity = createContainerEntity(userId);
        Ports portBindings = createPortBindings(containerEntity.getHostPort());

        dockerClient.buildImageCmd()
                .withDockerfile(new File("/Users/jan/dev/thesis/ml_runner/docker/Dockerfile"))
                .withPull(true)
                .withNoCache(true)
                .withTags(Set.of("python_server"))
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        String containerId = dockerClient.createContainerCmd("python_server")
                .withName(provideContainerName(userId))
                .withHostName(provideContainerName(userId))
                .withExposedPorts(ExposedPort.tcp(PYTHON_SERVER_PORT))
                .withHostConfig(new HostConfig().withPortBindings(portBindings))
                .exec()
                .getId();

        dockerClient.startContainerCmd(containerId).exec();
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

    public void copyFiles()
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://localhost:9999");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

        File file = new File("/Users/jan/app_files/text.txt");
        try
        {
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(file),
                    ContentType.DEFAULT_BINARY,
                    file.getName()
            );
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        CloseableHttpResponse response = null;
        try
        {
            response = httpClient.execute(uploadFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean containerHasBeenAlreadyBuilt(Long userId)
    {
        return containerRepository.existsByUserId(userId);
    }

    private void startContainerForTheUser(Long userId)
    {
        dockerClient.startContainerCmd(provideContainerName(userId));
    }

    private Ports createPortBindings(int hostPortNumber)
    {
        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(PYTHON_SERVER_PORT), Ports.Binding.bindPort(hostPortNumber));
        return portBindings;
    }
}
