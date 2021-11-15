package com.jan.web.runner;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinishedResponse
{
    @JsonProperty("isFinished")
    public boolean isFinished;
}
