package com.jan.web.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface RequestMaker
{
    ResponseEntity<String> makePostRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity);
    ResponseEntity<byte[]> downloadRequest(long portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity);
}
