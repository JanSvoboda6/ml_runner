package com.jan.web.runner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunnerRepository extends JpaRepository<Runner, Long>
{
    List<Runner> findAllByProjectId(long projectId);
    Runner findRunnerByIdAndProjectId(long id, long projectId);
}
