package com.jan.web.runner;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StatusResponse
{
    @JsonProperty("chronologicalStatuses")
    public List<String> chronologicalStatuses;
}
