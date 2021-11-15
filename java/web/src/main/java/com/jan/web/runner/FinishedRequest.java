package com.jan.web.runner;

public class FinishedRequest
{
    private long projectId;
    private long runnerId;

    public long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(long projectId)
    {
        this.projectId = projectId;
    }

    public long getRunnerId()
    {
        return runnerId;
    }

    public void setRunnerId(long runnerId)
    {
        this.runnerId = runnerId;
    }
}
