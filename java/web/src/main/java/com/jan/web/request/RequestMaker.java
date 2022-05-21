package com.jan.web.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * Interface providing methods for making requests.
 */
public interface RequestMaker
{
    ResponseEntity<String> makePostRequest(String connectionString, RequestMethod requestMethod, HttpEntity<String> requestEntity);
    ResponseEntity<byte[]> downloadRequest(String connectionString, RequestMethod requestMethod, HttpEntity<String> requestEntity);
}
