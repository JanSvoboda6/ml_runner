package com.jan.web.request;

public enum RequestMethod
{
    RUN_PROJECT("/runproject"),
    CREATE_DIRECTORY("/createdirectory"),
    IS_RUNNER_FINISHED("/project/runner/finished"),
    RUNNER_RESULT("/project/runner/result"),
    GET_FILES("/getfiles"),
    UPLOAD_FILES("/upload");

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
