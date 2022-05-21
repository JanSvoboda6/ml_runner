package com.jan.web.file;

import com.jan.web.file.information.FileInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface providing methods for file/folder manipulation.
 */
public interface FileService
{
    List<FileInformation> getAllFiles(long containerId);
    void uploadFiles(List<String> key, List<MultipartFile> files, long containerId);
    void createFolder(String key, long containerId);
    void deleteFolders(List<String> keys, long containerId);
    void deleteFiles(List<String> keys, long containerId);
    void moveFile(String oldKey, String newKey, long containerId);
    void moveFolder(String oldKey, String newKey, long containerId);
    ResponseEntity<byte[]> download(List<String> keys, long containerId);
}
