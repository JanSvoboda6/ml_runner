package com.jan.web.runner.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent a response of a container. It provides text (logs) and an accuracy metric.
 */
public class ResultResponse
{
    @JsonProperty("resultText")
    public String resultText;

    @JsonProperty("accuracy")
    public double accuracy;
}
