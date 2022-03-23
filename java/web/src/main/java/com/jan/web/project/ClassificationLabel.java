package com.jan.web.project;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "classification_label")
public class ClassificationLabel
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classification_label_seq")
    @SequenceGenerator(name = "classification_label_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    private String labelName;

    @NotBlank
    private String folderPath;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLabelName()
    {
        return labelName;
    }

    public void setLabelName(String className)
    {
        this.labelName = className;
    }

    public String getFolderPath()
    {
        return folderPath;
    }

    public void setFolderPath(String classPath)
    {
        this.folderPath = classPath;
    }
}