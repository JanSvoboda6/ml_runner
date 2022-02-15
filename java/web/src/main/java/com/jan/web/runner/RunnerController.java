package com.jan.web.runner;

import com.jan.web.docker.ContainerRepository;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerUtility;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/project/runner")
public class RunnerController
{
    private final RunnerRepository runnerRepository;
    private final ContainerRepository containerRepository;
    private final ContainerUtility containerUtility;
    private final RunnerService runnerService;
    private final RequestValidator requestValidator;
    private final RequestMaker requestMaker;
    private final ObjectMapper objectMapper;

    @Autowired
    public RunnerController(RunnerRepository runnerRepository,
                            ContainerRepository containerRepository,
                            ContainerUtility containerUtility,
                            RunnerService runnerService,
                            RequestValidator requestValidator,
                            RequestMaker requestMaker,
                            ObjectMapper objectMapper)
    {
        this.runnerRepository = runnerRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
        this.runnerService = runnerService;
        this.requestValidator = requestValidator;
        this.requestMaker = requestMaker;
        this.objectMapper = objectMapper;
    }

    @Bean
    public ObjectMapper objectMapper()
    {
        return new ObjectMapper();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Runner getRunner(@RequestParam long projectId, @RequestParam long runnerId)
    {
        //TODO Jan: Why it has two 2 parameters? - we need just one parameter
        return runnerRepository.findRunnerByIdAndProjectId(runnerId, projectId);
    }

    @PostMapping("/run")
    public ResponseEntity<?> runProject(@RequestHeader(name = "Authorization") String token, @RequestBody RunRequest request)
    {
        runnerService.runProject(request,
                requestValidator.validateProject(request.getProjectId()),
                requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token)));

        return ResponseEntity.ok().body("Project is running!");
    }

    @GetMapping("/result")
    public ResponseEntity<?> getResult(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId) throws JSONException, IOException
    {
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(projectId);
        requestValidator.validateRunner(runnerId);

        JSONObject response = getResultResponse(projectId, runnerId, containerEntity);

        return ResponseEntity.ok(response.toString());
    }


    @PostMapping(value = "/finished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isFinished(@RequestHeader(name="Authorization") String token, @RequestBody FinishedRequest finishedRequest)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("projectId", finishedRequest.getProjectId());
            request.put("runnerId", finishedRequest.getRunnerId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(
                    request.toString(),
                    headers);
            Optional<ContainerEntity> containerEntity = containerRepository.findById(containerUtility.getContainerIdFromToken(token));
            if(containerEntity.isPresent())
            {
                ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                        containerEntity.get().getId(),
                        com.jan.web.request.RequestMethod.IS_RUNNER_FINISHED,
                        entity);

                FinishedResponse finishedResponse = objectMapper.readValue(responseFromContainer.getBody(), FinishedResponse.class);

                JSONObject response = new JSONObject();
                response.put("isFinished", finishedResponse.isFinished);

                if (finishedResponse.isFinished)
                {
                    JSONObject resultRequest = new JSONObject();
                    resultRequest.put("projectId", finishedRequest.getProjectId());
                    resultRequest.put("runnerId", finishedRequest.getRunnerId());
                    HttpHeaders resultHeaders = new HttpHeaders();
                    resultHeaders.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> resultEntity = new HttpEntity<>(resultRequest.toString(), resultHeaders);

                    ResponseEntity<String> resultResponseFromContainer = requestMaker.makePostRequest(
                            containerEntity.get().getId(),
                            RequestMethod.RUNNER_RESULT,
                            resultEntity);

                    ResultResponse resultResponse = objectMapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

                    response.put("firstLabelResult", resultResponse.firstLabelResult);
                    response.put("secondLabelResult", resultResponse.secondLabelResult);

                    if (runnerRepository.findById(finishedRequest.getRunnerId()).isPresent())
                    {
                        Runner runnerToBeUpdated = runnerRepository.findById(finishedRequest.getRunnerId()).get();
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

    private JSONObject getResultResponse(long projectId, long runnerId, ContainerEntity containerEntity) throws JSONException, IOException
    {
        JSONObject response = new JSONObject();
        JSONObject resultRequest = new JSONObject();

        resultRequest.put("projectId", projectId);
        resultRequest.put("runnerId", runnerId);

        HttpHeaders resultHeaders = new HttpHeaders();
        resultHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> resultEntity = new HttpEntity<>(resultRequest.toString(), resultHeaders);

        ResponseEntity<String> resultResponseFromContainer = requestMaker.makePostRequest(
                containerEntity.getId(),
                RequestMethod.RUNNER_RESULT,
                resultEntity);

        ResultResponse resultResponse = objectMapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

        response.put("firstLabelResult", resultResponse.firstLabelResult);
        response.put("secondLabelResult", resultResponse.secondLabelResult);
        return response;
    }
}