package com.jan.web.fileservice;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UploadRequest
{
    private List<String> keys;
    private List<MultipartFile> files;

    public UploadRequest(List<String> keys, List<MultipartFile> files)
    {
        this.keys = keys;
        this.files = files;
    }

    public List<String> getKeys()
    {
        return keys;
    }

    public List<MultipartFile> getFiles()
    {
        return files;
    }
}
