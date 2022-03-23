package com.jan.web.project;

import com.jan.web.security.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
public class Project
{
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    private String name;
    private String firstLabel;
    private String secondLabel;
    private String firstLabelFolder;
    private String secondLabelFolder;
    private String selectedModel;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ClassificationLabel> classificationLabels;

    public Project(User user, String name, String firstLabel, String secondLabel, String firstLabelFolder, String secondLabelFolder, String selectedModel, List<ClassificationLabel> classificationLabels)
    {
        this.user = user;
        this.name = name;
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.firstLabelFolder = firstLabelFolder;
        this.secondLabelFolder = secondLabelFolder;
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

    public String getFirstLabel()
    {
        return firstLabel;
    }

    public String getSecondLabel()
    {
        return secondLabel;
    }

    public String getFirstLabelFolder()
    {
        return firstLabelFolder;
    }

    public String getSecondLabelFolder()
    {
        return secondLabelFolder;
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