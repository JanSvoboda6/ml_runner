package com.jan.web.runner.scheduling;

import com.jan.web.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Providing access to {@link RunnerQueueEntity}.
 */
public interface RunnerQueueRepository extends JpaRepository<RunnerQueueEntity, Long>
{
    List<RunnerQueueEntity> getAllByUser(User user);
    void deleteByRunnerId(long runnerId);
}
