package com.jan.web;

import javax.validation.constraints.NotBlank;

public class ModelRequest
{
    @NotBlank
    private String modelName;

    @NotBlank
    private String selectedModel;

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
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
