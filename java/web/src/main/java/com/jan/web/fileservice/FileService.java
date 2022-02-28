package com.jan.web.fileservice;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles(long containerId);
    void uploadFiles(Keys key, List<MultipartFile> files, long containerId);
    void createDirectory(String key, long containerId);
    void deleteFolders(List<String> keys, long containerId);
    void deleteFiles(List<String> keys, long containerId);
}
