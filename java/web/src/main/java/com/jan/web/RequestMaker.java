package com.jan.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface RequestMaker
{
    ResponseEntity<String> makePostRequest(int portNumber, RequestMethod requestMethod, HttpEntity<String> requestEntity);
}
