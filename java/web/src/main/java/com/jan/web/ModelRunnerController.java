package com.jan.web;

import com.jan.web.runner.ModelRunner;
import com.jan.web.runner.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/runner")
public class ModelRunnerController
{
    private final ModelRepository modelRepository;
    private final ModelRunner modelRunner;

    @Autowired
    public ModelRunnerController(ModelRepository modelRepository, ModelRunner modelRunner)
    {
        this.modelRepository = modelRepository;
        this.modelRunner = modelRunner;
    }

    @PostMapping("/run")
    public ResponseEntity<?> createModel(@RequestBody IdHolder modelIdHolder) throws InterruptedException
    {
        System.out.println(modelIdHolder);
        Optional<Model> model = modelRepository.findById(modelIdHolder.getId());
        Result result = null;

        if(model.isPresent())
        {
            result = modelRunner.run(model.get());
        }

        if (result != null)
       {
           return ResponseEntity.ok(result);
       }
       return  ResponseEntity.badRequest().body("Problem with running selected model!");
    }
}