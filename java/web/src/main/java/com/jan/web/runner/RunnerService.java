package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.project.Project;
import com.jan.web.runner.parameter.HyperParameter;
import com.jan.web.runner.result.Result;
import com.jan.web.runner.status.RunnerStatus;
import com.jan.web.security.user.User;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Interface proving methods for execution of a {@link Runner} of {@link Project},
 * monitoring execution status and providing results.
 */
public interface RunnerService
{
   void runProject(List<HyperParameter> hyperParameters, Project project, ContainerEntity containerEntity);
   RunnerStatus getStatus(long containerId, long runnerId) throws JSONException, IOException;
   Optional<Result> getResult(long containerId, long projectId, long runnerId) throws IOException, JSONException;
   boolean isAnyRunnerRunning(User user);
}
