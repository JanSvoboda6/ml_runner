package com.jan.web.runner.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jan.web.runner.Runner;

import java.util.List;

/**
 * Represents a response of a container with {@link Runner} statuses in a chronological order.
 */
public class StatusResponse
{
    @JsonProperty("chronologicalStatuses")
    public List<String> chronologicalStatuses;
}
