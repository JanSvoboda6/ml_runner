package com.jan.web;

import com.jan.web.project.ProjectController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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