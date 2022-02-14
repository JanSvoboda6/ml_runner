package com.jan.web;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.runner.*;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;
    private final ContainerUtility containerUtility;
    private final RequestMaker requestMaker;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             JsonWebTokenUtility jsonWebTokenUtility,
                             RunnerRepository runnerRepository,
                             ContainerRepository containerRepository,
                             UserRepository userRepository,
                             ContainerUtility containerUtility,
                             RequestMaker requestMaker)
    {
        this.projectRepository = projectRepository;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.runnerRepository = runnerRepository;
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.containerUtility = containerUtility;
        this.requestMaker = requestMaker;
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
            Project project = new Project(
                    user.get(),
                    request.getProjectName(),
                    request.getFirstLabel(),
                    request.getSecondLabel(),
                    request.getFirstLabelFolder(),
                    request.getSecondLabelFolder(),
                    request.getSelectedModel());
            projectRepository.save(project);
            return ResponseEntity.ok("Project " + project.getName() + " saved!");
        }
        return ResponseEntity.badRequest().body("Problem with creating project!");
    }

    @PostMapping(value = "/runner/finished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isFinished(@RequestHeader(name="Authorization") String token, @RequestBody FinishedRequest frontendRequest)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("projectId", frontendRequest.getProjectId());
            request.put("runnerId", frontendRequest.getRunnerId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(
                    request.toString(),
                    headers);
            Optional<ContainerEntity> containerEntity = containerRepository.findById(containerUtility.getContainerIdFromToken(token));
            if(containerEntity.isPresent())
            {
                ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                        (int) containerEntity.get().getId(),
                        RequestMethod.IS_PROJECT_FINISHED,
                        entity);

                ObjectMapper mapper = new ObjectMapper();
                FinishedResponse finishedResponse = mapper.readValue(responseFromContainer.getBody(), FinishedResponse.class);

                JSONObject response = new JSONObject();
                response.put("isFinished", finishedResponse.isFinished);

                if (finishedResponse.isFinished)
                {
                    JSONObject resultRequest = new JSONObject();
                    resultRequest.put("projectId", frontendRequest.getProjectId());
                    resultRequest.put("runnerId", frontendRequest.getRunnerId());
                    HttpHeaders resultHeaders = new HttpHeaders();
                    resultHeaders.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> resultEntity = new HttpEntity<>(resultRequest.toString(), resultHeaders);

                    ResponseEntity<String> resultResponseFromContainer = requestMaker.makePostRequest(
                            (int) containerEntity.get().getId(),
                            RequestMethod.PROJECT_RESULT,
                            resultEntity);

                    ResultResponse resultResponse = mapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

                    response.put("firstLabelResult", resultResponse.firstLabelResult);
                    response.put("secondLabelResult", resultResponse.secondLabelResult);

                    if (runnerRepository.findById(frontendRequest.getRunnerId()).isPresent())
                    {
                        Runner runnerToBeUpdated = runnerRepository.findById(frontendRequest.getRunnerId()).get();
                        runnerToBeUpdated.setFinished(true);
                        runnerRepository.save(runnerToBeUpdated);
                    }
                }

                return ResponseEntity.ok(response.toString());
            }

        } catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Problem with obtaining the information!");
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