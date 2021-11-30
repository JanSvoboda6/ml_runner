package com.jan.web.fileservice;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles(long containerId);
    void uploadFiles(Keys key, List<MultipartFile> files, long containerId);
    void createDirectory(String key, long containerId);
}
