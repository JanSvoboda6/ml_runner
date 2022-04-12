package com.jan.web.runner;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultResponse
{
    @JsonProperty("resultText")
    public String resultText;

    @JsonProperty("accuracy")
    public double accuracy;
}
