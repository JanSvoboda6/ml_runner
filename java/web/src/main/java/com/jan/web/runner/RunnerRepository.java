package com.jan.web.runner;

import com.jan.web.runner.status.RunnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunnerRepository extends JpaRepository<Runner, Long>
{
    List<Runner> findAllByProjectId(long projectId);
    Runner findRunnerByIdAndProjectId(long id, long projectId);
    Runner findByProjectIdAndStatusIs(long projectId, RunnerStatus status);
    List<Runner> findAllByProjectIdAndStatusIsNot(long projectId, RunnerStatus status);
}
