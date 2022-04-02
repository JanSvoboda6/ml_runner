package com.jan.web.runner;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultResponse
{
    @JsonProperty("firstLabelResult")
    public double firstLabelResult;

    @JsonProperty("secondLabelResult")
    public double secondLabelResult;

    @JsonProperty("accuracy")
    public double accuracy;
}
