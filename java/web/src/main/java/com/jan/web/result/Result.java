package com.jan.web.result;

import com.jan.web.runner.Runner;

import javax.persistence.*;

@Entity
public class Result
{
    public static final int RESULT_TEXT_MAXIMUM_LENGTH = 5000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Runner runner;

    private double accuracy;

    @Column(length = RESULT_TEXT_MAXIMUM_LENGTH)
    private String resultText;

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

    public double getAccuracy()
    {
        return accuracy;
    }

    public void setAccuracy(double accuracy)
    {
        this.accuracy = accuracy;
    }

    public String getResultText()
    {
        return resultText;
    }

    public void setResultText(String resultText)
    {
        this.resultText = resultText;
    }
}
