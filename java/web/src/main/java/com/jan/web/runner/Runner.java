package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.runner.parameter.HyperParameter;
import com.jan.web.runner.status.RunnerStatus;

import javax.persistence.*;
import java.util.List;

/**
 * Entity class representing a run of a {@link Project}.
 */
@Entity
public class Runner
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Project project;

    @Enumerated(EnumType.STRING)
    private RunnerStatus status;
    private String chronologicalStatuses;

    @OneToMany
    private List<HyperParameter> hyperParameters;

    private long timestamp;

    public Long getId()
    {
        return id;
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public RunnerStatus getStatus()
    {
        return status;
    }

    public void setStatus(RunnerStatus status)
    {
        this.status = status;
    }

    public String getChronologicalStatuses()
    {
        return chronologicalStatuses;
    }

    public void setChronologicalStatuses(String chronologicalStatuses)
    {
        this.chronologicalStatuses = chronologicalStatuses;
    }

    public List<HyperParameter> getHyperParameters()
    {
        return hyperParameters;
    }

    public void setHyperParameters(List<HyperParameter> hyperParameters)
    {
        this.hyperParameters = hyperParameters;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
