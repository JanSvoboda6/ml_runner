package com.jan.web.runner;

import com.jan.web.project.Project;

import javax.persistence.*;

@Entity
public class Runner
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Project project;
    private double gammaParameter;
    private double cParameter;
    private boolean isFinished;

    public Long getId()
    {
        return id;
    }

    public double getGammaParameter()
    {
        return gammaParameter;
    }

    public void setGammaParameter(double gammaParameter)
    {
        this.gammaParameter = gammaParameter;
    }

    public double getCParameter()
    {
        return cParameter;
    }

    public void setCParameter(double cParameter)
    {
        this.cParameter = cParameter;
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public void setFinished(boolean finished)
    {
        isFinished = finished;
    }
}
