package com.jan.web.fileservice;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController
{
    public static final String BASE_DIRECTORY = "/Users/jan/app_files/";
    private final FileService fileService;

    @Autowired
    public DatasetController(FileService fileService)
    {
        this.fileService = fileService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles() throws InterruptedException
    {
        //TODO Jan: handle the situation when no file is in the driectory
        //TODO Jan: handle the situation when directories are empty, currently dirs are not visible
        Thread.sleep(1000);
        return fileService.getAllFiles();
    }

    @PostMapping(value = "createdirectory")
    public ResponseEntity<?> createDirectory(@RequestBody Key directoryKey)
    {
        try
        {
            Files.createDirectories(Paths.get(BASE_DIRECTORY + directoryKey.getKey()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestPart("keys") Keys keys,
            @RequestPart("files") List<MultipartFile> files)
    {
        try
        {
            for (int i = 0; i < keys.getKeys().size(); i++)
            {
                Files.createDirectories(Paths.get(BASE_DIRECTORY + separateFolderPath(keys.getKeys().get(i))));
                files.get(i).transferTo(Paths.get(BASE_DIRECTORY + keys.getKeys().get(i)));
            }

        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return ResponseEntity.ok("OK.");
    }

    private String separateFolderPath(String filePath)
    {
        int indexOfLastSlash = filePath.lastIndexOf("/");
        return filePath.substring(0, indexOfLastSlash);
    }
}
