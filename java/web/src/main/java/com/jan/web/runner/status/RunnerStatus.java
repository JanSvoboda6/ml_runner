package com.jan.web.runner.status;

import com.jan.web.runner.Runner;

/**
 * Represent a status (state) of {@link Runner} execution.
 */
public enum RunnerStatus
{
    INITIAL(false),
    PREPARING_DATA(false),
    TRAINING(false),
    PREDICTING(false),
    FINISHED(true),
    FAILED(true),
    CANCELLED(true),
    SCHEDULED(false);

    private final boolean isEndState;

    RunnerStatus(boolean isEndState)
    {
        this.isEndState = isEndState;
    }

    public boolean isEndState()
    {
        return isEndState;
    }
}
