package com.jan.web.runner;

import com.jan.web.Project;
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

    @Autowired
    public ContainerProjectRunner(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public Result run(Project project)
    {
//        ProcessBuilder processBuilder = new ProcessBuilder("python3", "/Users/jan/dev/thesis/ml_runner/python/model.py");
//        processBuilder.redirectErrorStream(true);
//        double validationResultFirstLabel = 0;
//        double validationResultSecondLabel = 0;
//        try
//        {
//            Process process = processBuilder.start();
//            String output = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
//            process.waitFor();
//            String[] parsedOutput = output.split("\\s+");
//            validationResultFirstLabel = Double.parseDouble(parsedOutput[0]);
//            validationResultSecondLabel = Double.parseDouble(parsedOutput[1]);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        return new Result(validationResultFirstLabel, validationResultSecondLabel);
        try
        {
            JSONObject request = new JSONObject();
            request.put("name", project.getName());
            request.put("firstLabel", project.getFirstLabel());
            request.put("secondLabel", project.getSecondLabel());
            request.put("firstLabelFolder", project.getFirstLabelFolder());
            request.put("secondLabelFolder", project.getSecondLabelFolder());
            request.put("selectedModel", project.getSelectedModel());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);

            ResponseEntity<String> response = restTemplate
                    .exchange("http://localhost:9999" + "/runproject", HttpMethod.POST, entity, String.class);

            response.getStatusCode();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  new Result(99.0, 99.0);
    }


    @Override
    public boolean stop(Project project)
    {
        return false;
    }
}
