package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.result.Result;
import com.jan.web.security.user.User;
import org.json.JSONException;

import java.io.IOException;
import java.util.Optional;

public interface RunnerService
{
   void runProject(RunRequest request, Project project, ContainerEntity containerEntity);
   RunnerStatus getStatus(long containerId, long runnerId) throws JSONException, IOException;
   Optional<Result> getResult(long containerId, long projectId, long runnerId) throws IOException, JSONException;
   boolean isAnyRunnerRunning(User user);
}
