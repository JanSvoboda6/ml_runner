package com.jan.web.runner;

import com.jan.web.Project;

public interface ProjectRunner
{
    Result run(Project project) throws InterruptedException;
    boolean stop(Project project);
}
