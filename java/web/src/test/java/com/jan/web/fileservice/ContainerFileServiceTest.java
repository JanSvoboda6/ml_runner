package com.jan.web.fileservice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.request.ContainerRequestMaker;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ContainerFileServiceTest
{
    private final long USER_ID = 999L;
    private final long CONTAINER_ID = 15000L;
    String CONTAINER_NAME = "container-user-" + USER_ID;
    String CONTAINER_NAME_WITH_ADDED_SLASH = "/" + CONTAINER_NAME;
    private DockerService dockerService;
    private DockerClient dockerClient;

    @Autowired
    private ContainerRequestMaker requestMaker;

    private ContainerFileService fileService;

    @BeforeEach
    void before() throws InterruptedException
    {
        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        dockerClient = DockerClientBuilder.getInstance(config.build()).build();
        validateThatDockerIsRunning();

        ContainerRepository containerRepository = Mockito.mock(ContainerRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        User user = new User(USER_ID, "user@domain.com", "password");
        Mockito.when(userRepository.getById(USER_ID)).thenReturn(user);

        ContainerEntity containerEntity = new ContainerEntity(CONTAINER_ID, user);

        Mockito.when(containerRepository.save(Mockito.any())).thenReturn(containerEntity);
        Mockito.when(containerRepository.findById(CONTAINER_ID)).thenReturn(Optional.of(containerEntity));

        String dockerFilePath = FileSystems.getDefault().getPath("../../docker/Dockerfile").normalize().toAbsolutePath().toString();
        dockerService = new DockerService(containerRepository, userRepository, dockerClient, dockerFilePath, "python_server");
        fileService = new ContainerFileService(containerRepository, requestMaker);

        buildDockerContainerAndWaitForTheServerToStart();
    }

    @Test
    public void whenCreatingDirectory_thenDirectoryIsCreatedInContainer()
    {
        final String directory = "test_directory/";
        fileService.createDirectory(directory, CONTAINER_ID);
        List<FileInformation> files = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(files.get(0).getKey()).isEqualTo(directory);
    }

    @Test
    public void whenCreatingChildDirectory_thenChildDirectoryIsCreatedInContainer()
    {
        final String directory = "test_directory/";
        fileService.createDirectory(directory, CONTAINER_ID);

        final String childDirectory = directory + "child_directory/";
        fileService.createDirectory(childDirectory, CONTAINER_ID);
        List<FileInformation> files = fileService.getAllFiles(CONTAINER_ID);

        Assertions.assertThat(files.get(1).getKey()).isEqualTo(childDirectory);
    }

    @Test
    public void whenUploadingFiles_thenFilesAreStoredInContainer()
    {
        final String fileName = "test_file.txt";
        Keys keys = new Keys();
        keys.setKeys(List.of(fileName));
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.uploadFiles(keys, files, CONTAINER_ID);
        List<FileInformation> uploadedFiles = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(uploadedFiles.get(0).getKey()).isEqualTo(fileName);
    }

    @Test
    public void whenUploadingFilesToFolder_thenFilesAreStoredInContainer()
    {
        final String folderName = "AAA/";
        final String fileName = folderName + "aaa.txt";
        Keys keys = new Keys();
        keys.setKeys(List.of(fileName));
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.createDirectory(folderName, CONTAINER_ID);
        fileService.uploadFiles(keys, files, CONTAINER_ID);
        List<FileInformation> uploadedFiles = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(uploadedFiles.get(1).getKey()).isEqualTo(fileName);
    }

    @Test
    public void whenDeletingFolder_thenFolderAndItsContentIsDeletedFromContainer()
    {
        final String folderName = "AAA/";
        final String fileName =  folderName + "test_file.txt";

        Keys fileKeys = new Keys();
        fileKeys.setKeys(List.of(fileName));
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.createDirectory(folderName, CONTAINER_ID);
        fileService.createDirectory(folderName + "BBB/", CONTAINER_ID);
        fileService.uploadFiles(fileKeys, files, CONTAINER_ID);
        fileService.deleteFolders(List.of(folderName), CONTAINER_ID);

        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID)).isEmpty();
    }

    @Test
    public void whenDeletingFiles_thenFilesAreDeletedFromContainer()
    {
        final String folderName = "AAA/";
        final String firstFileName =  folderName + "aaa.txt";
        final String secondFileName =  folderName + "bbb.txt";

        Keys fileKeys = new Keys();
        fileKeys.setKeys(List.of(firstFileName, secondFileName));
        List<MultipartFile> files = List.of(new MockMultipartFile(firstFileName, new byte[1]),
                new MockMultipartFile(secondFileName, new byte[1]));

        fileService.createDirectory(folderName, CONTAINER_ID);
        fileService.uploadFiles(fileKeys, files, CONTAINER_ID);
        fileService.deleteFiles(List.of(firstFileName, secondFileName), CONTAINER_ID);
        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID).size()).isEqualTo(1);
        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID).get(0).getKey()).isEqualTo(folderName);
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

    private void buildDockerContainerAndWaitForTheServerToStart() throws InterruptedException
    {
        dockerService.buildDockerContainer(USER_ID);
        Thread.sleep(500);
    }
}