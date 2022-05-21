package com.jan.web.project;

import com.jan.web.project.label.ClassificationLabel;
import com.jan.web.project.label.ClassificationLabelRepository;
import com.jan.web.security.validation.RequestValidator;
import com.jan.web.runner.Runner;
import com.jan.web.runner.RunnerRepository;
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

/**
 * Controller providing API for creating/getting a project.
 */
@RestController
@RequestMapping("/api/project")
public class ProjectController
{
    private final ProjectRepository projectRepository;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final RunnerRepository runnerRepository;
    private final UserRepository userRepository;
    private final ClassificationLabelRepository labelRepository;
    private final RequestValidator validator;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             JsonWebTokenUtility jsonWebTokenUtility,
                             RunnerRepository runnerRepository,
                             UserRepository userRepository,
                             ClassificationLabelRepository labelRepository,
                             RequestValidator validator)
    {
        this.projectRepository = projectRepository;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.runnerRepository = runnerRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.validator = validator;
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
    public Project getProject(@RequestHeader(name="Authorization") String token, @PathVariable Long id)
    {
        return projectRepository.findByUserAndId(validator.validateUser(jsonWebTokenUtility.getUsernameFromJwtToken(token)), id).get();
    }

    @PostMapping("/saveproject")
    public ResponseEntity<?> createProject(@RequestHeader(name="Authorization") String token, @RequestBody ProjectRequest request)
    {
        User user = validator.validateUser(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        List<ClassificationLabel> classificationLabels = labelRepository.saveAll(request.getClassificationLabels());
        Project project = new Project(
                user,
                request.getProjectName(),
                request.getSelectedModel(),
                classificationLabels
        );
        Project savedProject = projectRepository.save(project);
        return ResponseEntity.ok().body(Collections.singletonMap("id", savedProject.getId()));
    }

    @GetMapping(value = "/runners", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Runner> getRunnerList(@RequestHeader(name="Authorization") String token, @RequestParam(name="projectId") long id)
    {
        User user = validator.validateUser(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        Optional<Project> project = projectRepository.findById(id);
        if(project.isPresent())
        {
            if (user == project.get().getUser())
            {
                return runnerRepository.findAllByProjectId(id);
            }
        }
        return Collections.emptyList();
    }
}