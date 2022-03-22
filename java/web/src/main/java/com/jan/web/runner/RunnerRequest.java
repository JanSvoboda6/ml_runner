package com.jan.web.runner;

public class RunnerRequest
{
    private final long runnerId;

    public RunnerRequest(long runnerId)
    {
        this.runnerId = runnerId;
    }

    public long getRunnerId()
    {
        return runnerId;
    }
}
