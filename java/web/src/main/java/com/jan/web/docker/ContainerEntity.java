package com.jan.web.docker;

import com.jan.web.security.user.User;

import javax.persistence.*;

@Entity
@Table(name = "containers")
public class ContainerEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_post")
    @SequenceGenerator(name = "seq_post", initialValue = 20000, allocationSize = 1)
    private Long id;

    @OneToOne
    private User user;

    private boolean isRunning;

    public ContainerEntity()
    {

    }

    public ContainerEntity(User user)
    {
        this.user = user;
    }

    public ContainerEntity(Long id, User user)
    {
        this.id = id;
        this.user = user;
    }

    public Long getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setRunning(boolean running)
    {
        isRunning = running;
    }
}
