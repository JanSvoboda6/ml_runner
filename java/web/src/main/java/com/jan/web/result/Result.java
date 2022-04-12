package com.jan.web.result;

import com.jan.web.runner.Runner;

import javax.persistence.*;
import java.util.List;

@Entity
public class Result
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Runner runner;

    private double accuracy;

    @Column(length = 2000)
    private String resultText;

//    private ClassificationReport classificationReport;
//    private ConfusionMatrix confusionMatrix;
//    private List<ClassAccuracy> accuracyPerClass;

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
