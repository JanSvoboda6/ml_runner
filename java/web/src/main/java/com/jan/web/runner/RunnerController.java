package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.security.validation.RequestValidator;
import com.jan.web.runner.result.Result;
import com.jan.web.runner.status.RunnerStatus;
import com.jan.web.security.user.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/project/runner")
public class RunnerController
{
    private final ContainerUtility containerUtility;
    private final RunnerService runnerService;
    private final RequestValidator requestValidator;

    @Autowired
    public RunnerController(ContainerUtility containerUtility, RunnerService runnerService, RequestValidator requestValidator)
    {
        this.containerUtility = containerUtility;
        this.runnerService = runnerService;
        this.requestValidator = requestValidator;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Runner getRunner(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId)
    {
        User user = requestValidator.validateUserFromJwtToken(token);
        requestValidator.validateProject(projectId, user);
        return requestValidator.validateRunner(runnerId, user);
    }

    @PostMapping("/run")
    public ResponseEntity<?> runProject(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody RunRequest request)
    {
        User user = requestValidator.validateUserFromJwtToken(token);
        runnerService.runProject(request.getHyperParameters(),
                requestValidator.validateProject(request.getProjectId(), user),
                requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token)));

        return ResponseEntity.ok().body("Project is running!");
    }

    @GetMapping(value = "/result", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getResult(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId) throws JSONException, IOException
    {
        User user = requestValidator.validateUserFromJwtToken(token);
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(projectId, user);
        requestValidator.validateRunner(runnerId, user);

        Optional<Result> result = runnerService.getResult(containerEntity.getId(), projectId, runnerId);
        if(result.isPresent())
        {
            return ResponseEntity.ok(prepareJsonResultResponse(result.get()).toString());
        }
        return ResponseEntity.ok("Result cannot be obtained since project is still running!");
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatus(@RequestHeader(name="Authorization") String token, @RequestParam long runnerId) throws JSONException, IOException
    {
        User user = requestValidator.validateUserFromJwtToken(token);
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateRunner(runnerId, user);
        RunnerStatus status = runnerService.getStatus(containerEntity.getId(), runnerId);
        return ResponseEntity.ok(prepareJsonStatusResponse(status).toString());
    }

    private JSONObject prepareJsonResultResponse(Result result) throws JSONException
    {
        JSONObject response = new JSONObject();
        response.put("resultText", result.getResultText());
        response.put("accuracy", result.getAccuracy());
        return response;
    }

    private JSONObject prepareJsonStatusResponse(RunnerStatus status) throws JSONException
    {
        JSONObject response = new JSONObject();
        response.put("status", status.toString());
        response.put("isEndState", status.isEndState());
        return response;
    }
}