package com.jan.web.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public class DockerService
{
    public void createDockerContainer()
    {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config.build()).build();

        ExposedPort tcpPort = ExposedPort.tcp(9999);
        Ports portBindings = new Ports();
        portBindings.bind(tcpPort, Ports.Binding.bindPort(9999));

        String containerId = null;
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withStatusFilter(List.of("exited"))
                .exec();
        if (containers.size() == 0)
        {
            String imageId = dockerClient.buildImageCmd()
                    .withDockerfile(new File("/Users/jan/dev/thesis/ml_runner/docker/Dockerfile"))
                    .withPull(true)
                    .withNoCache(true)
                    .withTag("python_server")
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();

            containerId = dockerClient.createContainerCmd("python_server")
                    .withName("testing-container")
                    .withHostName("testing-container-hostname")
                    .withExposedPorts(tcpPort)
                    .withHostConfig(new HostConfig().withPortBindings(portBindings))
                    .exec()
                    .getId();
        } else
        {
            containerId = containers.get(0).getId();
        }
        dockerClient.startContainerCmd(containerId).exec();

        //copyFiles();

        //dockerClient.stopContainerCmd(containerId).exec();
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
}
