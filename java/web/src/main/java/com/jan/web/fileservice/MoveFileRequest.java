package com.jan.web.fileservice;

import javax.validation.constraints.NotBlank;

public class MoveFileRequest
{
    @NotBlank
    private String oldKey;

    @NotBlank
    private String newKey;

    public String getOldKey()
    {
        return oldKey;
    }

    public String getNewKey()
    {
        return newKey;
    }
}
