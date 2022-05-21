package com.jan.web.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Class providing methods for making container request.
 */
@Service
public class ContainerRequestMaker implements RequestMaker
{
    private final RestTemplate restTemplate;

    public ContainerRequestMaker(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> makePostRequest(String connectionString, RequestMethod requestMethod, HttpEntity<String> requestEntity)
    {
        return restTemplate.exchange(
                composeUrl(connectionString, requestMethod.getRequestUrl()),
                HttpMethod.POST,
                requestEntity,
                String.class);
    }

    @Override
    public ResponseEntity<byte[]> downloadRequest(String connectionString, RequestMethod requestMethod, HttpEntity<String> requestEntity)
    {
        return restTemplate.exchange(composeUrl(connectionString, requestMethod.getRequestUrl()), HttpMethod.POST, requestEntity, byte[].class);
    }

    private String composeUrl(String connectionString, String requestUrl)
    {
        return connectionString + requestUrl;
    }
}
