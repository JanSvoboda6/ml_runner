package com.jan.web.runner.parameter;

import javax.persistence.*;

/**
 * Entity class representing a hyperparameter.
 */
@Entity
public class HyperParameter
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private String value;

    public HyperParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public HyperParameter()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
