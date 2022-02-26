package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final ResultRepository resultRepository;

    @Autowired
    public RunnerController(RunnerRepository runnerRepository,
                            ContainerRepository containerRepository,
                            ContainerUtility containerUtility,
                            RunnerService runnerService,
                            RequestValidator requestValidator,
                            RequestMaker requestMaker,
                            ObjectMapper objectMapper, ResultRepository resultRepository)
    {
        this.runnerRepository = runnerRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
        this.runnerService = runnerService;
        this.requestValidator = requestValidator;
        this.requestMaker = requestMaker;
        this.objectMapper = objectMapper;
        this.resultRepository = resultRepository;
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

    //TODO: Jan - projectId is redundant
    @GetMapping("/result")
    public ResponseEntity<?> getResult(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId) throws JSONException, IOException
    {
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(projectId);
        requestValidator.validateRunner(runnerId);

        if(isFinished(containerEntity.getId(), projectId, runnerId))
        {
            JSONObject response = getResultResponse(projectId, runnerId, containerEntity);

            return ResponseEntity.ok(response.toString());
        }

        return ResponseEntity.ok("Result cannot be obtained since project is still running!");
    }

    @PostMapping(value = "/finished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isFinished(@RequestHeader(name="Authorization") String token, @RequestBody FinishedRequest finishedRequest) throws JSONException, IOException
    {
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(finishedRequest.getProjectId());
        requestValidator.validateRunner(finishedRequest.getRunnerId());

        JSONObject response = new JSONObject();
        response.put("isFinished", isFinished(containerEntity.getId(), finishedRequest.getProjectId(), finishedRequest.getRunnerId()));

        return ResponseEntity.ok(response.toString());
    }

    private boolean isFinished(long containerId, long projectId, long runnerId) throws JSONException, IOException
    {
        if (runnerRepository.findById(runnerId).isPresent())
        {
            boolean isFinished = runnerRepository.findById(runnerId).get().isFinished();
            if (isFinished)
            {
                return true;
            }
        }

        JSONObject request = new JSONObject();
        request.put("projectId", projectId);
        request.put("runnerId", runnerId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                request.toString(),
                headers);

        Optional<ContainerEntity> containerEntity = containerRepository.findById(containerId);
        if (containerEntity.isPresent())
        {
            ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                    containerEntity.get().getId(),
                    com.jan.web.request.RequestMethod.IS_RUNNER_FINISHED,
                    entity);

            FinishedResponse finishedResponse = objectMapper.readValue(responseFromContainer.getBody(), FinishedResponse.class);

            if (finishedResponse.isFinished)
            {
                if (runnerRepository.findById(runnerId).isPresent())
                {
                    Runner runnerToBeUpdated = runnerRepository.findById(runnerId).get();
                    runnerToBeUpdated.setFinished(true);
                    runnerRepository.save(runnerToBeUpdated);
                }
            }

            return finishedResponse.isFinished;
        }
        return false;
    }

    private JSONObject getResultResponse(long projectId, long runnerId, ContainerEntity containerEntity) throws JSONException, IOException
    {
        JSONObject response = new JSONObject();
        Result result = resultRepository.findByRunnerId(runnerId);
        if(result != null)
        {
            response.put("firstLabelResult", result.getFirstLabelResult());
            response.put("secondLabelResult", result.getSecondLabelResult());
            return response;
        }
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

        result = new Result();
        result.setRunner(runnerRepository.findById(runnerId).get());
        result.setFirstLabelResult(resultResponse.firstLabelResult);
        result.setFirstLabelResult(resultResponse.secondLabelResult);
        resultRepository.save(result);

        return response;
    }
}