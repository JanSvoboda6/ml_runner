package com.jan.web.security.role;

import com.jan.web.security.user.User;

import javax.persistence.*;

/**
 * Represents a role of a {@link User}.
 * Roles could be utilized for authorization.
 */
@Entity
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType name;

    public Role()
    {
    }

    public Role(RoleType name)
    {
        this.name = name;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public RoleType getName()
    {
        return name;
    }

    public void setName(RoleType name)
    {
        this.name = name;
    }
}
