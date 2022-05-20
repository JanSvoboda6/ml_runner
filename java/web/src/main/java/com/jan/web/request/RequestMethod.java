package com.jan.web.request;

public enum RequestMethod
{
    EXECUTE_RUNNER("/runner/execute"),
    RUNNER_STATUS("/runner/status"),
    CREATE_FOLDER("/folders/create"),
    BATCH_DELETE_FOLDERS("/folders/delete"),
    BATCH_DELETE_FILES("/files/delete"),
    RUNNER_RESULT("/runner/result"),
    GET_FILES("/files"),
    UPLOAD_FILES("/upload"),
    MOVE_FILE("/files/move"),
    MOVE_FOLDER("/folders/move"),
    DOWNLOAD("/download");

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
