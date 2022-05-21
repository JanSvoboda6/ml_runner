package com.jan.web.runner.scheduling;

import com.jan.web.runner.Runner;
import com.jan.web.security.user.User;

import javax.persistence.*;

@Entity
public class RunnerQueueEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "runner_id")
    private Runner runner;

    public RunnerQueueEntity(User user, Runner runner)
    {
        this.user = user;
        this.runner = runner;
    }

    public RunnerQueueEntity()
    {

    }

    public Runner getRunner()
    {
        return runner;
    }

    public User getUser()
    {
        return user;
    }

    public long getId()
    {
        return id;
    }
}
