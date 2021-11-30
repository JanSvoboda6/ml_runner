package com.jan.web.fileservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController
{
    private final FileService fileService;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserRepository userRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public DatasetController(FileService fileService, JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository, ContainerRepository containerRepository)
    {
        this.fileService = fileService;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
        this.containerRepository = containerRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles(@RequestHeader(name="Authorization") String token)
    {
        //TODO Jan: handle the situation when no file is in the driectory
        //TODO Jan: handle the situation when directories are empty, currently dirs are not visible
        return fileService.getAllFiles(getContainerId(token));
    }

    @PostMapping(value = "createdirectory")
    public ResponseEntity<?> createDirectory(@RequestHeader(name="Authorization") String token, @RequestBody Key directoryKey)
    {
        fileService.createDirectory(directoryKey.getKey(), getContainerId(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestHeader(name="Authorization") String token,
            @RequestPart("keys") Keys keys,
            @RequestPart("files") List<MultipartFile> files)
    {
        fileService.uploadFiles(keys, files, getContainerId(token));
        return ResponseEntity.ok("OK.");
    }

    private Long getContainerId(String token)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if( user.isPresent())
        {
            return containerRepository.findByUserId(user.get().getId()).getId();
        }
        throw new RuntimeException("No user found!");
    }
}
