package com.jan.web.runner;

import com.jan.web.Project;

public interface ProjectRunner
{
    void run(Runner runner) throws InterruptedException;
    boolean stop(Runner runner);
}
