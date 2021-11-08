package com.jan.web.fileservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LocalFile
{

    @Autowired
    LocalFileService fileService;

    @Test
    public void whenGettingAllFiles_thenProperFileInformationListIsReturned()
    {
       List<FileInformation> fileInformationList = fileService.getAllFiles();
        Assertions.assertThat(fileInformationList.size()).isEqualTo(1);
    }
}