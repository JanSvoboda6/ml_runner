package com.jan.web.runner;

public interface ProjectRunner
{
    void run(Runner runner, long containerId);
    boolean stop(Runner runner);
}
