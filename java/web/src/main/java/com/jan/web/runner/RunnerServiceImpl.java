package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import com.jan.web.security.user.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class RunnerServiceImpl implements RunnerService
{
    RunnerRepository runnerRepository;
    private final ProjectRepository projectRepository;
    ProjectRunner projectRunner;
    private final ContainerRepository containerRepository;
    private final ResultRepository resultRepository;
    private final HyperParameterRepository hyperParameterRepository;
    private final RunnerQueueRepository runnerQueueRepository;
    private final RequestMaker requestMaker;
    private final ObjectMapper objectMapper;

    @Autowired
    public RunnerServiceImpl(RunnerRepository runnerRepository,
                             ProjectRepository projectRepository,
                             ProjectRunner projectRunner,
                             ContainerRepository containerRepository,
                             ResultRepository resultRepository,
                             HyperParameterRepository hyperParameterRepository,
                             RunnerQueueRepository runnerQueueRepository,
                             RequestMaker requestMaker,
                             ObjectMapper objectMapper)
    {
        this.runnerRepository = runnerRepository;
        this.projectRepository = projectRepository;
        this.projectRunner = projectRunner;
        this.containerRepository = containerRepository;
        this.resultRepository = resultRepository;
        this.hyperParameterRepository = hyperParameterRepository;
        this.runnerQueueRepository = runnerQueueRepository;
        this.requestMaker = requestMaker;
        this.objectMapper = objectMapper;
    }

    //TODO Jan: callWithHyperparameters
    @Override
    public void runProject(RunRequest request, Project project, ContainerEntity containerEntity)
    {
        Runner runner = mapRequestToRunner(request, project);
        if (isAnyRunnerRunning(containerEntity.getUser()))
        {
            runner.setStatus(RunnerStatus.SCHEDULED);
            runnerRepository.save(runner);
            runnerQueueRepository.save(new RunnerQueueEntity(containerEntity.getUser(), runner));
            return;
        } else
        {
            runner.setStatus(RunnerStatus.INITIAL);
        }
        projectRunner.run(runnerRepository.save(runner), containerEntity);
    }

    @Override
    public boolean isAnyRunnerRunning(User user)
    {
        List<Project> projects = projectRepository.findAllByUser(user);
        for(Project individualProject : projects)
        {
            List<Runner> runners = runnerRepository.findAllByProjectIdAndStatusIsNot(individualProject.getId(), RunnerStatus.SCHEDULED);
            for(Runner runner : runners)
            {
                if(!runner.getStatus().isEndState())
                {
                    return true;
                }
            }
        }
        return  false;
    }

    @Override
    public RunnerStatus getStatus(long containerId, long runnerId) throws JSONException, IOException
    {
        Runner runner = runnerRepository.findById(runnerId).get();
        RunnerStatus status = runner.getStatus();
        if (status.isEndState() || status == RunnerStatus.SCHEDULED)
        {
            return status;
        }
        HttpEntity<String> statusEntity = prepareStatusEntity(runnerId);
        Optional<ContainerEntity> containerEntityOptional = containerRepository.findById(containerId);

        if (containerEntityOptional.isPresent())
        {
            ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                    containerEntityOptional.get().getConnectionString(),
                    RequestMethod.RUNNER_STATUS,
                    statusEntity);

            StatusResponse statusResponse = objectMapper.readValue(responseFromContainer.getBody(), StatusResponse.class);
            List<String> statuses = statusResponse.chronologicalStatuses;
            runner.setStatus(RunnerStatus.valueOf(statuses.get(statuses.size() - 1)));
            runner.setChronologicalStatuses(String.join(",", statuses));
            runnerRepository.save(runner);
        }
        return runner.getStatus();
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
        Optional<ContainerEntity> containerEntityOptional = containerRepository.findById(containerId);
        if (containerEntityOptional.isPresent())
        {
            ResponseEntity<String> responseFromContainer = requestMaker.makePostRequest(
                    containerEntityOptional.get().getConnectionString(),
                    RequestMethod.IS_RUNNER_FINISHED,
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
                    containerRepository.findById(containerId).get().getConnectionString(),
                    RequestMethod.RUNNER_RESULT,
                    resultEntity);

            ResultResponse resultResponse = objectMapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

            Result result = new Result();
            result.setRunner(runnerRepository.findById(runnerId).get());
            result.setResultText(resultResponse.resultText);
            result.setAccuracy(resultResponse.accuracy);
            resultRepository.save(result);
            return Optional.of(result);
        } else
        {
            return Optional.empty();
        }
    }

    private HttpEntity<String> prepareRequestEntity(long projectId, long runnerId) throws JSONException
    {
        JSONObject resultRequest = new JSONObject();
        resultRequest.put("projectId", projectId);
        resultRequest.put("runnerId", runnerId);

        HttpHeaders resultHeaders = new HttpHeaders();
        resultHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(resultRequest.toString(), resultHeaders);
    }

    private HttpEntity<String> prepareStatusEntity(long runnerId) throws JSONException
    {
        JSONObject resultRequest = new JSONObject();
        resultRequest.put("runnerId", runnerId);
        HttpHeaders resultHeaders = new HttpHeaders();
        resultHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(resultRequest.toString(), resultHeaders);
    }

    private Runner mapRequestToRunner(RunRequest request, Project project)
    {
        Runner runner = new Runner();
        runner.setProject(project);
        runner.setHyperParameters(hyperParameterRepository.saveAll(request.getHyperParameters()));
        runner.setGammaParameter(request.getGammaParameter());
        runner.setCParameter(request.getCParameter());
        runner.setFinished(false);
        runner.setTimestamp(Instant.now().getEpochSecond());
        return runner;
    }
}
