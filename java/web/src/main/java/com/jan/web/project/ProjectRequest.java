package com.jan.web.project;

import javax.validation.constraints.NotBlank;

public class ProjectRequest
{
    @NotBlank
    private String projectName;

    @NotBlank
    private String firstLabel;

    @NotBlank
    private String secondLabel;

    @NotBlank
    private String firstLabelFolder;

    @NotBlank
    private String secondLabelFolder;

    @NotBlank
    private String selectedModel;


    public String getProjectName()
    {
        return projectName;
    }

    public void setModelName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getFirstLabel()
    {
        return firstLabel;
    }

    public void setFirstLabel(String firstLabel)
    {
        this.firstLabel = firstLabel;
    }

    public String getSecondLabel()
    {
        return secondLabel;
    }

    public void setSecondLabel(String secondLabel)
    {
        this.secondLabel = secondLabel;
    }

    public String getFirstLabelFolder()
    {
        return firstLabelFolder;
    }

    public void setFirstLabelFolder(String firstLabelFolder)
    {
        this.firstLabelFolder = firstLabelFolder;
    }

    public String getSecondLabelFolder()
    {
        return secondLabelFolder;
    }

    public void setSecondLabelFolder(String secondLabelFolder)
    {
        this.secondLabelFolder = secondLabelFolder;
    }

    public String getSelectedModel()
    {
        return selectedModel;
    }

    public void setSelectedModel(String selectedModel)
    {
        this.selectedModel = selectedModel;
    }
}
