package com.jan.web.project;

import com.jan.web.runner.*;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController
{
    private final ProjectRepository projectRepository;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final RunnerRepository runnerRepository;
    private final UserRepository userRepository;
    private final ClassificationLabelRepository labelRepository;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             JsonWebTokenUtility jsonWebTokenUtility,
                             RunnerRepository runnerRepository,
                             UserRepository userRepository,
                             ClassificationLabelRepository labelRepository)
    {
        this.projectRepository = projectRepository;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.runnerRepository = runnerRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getProjects(@RequestHeader(name="Authorization") String token)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            return projectRepository.findAllByUser(user.get());
        }
        return Collections.emptyList();
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping("/saveproject")
    public ResponseEntity<?> createProject(@RequestHeader(name="Authorization") String token, @RequestBody ProjectRequest request)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent())
        {
            List<ClassificationLabel> classificationLabels = labelRepository.saveAll(request.getClassificationLabels());
            Project project = new Project(
                    user.get(),
                    request.getProjectName(),
                    request.getFirstLabel(),
                    request.getSecondLabel(),
                    request.getFirstLabelFolder(),
                    request.getSecondLabelFolder(),
                    request.getSelectedModel(),
                    classificationLabels
            );
            projectRepository.save(project);
            return ResponseEntity.ok("Project " + project.getName() + " saved!");
        }
        return ResponseEntity.badRequest().body("Problem with creating project!");
    }

    @GetMapping(value = "/runners", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Runner> getRunnerList(@RequestHeader(name="Authorization") String token, @RequestParam(name="projectId") long id)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent())
        {
            Optional<Project> project = projectRepository.findById(id);
            if(project.isPresent())
            {
                if (user.get() == project.get().getUser())
                {
                    return runnerRepository.findAllByProjectId(id);
                }
            }
        }
        return Collections.emptyList();
    }
}