package com.jan.web.project;

import com.jan.web.project.label.ClassificationLabel;
import com.jan.web.security.user.User;

import javax.persistence.*;
import java.util.List;

/**
 * Entity class representing a project.
 */
@Entity
public class Project
{
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;
    private String name;
    private String selectedModel;

    @OneToMany
    private List<ClassificationLabel> classificationLabels;

    public Project(User user, String name, String selectedModel, List<ClassificationLabel> classificationLabels)
    {
        this.user = user;
        this.name = name;
        this.selectedModel = selectedModel;
        this.classificationLabels = classificationLabels;
    }

    public Project()
    {
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getSelectedModel()
    {
        return selectedModel;
    }

    public User getUser()
    {
        return user;
    }

    public List<ClassificationLabel> getClassificationLabels()
    {
        return classificationLabels;
    }

    public void setClassificationLabels(List<ClassificationLabel> classificationLabels)
    {
        this.classificationLabels = classificationLabels;
    }
}