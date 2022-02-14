package com.jan.web.runner;

public class RunRequest
{
    private long projectId;
    private double gammaParameter;
    private double cParameter;

    public long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(long projectId)
    {
        this.projectId = projectId;
    }

    public double getGammaParameter()
    {
        return gammaParameter;
    }

    public void setGammaParameter(double gammaParameter)
    {
        this.gammaParameter = gammaParameter;
    }

    public double getcParameter()
    {
        return cParameter;
    }

    public void setcParameter(double cParameter)
    {
        this.cParameter = cParameter;
    }
}
