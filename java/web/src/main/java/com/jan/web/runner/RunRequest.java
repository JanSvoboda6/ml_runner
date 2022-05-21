package com.jan.web.runner;

import com.jan.web.runner.parameter.HyperParameter;

import java.util.List;

/**
 * Request for execution of {@link Runner} used in {@link RunnerController}.
 */
public class RunRequest
{
    private final long projectId;
    private final double gammaParameter;
    private final double cParameter;
    private final List<HyperParameter> hyperParameters;

    public RunRequest(long projectId, double gammaParameter, double cParameter, List<HyperParameter> hyperParameters)
    {
        this.projectId = projectId;
        this.gammaParameter = gammaParameter;
        this.cParameter = cParameter;
        this.hyperParameters = hyperParameters;
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

    public List<HyperParameter> getHyperParameters()
    {
        return hyperParameters;
    }
}
