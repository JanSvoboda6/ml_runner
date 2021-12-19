package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.ProjectRepository;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ContainerProjectRunner implements ProjectRunner
{

    private final RestTemplate restTemplate;
    private final RunnerRepository runnerRepository;
    private final ProjectRepository projectRepository;
    private final ContainerRepository containerRepository;
    private final ContainerUtility containerUtility;

    @Autowired
    public ContainerProjectRunner(RestTemplate restTemplate,
                                  RunnerRepository runnerRepository,
                                  ProjectRepository projectRepository,
                                  ContainerRepository containerRepository,
                                  ContainerUtility containerUtility)
    {
        this.restTemplate = restTemplate;
        this.runnerRepository = runnerRepository;
        this.projectRepository = projectRepository;
        this.containerRepository = containerRepository;
        this.containerUtility = containerUtility;
    }

    @Override
    public void run(Runner runner, long containerId)
    {
        try
        {
            if (runnerRepository.findById(runner.getId()).isPresent())
            {
                Project project = runner.getProject();
                JSONObject request = new JSONObject();
                request.put("projectId", project.getId());
                request.put("name", project.getName());
                request.put("firstLabel", project.getFirstLabel());
                request.put("secondLabel", project.getSecondLabel());
                request.put("firstLabelFolder", project.getFirstLabelFolder());
                request.put("secondLabelFolder", project.getSecondLabelFolder());
                request.put("selectedModel", project.getSelectedModel());
                request.put("runnerId", runner.getId());
                request.put("gammaParameter", runner.getGammaParameter());
                request.put("cParameter", runner.getCParameter());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);
                ResponseEntity<String> response = restTemplate
                        .exchange("http://localhost:" + containerId + "/runproject", HttpMethod.POST, entity, String.class);

                //TODO Jan: respond
                response.getStatusCode();
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean stop(Runner runner)
    {
        //not implemented
        return false;
    }
}
