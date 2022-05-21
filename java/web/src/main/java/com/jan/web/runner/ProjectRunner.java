package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;

/**
 * Interface providing a run method for execution of a {@link Runner}.
 */
public interface ProjectRunner
{
    void run(Runner runner, ContainerEntity container);
}
