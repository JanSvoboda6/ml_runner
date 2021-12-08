package com.jan.web;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.runner.ProjectRunner;
import com.jan.web.runner.ResultResponse;
import com.jan.web.runner.Runner;
import com.jan.web.runner.RunnerRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/project/runner")
public class ModelRunnerController
{
    private final ProjectRepository projectRepository;
    private final RunnerRepository runnerRepository;
    private final ContainerRepository containerRepository;
    private final ContainerUtility containerUtility;
    private final ProjectRunner projectRunner;
    private final RestTemplate restTemplate;

    @Autowired
    public ModelRunnerController(ProjectRepository projectRepository,
                                 RunnerRepository runnerRepository,
                                 ContainerRepository containerRepository,
                                 ContainerUtility containerUtility,
                                 ProjectRunner projectRunner,
                                 RestTemplate restTemplate)
    {
        this.projectRepository = projectRepository;
        this.runnerRepository = runnerRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
        this.projectRunner = projectRunner;
        this.restTemplate = restTemplate;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Runner getRunner(@RequestParam long projectId, @RequestParam long runnerId)
    {
        return runnerRepository.findRunnerByIdAndProjectId(runnerId, projectId);
    }

    @PostMapping("/run")
    public ResponseEntity<?> runProject(@RequestHeader(name="Authorization") String token, @RequestBody RunRequest request) throws InterruptedException
    {
        Optional<Project> project = projectRepository.findById(request.getProjectId());
        if(project.isPresent())
        {
            Optional<ContainerEntity> containerEntity = containerRepository.findById(containerUtility.getContainerIdFromToken(token));
            if (containerEntity.isPresent())
            {
                Runner runner = new Runner();
                runner.setProject(project.get());
                runner.setGammaParameter(request.getGammaParameter());
                runner.setCParameter(request.getcParameter());
                runner.setFinished(false);
                runnerRepository.save(runner);

                projectRunner.run(runner, containerEntity.get().getId());
                return ResponseEntity.ok().body("Project is running!");
            }
        }

       return  ResponseEntity.badRequest().body("Problem with running selected project!");
    }

    @GetMapping("/result")
    public ResponseEntity<?> getResult(@RequestHeader(name="Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId)
    {
        try
        {
            JSONObject response = new JSONObject();
            JSONObject resultRequest = new JSONObject();
            ObjectMapper mapper = new ObjectMapper();

            resultRequest.put("projectId", projectId);
            resultRequest.put("runnerId", runnerId);

            HttpHeaders resultHeaders = new HttpHeaders();
            resultHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> resultEntity = new HttpEntity<>(resultRequest.toString(), resultHeaders);
            Optional<ContainerEntity> containerEntity = containerRepository.findById(containerUtility.getContainerIdFromToken(token));
            if(containerEntity.isPresent())
            {
                ResponseEntity<String> resultResponseFromContainer = restTemplate
                        .exchange("http://localhost:" + containerEntity.get().getId() + "/project/runner/result", HttpMethod.POST, resultEntity, String.class);

                ResultResponse resultResponse = mapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

                response.put("firstLabelResult", resultResponse.firstLabelResult);
                response.put("secondLabelResult", resultResponse.secondLabelResult);

                return ResponseEntity.ok(response.toString());
            }
        }catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Problem with getting results!");
    }
}