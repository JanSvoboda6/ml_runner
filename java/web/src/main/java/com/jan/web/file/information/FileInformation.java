package com.jan.web.file.information;

public class FileInformation
{
    private final String key;
    private final long size;
    private final long modified;

    public FileInformation(String key)
    {
        this.key = key;
        this.size = 0;
        this.modified = 0;
    }

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
