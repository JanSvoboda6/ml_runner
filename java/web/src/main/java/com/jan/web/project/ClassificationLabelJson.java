package com.jan.web.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassificationLabelJson
{
    @JsonProperty("labelName")
    public String labelName;

    @JsonProperty("folderPath")
    public String folderPath;

    public static ClassificationLabelJson fromClassificationLabel(ClassificationLabel label)
    {
        ClassificationLabelJson json = new ClassificationLabelJson();
        json.labelName = label.getLabelName();
        json.folderPath = label.getFolderPath();
        return json;
    }
}
