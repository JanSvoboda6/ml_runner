package com.jan.web;

import com.jan.web.security.user.User;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Project
{
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private User user;
    private String name;
    private String firstLabel;
    private String secondLabel;
    private String firstLabelFolder;
    private String secondLabelFolder;
    private String selectedModel;

    public Project(User user, String name, String firstLabel, String secondLabel, String firstLabelFolder, String secondLabelFolder, String selectedModel)
    {
        this.user = user;
        this.name = name;
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.firstLabelFolder = firstLabelFolder;
        this.secondLabelFolder = secondLabelFolder;
        this.selectedModel = selectedModel;
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
}