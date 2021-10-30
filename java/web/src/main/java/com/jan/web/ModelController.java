package com.jan.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
        Thread.sleep(500);
        return modelRepository.findAll();
    }

    @GetMapping("/{id}")
    public Model getModel(@PathVariable Long id) {
        return modelRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/savemodel")
    public ResponseEntity<?> createModel(@RequestBody ModelRequest request)
    {
        System.out.println(request.getModelName());
        Model model = new Model();
        model.setName(request.getModelName());
        modelRepository.save(model);
        return ResponseEntity.ok("Project saved!");
    }

    @PostMapping(value = "/savemodel/files",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestPart(name = "labels", required = false) Labels labels,
            @RequestPart("filesOfFirstLabel") MultipartFile[] filesOfFirstLabel,
            @RequestPart("filesOfSecondLabel") MultipartFile[] filesOfSecondLabel
            )
    {
        System.out.println(labels);
        String firstLabel = labels.getLabels().get(0);
        String secondLabel = labels.getLabels().get(1);
        List<MultipartFile> filesOfFirstLabelList = Arrays.stream(filesOfFirstLabel).toList();
        List<MultipartFile> filesOfSecondLabelList = Arrays.stream(filesOfSecondLabel).toList();

        File parentDirectory = new File("//Users//jan//app_files");
        parentDirectory.mkdir();
        File directoryOfFirstLabel = new File("//Users//jan//app_files//" + firstLabel);
        directoryOfFirstLabel.mkdir();
        File directoryOfSecondLabel = new File("//Users//jan//app_files//" + secondLabel);
        directoryOfSecondLabel.mkdir();

        filesOfFirstLabelList.forEach(file -> {
            try
            {
                file.transferTo(new File("//Users//jan//app_files//" + firstLabel + "//" + file.getOriginalFilename()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        filesOfSecondLabelList.forEach(file -> {
            try
            {
                file.transferTo(new File("//Users//jan//app_files//" + secondLabel + "//" + file.getOriginalFilename()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok("Files uploaded!");
    }

    public List<Model> list() {
        return modelRepository.findAll();
    }
}