package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;

public interface ProjectRunner
{
    void run(Runner runner, ContainerEntity container);
    boolean stop(Runner runner);
}
