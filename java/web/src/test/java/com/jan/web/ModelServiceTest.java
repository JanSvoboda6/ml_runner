package com.jan.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ModelServiceTest
{
    @Autowired
    private ModelController modelController;

    @Test
    public void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
        List<Model> models = modelController.list();

        Assertions.assertThat(models.size()).isEqualTo(3);
    }
}