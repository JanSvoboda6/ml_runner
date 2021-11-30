package com.jan.web.docker;

import com.jan.web.security.user.User;

import javax.persistence.*;

@Entity
@Table(name = "containers")
@SequenceGenerator(name="host_port_sequence", initialValue=20000, allocationSize=1)
public class ContainerEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="host_port_sequence")
    private int hostPort;

    private boolean isRunning;

    public ContainerEntity()
    {

    }

    public ContainerEntity(User user)
    {
        this.user = user;
    }

    public ContainerEntity(User user, int hostPort)
    {
        this.user = user;
        this.hostPort = hostPort;
    }

    public Long getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public int getHostPort()
    {
        return hostPort;
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
