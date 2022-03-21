package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.result.Result;
import org.json.JSONException;

import java.io.IOException;
import java.util.Optional;

public interface RunnerService
{
   void runProject(RunRequest request, Project project, ContainerEntity containerEntity);
   boolean isFinished(long containerId, long projectId, long runnerId) throws IOException, JSONException;
   Optional<Result> getResult(long containerId, long projectId, long runnerId) throws IOException, JSONException;
}
