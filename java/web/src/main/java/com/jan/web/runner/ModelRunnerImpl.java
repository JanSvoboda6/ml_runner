package com.jan.web.runner;

import com.jan.web.Model;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class ModelRunnerImpl implements ModelRunner
{
    @Override
    public Result run(Model model) throws InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "/Users/jan/dev/thesis/ml_runner/python/model.py");
        processBuilder.redirectErrorStream(true);
        double validationResultFirstLabel = 0;
        double validationResultSecondLabel = 0;
        try
        {
            Process process = processBuilder.start();
            String output = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
            process.waitFor();
            String[] parsedOutput = output.split("\\s+");
            validationResultFirstLabel = Double.parseDouble(parsedOutput[0]);
            validationResultSecondLabel = Double.parseDouble(parsedOutput[1]);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return new Result(validationResultFirstLabel, validationResultSecondLabel);
    }

    @Override
    public boolean stop(Model model)
    {
        return false;
    }
}
