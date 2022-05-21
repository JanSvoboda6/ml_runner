package com.jan.web.runner.status;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StatusResponse
{
    @JsonProperty("chronologicalStatuses")
    public List<String> chronologicalStatuses;
}
