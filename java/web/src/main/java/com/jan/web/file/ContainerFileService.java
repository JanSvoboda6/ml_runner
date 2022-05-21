package com.jan.web.file;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.file.information.FileInformation;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class communicating with a container and providing the dataset (file/folder) manipulation functionality.
 */
@Service
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
                URL url = new URL(containerEntity.get().getConnectionString() + RequestMethod.GET_FILES.getRequestUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
                {
                    for (String line; (line = reader.readLine()) != null; )
                    {
                        result.append(line);
                    }
                } catch (IOException exception)
                {
                    throw new RuntimeException(exception);
                }
            }
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<FileInformation> fileInformationList = new ArrayList<>();
        try
        {
            FileResponse response = mapper.readValue(result.toString(), FileResponse.class);
            response.directories.forEach(folderName -> fileInformationList.add(new FileInformation(folderName)));
            response.files.forEach(file -> fileInformationList.add(new FileInformation(file.key, file.size, (long)file.modified)));
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        return fileInformationList;
    }

    public void uploadFiles(List<String> keys, List<MultipartFile> files, long containerId)
    {
        if(keys.size() != files.size())
        {
            throw new RuntimeException("Keys and files size are not equal!");
        }
        Optional<ContainerEntity> containerEntity = repository.findById(containerId);
        if(containerEntity.isPresent())
        {
            HttpPost uploadFileToContainerRequest = new HttpPost(containerEntity.get().getConnectionString() + RequestMethod.UPLOAD_FILES.getRequestUrl());
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try
            {
                for (int i = 0; i < keys.size(); i++)
                {
                    builder.addBinaryBody(
                            keys.get(i),
                            files.get(i).getBytes(),
                            ContentType.DEFAULT_BINARY,
                            files.get(i).getName());
                }
            }
            catch (Exception exception)
            {
                throw new RuntimeException(exception);
            }

            uploadFileToContainerRequest.setEntity(builder.build());
            CloseableHttpClient httpClient = HttpClients.createDefault();
            try
            {
                httpClient.execute(uploadFileToContainerRequest);
            }
            catch (IOException exception)
            {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public void createFolder(String key, long containerId)
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
                    container.getConnectionString(),
                    RequestMethod.CREATE_FOLDER,
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
            HttpEntity<String> entity = constructHttpEntityForDeleteRequest(keys);
            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getConnectionString(),
                    RequestMethod.BATCH_DELETE_FOLDERS,
                    entity));
        } catch (JSONException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void deleteFiles(List<String> keys, long containerId)
    {
        try
        {
            HttpEntity<String> entity = constructHttpEntityForDeleteRequest(keys);
            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getConnectionString(),
                    RequestMethod.BATCH_DELETE_FILES,
                    entity));
        } catch (JSONException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void moveFile(String oldKey, String newKey, long containerId)
    {
        try
        {
            HttpEntity<String> entity = constructHttpEntityForMoveRequest(oldKey, newKey);
            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getConnectionString(),
                    RequestMethod.MOVE_FILE,
                    entity));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void moveFolder(String oldKey, String newKey, long containerId)
    {
        try
        {
            HttpEntity<String> entity = constructHttpEntityForMoveRequest(oldKey, newKey);

            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            containerEntity.ifPresent(container -> requestMaker.makePostRequest(
                    container.getConnectionString(),
                    RequestMethod.MOVE_FOLDER,
                    entity));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public ResponseEntity<byte[]> download(List<String> keys, long containerId)
    {
        try
        {
            JSONObject request = new JSONObject();
            request.put("keys", keys);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(request.toString(), headers);

            Optional<ContainerEntity> containerEntity = repository.findById(containerId);
            return requestMaker.downloadRequest(containerEntity.get().getConnectionString(), RequestMethod.DOWNLOAD, entity);
        } catch (JSONException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private HttpEntity<String> constructHttpEntityForDeleteRequest(List<String> keys) throws JSONException
    {
        JSONObject request = new JSONObject();
        request.put("keys", keys);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request.toString(), headers);
    }

    private HttpEntity<String> constructHttpEntityForMoveRequest(String oldKey, String newKey) throws JSONException
    {
        JSONObject request = new JSONObject();
        request.put("key", oldKey);
        request.put("newKey", newKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request.toString(), headers);
    }
}
