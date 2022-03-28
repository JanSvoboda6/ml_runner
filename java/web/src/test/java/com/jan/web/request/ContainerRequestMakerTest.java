package com.jan.web.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ContainerRequestMakerTest
{
    private ContainerRequestMaker requestMaker;
    private RestTemplate restTemplate;

    @BeforeEach
    public void before()
    {
        restTemplate = Mockito.mock(RestTemplate.class);
        requestMaker = new ContainerRequestMaker(restTemplate);
    }

    @Test
    public void whenRequestIsMade_thenResponseIsReturned()
    {
        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        HttpEntity<String> requestEntity = Mockito.mock(ResponseEntity.class);

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.any(Class.class)))
                .thenReturn(responseEntity);

        Assertions.assertThat(requestMaker.makePostRequest("http://random:9999", RequestMethod.RUN_PROJECT, requestEntity))
                .isEqualTo(responseEntity);
    }

}
