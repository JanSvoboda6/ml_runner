package com.jan.web.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jan.web.file.information.FileInformationJSON;

import java.util.List;

public class FileResponse
{
    @JsonProperty("directories")
    public List<String> directories;

    @JsonProperty("files")
    public List<FileInformationJSON> files;
}
