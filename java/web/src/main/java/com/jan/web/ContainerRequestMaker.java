package com.jan.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    public ResponseEntity<String> makePostRequest(int portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity)
    {
        return restTemplate.exchange(
                composeUrl(portNumber, requestMethod.getRequestUrl()),
                HttpMethod.POST,
                requestEntity,
                String.class);
    }

    private String composeUrl(int portNumber, String requestUrl)
    {
        return CONTAINER_BASE_URL + portNumber + requestUrl;
    }

}
