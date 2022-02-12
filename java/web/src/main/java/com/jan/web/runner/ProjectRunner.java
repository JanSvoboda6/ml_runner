package com.jan.web.runner;

public interface ProjectRunner
{
    void run(Runner runner, long containerId) throws InterruptedException;
    boolean stop(Runner runner);
}
