package com.jan.web.file;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class UploadRequest
{
    @NotBlank
    private final List<String> keys;

    @NotBlank
    private final List<MultipartFile> files;

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
