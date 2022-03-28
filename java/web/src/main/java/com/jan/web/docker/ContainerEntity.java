package com.jan.web.docker;

import com.jan.web.security.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "container")
public class ContainerEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_post")
    @SequenceGenerator(name = "seq_post", initialValue = 20000, allocationSize = 1)
    private Long id;

    @OneToOne
    private User user;

    @NotBlank
    private String connectionString;

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

    public String getConnectionString()
    {
        return connectionString;
    }

    public void setConnectionString(String connectionString)
    {
        this.connectionString = connectionString;
    }
}
