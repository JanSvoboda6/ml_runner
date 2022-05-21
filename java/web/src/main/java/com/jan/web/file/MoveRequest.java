package com.jan.web.file;

import javax.validation.constraints.NotBlank;

/**
 * Request for a file/folder moving (renaming).
 */
public class MoveRequest
{
    @NotBlank
    private final String oldKey;

    @NotBlank
    private final String newKey;

    public MoveRequest(String oldKey, String newKey)
    {
        this.oldKey = oldKey;
        this.newKey = newKey;
    }

    public String getOldKey()
    {
        return oldKey;
    }

    public String getNewKey()
    {
        return newKey;
    }
}
