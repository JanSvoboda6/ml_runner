package com.jan.web.request;

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
    public ResponseEntity<String> makePostRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity)
    {
        return restTemplate.exchange(
                composeUrl(portNumber, requestMethod.getRequestUrl()),
                HttpMethod.POST,
                requestEntity,
                String.class);
    }

    @Override
    public ResponseEntity<byte[]> downloadRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity){
        return restTemplate.exchange(composeUrl(portNumber, requestMethod.getRequestUrl()), HttpMethod.POST, requestEntity, byte[].class);
//            Files.write(Paths.get("test_zip_file.zip"), Objects.requireNonNull(response.getBody()));
    }

    private String composeUrl(long portNumber, String requestUrl)
    {
        return CONTAINER_BASE_URL + portNumber + requestUrl;
    }

}
