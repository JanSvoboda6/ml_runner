package com.jan.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProjectServiceTest
{
    @Autowired
    private ProjectController projectController;

//    @Test
//    public void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
//        List<Project> models = projectController.list();
//
//        Assertions.assertThat(models.size()).isEqualTo();
//    }
}