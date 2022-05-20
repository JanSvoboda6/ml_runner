package com.jan.web.fileservice;

import com.jan.web.docker.ContainerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

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
        fileService.createFolder(directoryKey.getKey(), containerUtility.getContainerIdFromToken(token));
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

    @PostMapping(value = "/files/move")
    public ResponseEntity<?> moveFile(@RequestHeader(name="Authorization") String token, @RequestBody MoveRequest request)
    {
        fileService.moveFile(request.getOldKey(), request.getNewKey(), containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/folders/move")
    public ResponseEntity<?> moveFolder(@RequestHeader(name="Authorization") String token, @RequestBody MoveRequest request)
    {
        fileService.moveFolder(request.getOldKey(), request.getNewKey(), containerUtility.getContainerIdFromToken(token));
        return ResponseEntity.ok("OK.");
    }

    @PostMapping(value = "/download", produces="application/zip")
    public ResponseEntity<Resource> download(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        var response = fileService.download(keys, containerUtility.getContainerIdFromToken(token));
        ByteArrayResource resource = new ByteArrayResource(Objects.requireNonNull(response.getBody()));
        MediaType mediaType = MediaTypeFactory
                .getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        ContentDisposition disposition = ContentDisposition
                .attachment()
                .build();
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
