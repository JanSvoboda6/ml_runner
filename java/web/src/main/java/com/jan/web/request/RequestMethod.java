package com.jan.web.request;

public enum RequestMethod
{
    RUN_PROJECT("/runproject"),
    CREATE_DIRECTORY("/createdirectory"),
    BATCH_DELETE_FOLDERS("/folders/delete"),
    BATCH_DELETE_FILES("/files/delete"),
    IS_RUNNER_FINISHED("/project/runner/finished"),
    RUNNER_RESULT("/project/runner/result"),
    GET_FILES("/getfiles"),
    UPLOAD_FILES("/upload"),
    MOVE_FILE("/files/move");

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
