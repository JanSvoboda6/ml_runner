package com.jan.web.runner;

public class FinishedRequest
{
    private final long projectId;
    private final long runnerId;

    public FinishedRequest(long projectId, long runnerId)
    {
        this.projectId = projectId;
        this.runnerId = runnerId;
    }

    public long getProjectId()
    {
        return projectId;
    }
    public long getRunnerId()
    {
        return runnerId;
    }
}
