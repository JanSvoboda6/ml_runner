package com.jan.web;

public enum RequestMethod
{
    RUN_PROJECT("/runproject"),
    CREATE_DIRECTORY("/createdirectory")
    ;

    private final String requestUrl;

    RequestMethod(String requestUrl)
    {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl()
    {
        return requestUrl;
    }
}
