package com.jan.web.fileservice;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LocalFileService implements FileService
{
    public static final String BASE_DIRECTORY = "~/app_files";

    @Override
    public List<FileInformation> getAllFiles()
    {
        List<FileInformation> fileInformationList = null;
        try (Stream<Path> walk = Files.walk(Paths.get(BASE_DIRECTORY))) {
           fileInformationList = walk.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(file -> new FileInformation(file.getName(), file.length(), file.lastModified())).collect(Collectors.toList());

            fileInformationList.forEach(System.out::println);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return fileInformationList;
    }
}
