package com.jan.web.fileservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FileResponse
{
    @JsonProperty("directories")
    public List<String> directories;

    @JsonProperty("files")
    public List<FileInformationJSON> files;
}
