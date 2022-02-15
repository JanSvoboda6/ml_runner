package com.jan.web.result;

import com.jan.web.runner.Runner;

import javax.persistence.*;

@Entity
public class Result
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Runner runner;

    private double firstLabelResult;
    private double secondLabelResult;

    public Long getId()
    {
        return id;
    }

    public Runner getRunner()
    {
        return runner;
    }

    public void setRunner(Runner runner)
    {
        this.runner = runner;
    }

    public double getFirstLabelResult()
    {
        return firstLabelResult;
    }

    public void setFirstLabelResult(double firstLabelResult)
    {
        this.firstLabelResult = firstLabelResult;
    }

    public double getSecondLabelResult()
    {
        return secondLabelResult;
    }

    public void setSecondLabelResult(double secondLabelResult)
    {
        this.secondLabelResult = secondLabelResult;
    }
}
