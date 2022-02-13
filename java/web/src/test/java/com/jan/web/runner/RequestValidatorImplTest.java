package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.ProjectRepository;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class RequestValidatorImplTest
{
    public static final long RANDOM_PROJECT_ID = 999L;
    public static final long CONTAINER_ENTITY_ID = 999L;
    private RequestValidator validator;
    private ProjectRepository projectRepository;
    private ContainerRepository containerRepository;

    @BeforeEach
    public void before()
    {
        projectRepository = Mockito.mock(ProjectRepository.class);
        containerRepository = Mockito.mock(ContainerRepository.class);

        validator = new RequestValidatorImpl(projectRepository, containerRepository);
    }

    @Test
    public void whenProjectIsValid_thenProjectIsReturned()
    {
        Project project = new Project();
        Mockito.when(projectRepository.findById(RANDOM_PROJECT_ID)).thenReturn(Optional.of(project));
        Assertions.assertThat(validator.validateProject(RANDOM_PROJECT_ID)).isEqualTo(project);
    }

    @Test
    public void whenProjectDoesNotExist_thenExceptionIsThrown()
    {
        Mockito.when(projectRepository.findById(RANDOM_PROJECT_ID)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> validator.validateProject(RANDOM_PROJECT_ID))
                .hasMessage("The project with id " + RANDOM_PROJECT_ID + " cannot be found!");
    }

    @Test
    public void whenContainerEntityIsValid_thenContainerEntityIsReturned()
    {
        ContainerEntity containerEntity = new ContainerEntity();
        Mockito.when(containerRepository.findById(CONTAINER_ENTITY_ID)).thenReturn(Optional.of(containerEntity));
        Assertions.assertThat(validator.validateContainerEntity(CONTAINER_ENTITY_ID)).isEqualTo(containerEntity);
    }

    @Test
    public void whenContainerEntityDoesNotExist_thenExceptionIsThrown()
    {
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> validator.validateContainerEntity(CONTAINER_ENTITY_ID))
                .hasMessage("The container with id " + CONTAINER_ENTITY_ID + " does not exist.");
    }
}
