package com.jan.web.project;

import com.jan.web.project.label.ClassificationLabel;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Request class used in {@link ProjectController}.
 */
public class ProjectRequest
{
    @NotBlank
    private String projectName;

    @NotBlank
    private String selectedModel;

    @NotBlank
    private List<ClassificationLabel> classificationLabels;

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

    public List<ClassificationLabel> getClassificationLabels()
    {
        return classificationLabels;
    }

    public void setClassificationLabels(List<ClassificationLabel> classificationLabels)
    {
        this.classificationLabels = classificationLabels;
    }

}
