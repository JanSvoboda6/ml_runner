package com.jan.web.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jan.web.file.information.FileInformationJSON;

import java.util.List;

/**
 * Class for mapping JSON to object when container responds with list of folders (directories)/files.
 */
public class FileResponse
{
    @JsonProperty("directories")
    public List<String> directories;

    @JsonProperty("files")
    public List<FileInformationJSON> files;
}
