package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.project.Project;
import com.jan.web.project.label.ClassificationLabel;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.runner.parameter.HyperParameter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Makes a request for {@link Runner} execution in a container.
 */
@Service
public class ContainerProjectRunner implements ProjectRunner
{
    private final RequestMaker requestMaker;

    @Autowired
    public ContainerProjectRunner(RequestMaker requestMaker)
    {
        this.requestMaker = requestMaker;
    }

    @Override
    public void run(Runner runner, ContainerEntity container)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try
        {
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(
                    assembleRequest(runner).toString(),
                    headers);
            requestMaker.makePostRequest(container.getConnectionString(), RequestMethod.EXECUTE_RUNNER, entity);

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
        request.put("selectedModel", project.getSelectedModel());
        request.put("runnerId", runner.getId());
        request.put("hyperParameters", buildHyperParametersJson(runner.getHyperParameters()));
        request.put("classificationLabels", buildClassificationLabelsJson(project.getClassificationLabels()));
        return request;
    }

    private JSONArray buildHyperParametersJson(List<HyperParameter> hyperParameters)
    {
        JSONArray json = new JSONArray();
        for(HyperParameter hyperParameter: hyperParameters)
        {
            try
            {
                json.put(new JSONObject().put("name", hyperParameter.getName()).put("value", hyperParameter.getValue()));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return json;
    }

    private JSONArray buildClassificationLabelsJson(List<ClassificationLabel> classificationLabels)
    {
        JSONArray json = new JSONArray();
        for(ClassificationLabel classificationLabel : classificationLabels)
        {
            try
            {
                json.put(new JSONObject().put("labelName", classificationLabel.getLabelName()).put("folderPath", classificationLabel.getFolderPath()));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return json;
    }
}
