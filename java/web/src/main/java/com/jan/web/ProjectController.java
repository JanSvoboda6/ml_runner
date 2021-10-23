package com.jan.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProjectController
{
    @PostMapping("/saveproject")
    public ResponseEntity<?> createModel(@RequestBody ProjectRequest request)
    {
        //TODO Jan:Implement file handler
        return ResponseEntity.ok("Project saved!");
    }
}