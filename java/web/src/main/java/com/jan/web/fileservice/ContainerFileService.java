package com.jan.web.fileservice;

import com.jan.web.request.ContainerRequestMaker;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ContainerFileService implements FileService
{
    private final ContainerRepository repository;
    private final RequestMaker requestMaker;

    @Autowired
    public ContainerFileService(ContainerRepository repository, RequestMaker requestMaker)
    {
        this.repository = repository;
        this.requestMaker = requestMaker;
    }

    @Override
    public List<FileInformation> getAllFiles(long containerId)
    {
        StringBuilder result = new StringBuilder();
        try
        {
            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            if(containerEntity.isPresent())
            {
                URL url = new URL(composeUrl(containerEntity.get().getId(), RequestMethod.GET_FILES.getRequestUrl()));
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

    public void uploadFiles(Keys keys, List<MultipartFile> files, long containerId)
    {
        if(keys.getKeys().size() != files.size())
        {
            return;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        Optional<ContainerEntity> containerEntity = repository.findById(containerId);
        if(containerEntity.isPresent())
        {
            HttpPost uploadFile = new HttpPost(composeUrl(containerEntity.get().getId(), RequestMethod.UPLOAD_FILES.getRequestUrl()));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try
            {
                for (int i = 0; i < keys.getKeys().size(); i++)
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
    }

    @Override
    public void createDirectory(String key, long containerId)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("key", key);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);

            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getId(),
                    RequestMethod.CREATE_DIRECTORY,
                    entity));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFolders(List<String> keys, long containerId)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("keys", keys);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);

            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getId(),
                    RequestMethod.BATCH_DELETE_FOLDERS,
                    entity));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private String composeUrl(long port, String requestMethod)
    {
        return ContainerRequestMaker.CONTAINER_BASE_URL + port + requestMethod;
    }
}
