package com.jan.web;

import com.jan.web.runner.ProjectRunner;
import com.jan.web.runner.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/runner")
public class ModelRunnerController
{
    private final ProjectRepository projectRepository;
    private final ProjectRunner projectRunner;

    @Autowired
    public ModelRunnerController(ProjectRepository projectRepository, ProjectRunner projectRunner)
    {
        this.projectRepository = projectRepository;
        this.projectRunner = projectRunner;
    }

    @PostMapping("/run")
    public ResponseEntity<?> createModel(@RequestBody IdHolder modelIdHolder) throws InterruptedException
    {
        System.out.println(modelIdHolder);
        Optional<Project> model = projectRepository.findById(modelIdHolder.getId());
        Result result = null;

        if(model.isPresent())
        {
            result = projectRunner.run(model.get());
        }

        if (result != null)
       {
           return ResponseEntity.ok(result);
       }
       return  ResponseEntity.badRequest().body("Problem with running selected model!");
    }
}