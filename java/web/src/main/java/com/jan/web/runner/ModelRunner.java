package com.jan.web.runner;

import com.jan.web.Model;

public interface ModelRunner
{
    Result run(Model model) throws InterruptedException;
    boolean stop(Model model);
}
