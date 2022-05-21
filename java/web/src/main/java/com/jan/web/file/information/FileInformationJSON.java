package com.jan.web.file.information;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used for mapping between object and JSON.
 */
public class FileInformationJSON
{
    @JsonProperty("key") public String key;
    @JsonProperty("size") public int size;
    @JsonProperty("modified") public float modified;
}
