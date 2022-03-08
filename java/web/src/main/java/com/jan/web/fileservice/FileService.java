package com.jan.web.fileservice;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles(long containerId);
    void uploadFiles(Keys key, List<MultipartFile> files, long containerId);
    void createFolder(String key, long containerId);
    void deleteFolders(List<String> keys, long containerId);
    void deleteFiles(List<String> keys, long containerId);
    void moveFile(String oldKey, String newKey, long containerId);
    void moveFolder(String oldKey, String newKey, long containerId);
    void download(List<String> keys, long containerId);
}
