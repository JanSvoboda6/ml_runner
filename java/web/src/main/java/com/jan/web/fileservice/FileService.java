package com.jan.web.fileservice;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles();
    void uploadFiles(Keys key, List<MultipartFile> files);
    void createDirectory(String key);
}
