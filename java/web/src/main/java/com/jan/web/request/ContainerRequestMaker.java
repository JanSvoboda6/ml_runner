package com.jan.web.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ContainerRequestMaker implements RequestMaker
{
    public static final String CONTAINER_BASE_URL = "http://localhost:";
    private final RestTemplate restTemplate;

    public ContainerRequestMaker(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> makePostRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity)
    {
        return restTemplate.exchange(
                composeUrl(portNumber, requestMethod.getRequestUrl()),
                HttpMethod.POST,
                requestEntity,
                String.class);
    }

    @Override
    public void downloadRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity){
        try
        {
            var response = restTemplate.exchange(composeUrl(portNumber, requestMethod.getRequestUrl()), HttpMethod.POST, requestEntity, byte[].class);
            Files.write(Paths.get("test_zip_file.zip"), response.getBody());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

//        RequestCallback requestCallback = request -> request
//                .getHeaders()
//                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
//
//        // Streams the response instead of loading it all in memory
//        ResponseExtractor<Void> responseExtractor = response -> {
//            // Here you can write the inputstream to a file or any other place
//            Path path = Paths.get("downloadv3.jpg");
//            Files.copy(response.getBody(), path);
//            return null;
//        };
//        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    private String composeUrl(long portNumber, String requestUrl)
    {
        return CONTAINER_BASE_URL + portNumber + requestUrl;
    }

}
