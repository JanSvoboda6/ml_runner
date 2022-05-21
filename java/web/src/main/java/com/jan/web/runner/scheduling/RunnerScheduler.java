package com.jan.web.runner.scheduling;

import com.jan.web.docker.ContainerRepository;
import com.jan.web.runner.ProjectRunner;
import com.jan.web.runner.Runner;
import com.jan.web.runner.RunnerService;
import com.jan.web.runner.status.RunnerStatus;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Schedules a {@link Runner}. If no {@link Runner} is running at the moment then new {@Runner} could be started.
 */
@Service
public class RunnerScheduler
{
    private final RunnerService runnerService;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;
    private final ProjectRunner projectRunner;
    private final RunnerQueueRepository runnerQueueRepository;

    public RunnerScheduler(RunnerService runnerService,
                           ContainerRepository containerRepository,
                           UserRepository userRepository,
                           ProjectRunner projectRunner,
                           RunnerQueueRepository runnerQueueRepository)
    {
        this.runnerService = runnerService;
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
        this.projectRunner = projectRunner;
        this.runnerQueueRepository = runnerQueueRepository;
    }

    @Scheduled(fixedDelay = 10 * 1000)
    @Transactional
    public void scheduleRunning()
    {
        List<User> users = userRepository.findAll();
        for(User user: users)
        {
            if (runnerService.isAnyRunnerRunning(user))
            {
                break;
            }
            List<Runner> runnersToBeStarted = new ArrayList<>();

            List<RunnerQueueEntity> runnerQueueEntities = runnerQueueRepository.getAllByUser(user);

            runnerQueueEntities.forEach(runnerQueueEntity -> runnersToBeStarted.add(runnerQueueEntity.getRunner()));

            if(!runnersToBeStarted.isEmpty())
            {
                runnersToBeStarted.sort(Comparator.comparing(Runner::getTimestamp));
                Runner runner = runnersToBeStarted.get(0);
                runner.setStatus(RunnerStatus.INITIAL);
                runnerQueueRepository.deleteByRunnerId(runner.getId());
                projectRunner.run(runner, containerRepository.findByUserId(runner.getProject().getUser().getId()).get());
            }
        }
    }
}
