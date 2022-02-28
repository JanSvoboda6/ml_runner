package com.jan.web.fileservice;

import com.jan.web.docker.ContainerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController
{
    private final FileService fileService;
    private final ContainerUtility containerUtility;

    @Autowired
    public DatasetController(FileService fileService, ContainerUtility containerUtility)
    {
        this.fileService = fileService;
        this.containerUtility = containerUtility;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles(@RequestHeader(name="Authorization") String token)
    {
        return fileService.getAllFiles(containerUtility.getContainerIdFromToken(token));
    }

    @PostMapping(value = "createdirectory") //TODO Jan: rename to directory
    public ResponseEntity<?> createDirectory(@RequestHeader(name="Authorization") String token, @RequestBody Key directoryKey) //TODO Jan: is Key needed?
    {
        //TODO Jan: Filter keys with parent directory symbols ..
        fileService.createDirectory(directoryKey.getKey(), containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestHeader(name="Authorization") String token,
            @RequestPart("keys") Keys keys, //TODO Jan: Are Keys needed?
            @RequestPart("files") List<MultipartFile> files)
    {
        fileService.uploadFiles(keys, files, containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/folders/delete")
    public ResponseEntity<?> batchDeleteFolders(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        fileService.deleteFolders(keys, containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/files/delete")
    public ResponseEntity<?> batchDeleteFiles(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        fileService.deleteFiles(keys, containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }
}
