package com.jan.web;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.runner.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController
{
    private final ProjectRepository projectRepository;
    private final RestTemplate restTemplate;
    private final RunnerRepository runnerRepository;
    private final ContainerRepository containerRepository;
    private final ContainerUtility containerUtility;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             RestTemplate restTemplate,
                             RunnerRepository runnerRepository,
                             ContainerRepository containerRepository,
                             ContainerUtility containerUtility)
    {
        this.projectRepository = projectRepository;
        this.restTemplate = restTemplate;
        this.runnerRepository = runnerRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
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
            HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
            Optional<ContainerEntity> containerEntity = containerRepository.findById(containerUtility.getContainerIdFromToken(token));
            if(containerEntity.isPresent())
            {
                ResponseEntity<String> responseFromContainer = restTemplate
                        .exchange("http://localhost:" + containerEntity.get().getId() + "/project/runner/finished", HttpMethod.POST, entity, String.class);

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
                    ResponseEntity<String> resultResponseFromContainer = restTemplate
                            .exchange("http://localhost:" + containerEntity.get().getId() + "/project/runner/result", HttpMethod.POST, resultEntity, String.class);

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
    public List<Runner> getRunnerList(@RequestParam(name="projectId") long id)
    {
        return runnerRepository.findAllByProjectId(id);
    }

    public List<Project> list() {
        return projectRepository.findAll();
    }
}