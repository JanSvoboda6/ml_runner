package com.jan.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController
{
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectController(ProjectRepository projectRepository)
    {
        this.projectRepository = projectRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getProjects()
    {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/saveproject")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest request)
    {
        Project project = new Project(
                request.getProjectName(),
                request.getFirstLabel(),
                request.getSecondLabel(),
                request.getFirstLabelFolder(),
                request.getSecondLabelFolder(),
                request.getSelectedModel());
        projectRepository.save(project);
        return ResponseEntity.ok("Project " + project.getName() + " saved!");
    }

    public List<Project> list() {
        return projectRepository.findAll();
    }
}