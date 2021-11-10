package com.jan.web.fileservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileInformationJSON
{
    @JsonProperty("key") public String key;
    @JsonProperty("size") public int size;
    @JsonProperty("modified") public float modified;
}
