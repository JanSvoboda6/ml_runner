package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ContainerProjectRunner implements ProjectRunner
{
    private final RequestMaker requestMaker;

    @Autowired
    public ContainerProjectRunner(RequestMaker requestMaker)
    {
        this.requestMaker = requestMaker;
    }

    @Override
    public void run(Runner runner, long containerId)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try
        {
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(
                    assembleRequest(runner).toString(),
                    headers);
            requestMaker.makePostRequest(containerId, RequestMethod.RUN_PROJECT, entity);

        } catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private JSONObject assembleRequest(Runner runner) throws JSONException
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
        return request;
    }

    @Override
    public boolean stop(Runner runner)
    {
        //not implemented
        return false;
    }
}
