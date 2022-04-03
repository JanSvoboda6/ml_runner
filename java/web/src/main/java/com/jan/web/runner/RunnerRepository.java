package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RunnerRepository extends JpaRepository<Runner, Long>
{
    List<Runner> findAllByProjectId(long projectId);
    Runner findRunnerByIdAndProjectId(long id, long projectId);
    Runner findByProjectIdAndStatusIs(long projectId, RunnerStatus status);
    List<Runner> findAllByProjectIdAndStatusIsNot(long projectId, RunnerStatus status);
}
