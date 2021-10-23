package com.jan.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController
{
    private final ModelRepository modelRepository;

    @Autowired
    public ModelController(ModelRepository modelRepository)
    {
        this.modelRepository = modelRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Model> getModels() throws InterruptedException
    {
        //TODO Jan: mimic server side delay
        Thread.sleep(2000);
        return modelRepository.findAll();
    }

    @GetMapping("/{id}")
    public Model getModel(@PathVariable Long id) {
        return modelRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/api/saveproject")
    public ResponseEntity<?> createModel(@Valid @RequestBody ProjectRequest request)
    {
        System.out.println(request);
        return ResponseEntity.ok("Project saved!");
    }

    public List<Model> list() {
        return modelRepository.findAll();
    }
}