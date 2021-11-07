package com.jan.web.fileservice;

import java.time.LocalDateTime;

public class FileInformation
{
    private final String name;
    private final long size;
    private final long lastModified;

    public FileInformation(String name, long size, long lastModified)
    {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getName()
    {
        return name;
    }

    public long getSize()
    {
        return size;
    }

    public long getLastModified()
    {
        return lastModified;
    }
}
