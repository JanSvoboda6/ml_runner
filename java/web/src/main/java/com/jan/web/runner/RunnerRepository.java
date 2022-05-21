package com.jan.web.runner;

import com.jan.web.runner.status.RunnerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for providing access to {@link Runner}.
 */
public interface RunnerRepository extends JpaRepository<Runner, Long>
{
    List<Runner> findAllByProjectId(long projectId);
    List<Runner> findAllByProjectIdAndStatusIsNot(long projectId, RunnerStatus status);
}
