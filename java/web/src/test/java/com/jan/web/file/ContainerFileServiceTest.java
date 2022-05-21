package com.jan.web.file;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.file.information.FileInformation;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest(properties = { "jan.bindContainerToLocalhost=true" })
class ContainerFileServiceTest
{
    private final long USER_ID = 999L;
    private final long CONTAINER_ID = 15000L;
    private final int CONTAINER_LOCALHOST_PORT = 9999;
    String CONTAINER_NAME = "container-user-" + USER_ID;
    String CONTAINER_NAME_WITH_ADDED_SLASH = "/" + CONTAINER_NAME;
    private DockerService dockerService;
    private DockerClient dockerClient;

    @Autowired
    private ContainerRequestMaker requestMaker;

    @Autowired
    private ContainerRepository containerRepository;

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
        containerEntity.setConnectionString("http://localhost:" + CONTAINER_LOCALHOST_PORT);

        Mockito.when(containerRepository.save(Mockito.any())).thenReturn(containerEntity);
        Mockito.when(containerRepository.findById(CONTAINER_ID)).thenReturn(Optional.of(containerEntity));

        String dockerFilePath = FileSystems.getDefault().getPath("./resources/python_runner_agent/Dockerfile").normalize().toAbsolutePath().toString();
        dockerService = new DockerService(containerRepository, userRepository, dockerClient, dockerFilePath, "python_server", true, CONTAINER_LOCALHOST_PORT, false);
        fileService = new ContainerFileService(containerRepository, requestMaker);

        buildDockerContainerAndWaitForTheServerToStart();
    }

    @Test
    public void whenCreatingDirectory_thenDirectoryIsCreatedInContainer()
    {
        final String directory = "test_directory/";
        fileService.createFolder(directory, CONTAINER_ID);
        List<FileInformation> files = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(files.get(0).getKey()).isEqualTo(directory);
    }

    @Test
    public void whenCreatingChildDirectory_thenChildDirectoryIsCreatedInContainer()
    {
        final String directory = "test_directory/";
        fileService.createFolder(directory, CONTAINER_ID);

        final String childDirectory = directory + "child_directory/";
        fileService.createFolder(childDirectory, CONTAINER_ID);
        List<FileInformation> files = fileService.getAllFiles(CONTAINER_ID);

        Assertions.assertThat(files.get(1).getKey()).isEqualTo(childDirectory);
    }

    @Test
    public void whenUploadingFiles_thenFilesAreStoredInContainer()
    {
        final String fileName = "test_file.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.uploadFiles(List.of(fileName), files, CONTAINER_ID);
        List<FileInformation> uploadedFiles = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(uploadedFiles.get(0).getKey()).isEqualTo(fileName);
    }

    @Test
    public void whenUploadingFilesToFolder_thenFilesAreStoredInContainer()
    {
        final String folderName = "AAA/";
        final String fileName = folderName + "aaa.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.uploadFiles(List.of(fileName), files, CONTAINER_ID);
        List<FileInformation> uploadedFiles = fileService.getAllFiles(CONTAINER_ID);
        Assertions.assertThat(uploadedFiles.get(1).getKey()).isEqualTo(fileName);
    }

    @Test
    public void whenDeletingFolder_thenFolderAndItsContentIsDeletedFromContainer()
    {
        final String folderName = "AAA/";
        final String fileName =  folderName + "test_file.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.createFolder(folderName + "BBB/", CONTAINER_ID);
        fileService.uploadFiles(List.of(fileName), files, CONTAINER_ID);
        fileService.deleteFolders(List.of(folderName), CONTAINER_ID);

        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID)).isEmpty();
    }

    @Test
    public void whenDeletingFiles_thenFilesAreDeletedFromContainer()
    {
        final String folderName = "AAA/";
        final String firstFileName =  folderName + "aaa.txt";
        final String secondFileName =  folderName + "bbb.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(firstFileName, new byte[1]),
                new MockMultipartFile(secondFileName, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.uploadFiles(List.of(firstFileName, secondFileName), files, CONTAINER_ID);
        fileService.deleteFiles(List.of(firstFileName, secondFileName), CONTAINER_ID);
        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID).size()).isEqualTo(1);
        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID).get(0).getKey()).isEqualTo(folderName);
    }

    @Test
    public void whenMovingFile_thenFileIsMovedInContainer()
    {
        final String folderName = "AAA/";
        final String fileName =  folderName + "aaa.txt";
        final String renamedFile =  folderName + "bbb.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(fileName, new byte[1]),
                new MockMultipartFile(renamedFile, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.uploadFiles(List.of(fileName, renamedFile), files, CONTAINER_ID);
        fileService.moveFile(fileName, renamedFile, CONTAINER_ID);
        Assertions.assertThat(fileService.getAllFiles(CONTAINER_ID).get(1).getKey()).isEqualTo(renamedFile);
    }

    @Test
    public void whenMovingFolder_thenFolderAndItsContentIsMovedInContainer()
    {
        final String folderName = "AAA/";
        final String newFolderName = "BBB/";
        final String firstFileName =  folderName + "aaa.txt";
        final String secondFileName =  folderName + "bbb.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(firstFileName, new byte[1]),
                new MockMultipartFile(secondFileName, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.uploadFiles(List.of(firstFileName, secondFileName), files, CONTAINER_ID);
        fileService.moveFolder(folderName, newFolderName, CONTAINER_ID);

        final String newFirstFileName =  newFolderName + "aaa.txt";
        final String newSecondFileName =  newFolderName + "bbb.txt";

        List<String> keys = fileService.getAllFiles(CONTAINER_ID).stream().map(FileInformation::getKey).collect(Collectors.toList());

        Assertions.assertThat(keys.size()).isEqualTo(3);
        Assertions.assertThat(keys).contains(newFolderName);
        Assertions.assertThat(keys).contains(newFirstFileName);
        Assertions.assertThat(keys).contains(newSecondFileName);
    }

    @Test
    public void whenFolderIsDownloaded_thenZipFileIsReturned()
    {
        final String folderName = "AAA/";
        final String firstFileName =  folderName + "aaa.txt";
        final String secondFileName =  folderName + "bbb.txt";
        List<MultipartFile> files = List.of(new MockMultipartFile(firstFileName, new byte[1]),
                new MockMultipartFile(secondFileName, new byte[1]));

        fileService.createFolder(folderName, CONTAINER_ID);
        fileService.uploadFiles(List.of(firstFileName, secondFileName), files, CONTAINER_ID);
        ResponseEntity<byte[]> downloadResponse = fileService.download(List.of(folderName), CONTAINER_ID);

        Assertions.assertThat(downloadResponse.getBody()).isNotNull();
    }

    @AfterEach
    void after()
    {
        deleteContainer();
    }

    private void deleteContainer()
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
        Thread.sleep(1000);
    }
}