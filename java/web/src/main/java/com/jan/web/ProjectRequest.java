package com.jan.web;

import javax.validation.constraints.NotBlank;

public class ProjectRequest
{
    @NotBlank
    private String projectName;

    @NotBlank
    private String selectedModel;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
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
