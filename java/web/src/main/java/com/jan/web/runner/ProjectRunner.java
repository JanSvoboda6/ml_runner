package com.jan.web.runner;

import com.jan.web.Project;

public interface ProjectRunner
{
    void run(Runner runner, long containerId) throws InterruptedException;
    boolean stop(Runner runner);
}
