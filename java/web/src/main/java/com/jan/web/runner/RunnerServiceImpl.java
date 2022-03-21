package com.jan.web.runner;

import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


@Component
public class RunnerServiceImpl implements RunnerService
{
    RunnerRepository runnerRepository;
    ProjectRunner projectRunner;
    private final ContainerRepository containerRepository;
    private final ResultRepository resultRepository;
    private final RequestMaker requestMaker;
    private final ObjectMapper objectMapper;

    @Autowired
    public RunnerServiceImpl(RunnerRepository runnerRepository,
                             ProjectRunner projectRunner,
                             ContainerRepository containerRepository,
                             ResultRepository resultRepository,
                             RequestMaker requestMaker,
                             ObjectMapper objectMapper)
    {
        this.runnerRepository = runnerRepository;
        this.projectRunner = projectRunner;
        this.containerRepository = containerRepository;
        this.resultRepository = resultRepository;
        this.requestMaker = requestMaker;
        this.objectMapper = objectMapper;
    }

    @Override
    public void runProject(RunRequest request, Project project, ContainerEntity containerEntity)
    {
        projectRunner.run(mapRequestToRunner(request, project), containerEntity.getId());
    }

    @Override
    public boolean isFinished(long containerId, long projectId, long runnerId) throws IOException, JSONException
    {
        if (runnerRepository.findById(runnerId).isPresent())
        {
            boolean isFinished = runnerRepository.findById(runnerId).get().isFinished();
            if (isFinished)
            {
                return true;
            }
        }

        HttpEntity<String> resultEntity = prepareRequestEntity(projectId, runnerId);

        Optional<ContainerEntity> containerEntity = containerRepository.findById(containerId);
        if (containerEntity.isPresent())
        {
            ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                    containerEntity.get().getId(),
                    com.jan.web.request.RequestMethod.IS_RUNNER_FINISHED,
                    resultEntity);

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

    @Override
    public Optional<Result> getResult(long containerId, long projectId, long runnerId) throws IOException, JSONException
    {
        if (isFinished(containerId, projectId, runnerId))
        {
            Optional<Result> alreadyPresentResult = resultRepository.findByRunnerId(runnerId);
            if (alreadyPresentResult.isPresent())
            {
                return alreadyPresentResult;
            }

            HttpEntity<String> resultEntity = prepareRequestEntity(projectId, runnerId);

            ResponseEntity<String> resultResponseFromContainer = requestMaker.makePostRequest(
                    containerId,
                    RequestMethod.RUNNER_RESULT,
                    resultEntity);

            ResultResponse resultResponse = objectMapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

            Result result = new Result();
            result.setRunner(runnerRepository.findById(runnerId).get());
            result.setFirstLabelResult(resultResponse.firstLabelResult);
            result.setFirstLabelResult(resultResponse.secondLabelResult);
            resultRepository.save(result);
            return Optional.of(result);
        }
        else
        {
            return Optional.empty();
        }
    }

    private HttpEntity<String> prepareRequestEntity ( long projectId, long runnerId) throws JSONException
    {
        JSONObject resultRequest = new JSONObject();
        resultRequest.put("projectId", projectId);
        resultRequest.put("runnerId", runnerId);

        HttpHeaders resultHeaders = new HttpHeaders();
        resultHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(resultRequest.toString(), resultHeaders);
    }

    private Runner mapRequestToRunner(RunRequest request, Project project)
    {
        Runner runner = new Runner();
        runner.setProject(project);
        runner.setGammaParameter(request.getGammaParameter());
        runner.setCParameter(request.getcParameter());
        runner.setFinished(false);
        runnerRepository.save(runner);
        return runner;
    }
}
