package com.jan.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController
{
    @PostMapping("/saveproject")
    public ResponseEntity<?> createModel(@RequestBody ProjectRequest request)
    {
        //TODO Jan:Implement file handler
        System.out.println(request.getProjectName());
        return ResponseEntity.ok("Project saved!");
    }

    @PostMapping(value = "/saveproject/files",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestPart(name = "jsonBodyData", required = false) String parameters,
            @RequestPart("files") MultipartFile[] files)
    {
        //TODO Jan:Implement file handler
        System.out.println(parameters);
        List<MultipartFile> filesList = Arrays.stream(files).toList();
        filesList.forEach(file -> {
            try
            {
                file.transferTo(new File("//Users//jan//app_files//" + file.getOriginalFilename()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok("Files uploaded!");
    }
}