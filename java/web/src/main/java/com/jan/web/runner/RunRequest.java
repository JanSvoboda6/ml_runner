package com.jan.web.runner;

public class RunRequest
{
    private final long projectId;
    private final double gammaParameter;
    private final double cParameter;

    public RunRequest(long projectId, double gammaParameter, double cParameter)
    {
        this.projectId = projectId;
        this.gammaParameter = gammaParameter;
        this.cParameter = cParameter;
    }

    public long getProjectId()
    {
        return projectId;
    }

    public double getGammaParameter()
    {
        return gammaParameter;
    }

    public double getCParameter()
    {
        return cParameter;
    }
}
