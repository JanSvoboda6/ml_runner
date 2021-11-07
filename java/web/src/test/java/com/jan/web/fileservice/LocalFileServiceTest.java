package com.jan.web.fileservice;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LocalFileServiceTest
{
    @Autowired
    LocalFileService fileService;

    @Test
    public void whenGettingAllFiles_thenProperFileInformationListIsReturned()
    {
       List<FileInformation> fileInformationList = fileService.getAllFiles();
    }
}