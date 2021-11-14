package com.jan.web.fileservice;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContainerFileService implements FileService
{
    private final RestTemplate restTemplate;

    @Autowired
    public ContainerFileService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<FileInformation> getAllFiles()
    {
        StringBuilder result = new StringBuilder();
        try
        {
            URL url = new URL("http://localhost:9999/getfiles");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                for (String line; (line = reader.readLine()) != null; )
                {
                    result.append(line);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<FileInformation> fileInformationList = new ArrayList<>();
        try
        {
            FileResponse response = mapper.readValue(result.toString(), FileResponse.class);
            response.directories.forEach(directoryName -> fileInformationList.add(new FileInformation(directoryName)));
            response.files.forEach(file -> fileInformationList.add(new FileInformation(file.key, file.size, (long)file.modified)));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return fileInformationList;
    }

    public void uploadFiles(Keys keys, List<MultipartFile> files)
    {
        if(keys.getKeys().size() != files.size())
        {
            return;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://localhost:9999" + "/upload");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("Files", "Hello Docker!", ContentType.TEXT_PLAIN);
        try
        {
            for(int i = 0; i < keys.getKeys().size(); i++)
            {
                builder.addBinaryBody(
                        keys.getKeys().get(i),
                        files.get(i).getBytes(),
                        ContentType.DEFAULT_BINARY,
                        files.get(i).getName());
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        CloseableHttpResponse response = null;
        try
        {
            response = httpClient.execute(uploadFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void createDirectory(String key)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("key", key);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);

            ResponseEntity<String> response = restTemplate
                    .exchange("http://localhost:9999" + "/createdirectory", HttpMethod.POST, entity, String.class);

            response.getStatusCode();
            //JSONObject userJson = new JSONObject(response.getBody());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
