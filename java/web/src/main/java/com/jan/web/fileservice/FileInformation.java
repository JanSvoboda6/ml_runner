package com.jan.web.fileservice;

public class FileInformation
{
    private final String key;
    private final long size;
    private final long modified;

    public FileInformation(String key, long size, long modified)
    {
        this.key = key;
        this.size = size;
        this.modified = modified;
    }

    public String getKey()
    {
        return key;
    }

    public long getSize()
    {
        return size;
    }

    public long getModified()
    {
        return modified;
    }
}
