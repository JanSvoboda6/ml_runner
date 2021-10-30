package com.jan.web.runner;

public class Result
{
    private final double validationResultFirstLabel;
    private final double validationResultSecondLabel;

    public Result(double validationResultFirstLabel, double validationResultSecondLabel)
    {
        this.validationResultFirstLabel = validationResultFirstLabel;
        this.validationResultSecondLabel = validationResultSecondLabel;
    }

    public double getValidationResultFirstLabel()
    {
        return validationResultFirstLabel;
    }

    public double getValidationResultSecondLabel()
    {
        return validationResultSecondLabel;
    }
}
