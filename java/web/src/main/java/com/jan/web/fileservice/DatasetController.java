package com.jan.web.fileservice;

import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ContainerUtility containerUtility;

    @Autowired
    public DatasetController(FileService fileService, JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository, ContainerRepository containerRepository, ContainerUtility containerUtility)
    {
        this.fileService = fileService;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles(@RequestHeader(name="Authorization") String token)
    {
        //TODO Jan: handle the situation when no file is in the driectory
        //TODO Jan: handle the situation when directories are empty, currently dirs are not visible
        return fileService.getAllFiles(containerUtility.getContainerIdFromToken(token));
    }

    @PostMapping(value = "createdirectory")
    public ResponseEntity<?> createDirectory(@RequestHeader(name="Authorization") String token, @RequestBody Key directoryKey)
    {
        fileService.createDirectory(directoryKey.getKey(), containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestHeader(name="Authorization") String token,
            @RequestPart("keys") Keys keys,
            @RequestPart("files") List<MultipartFile> files)
    {
        fileService.uploadFiles(keys, files, containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }
}
