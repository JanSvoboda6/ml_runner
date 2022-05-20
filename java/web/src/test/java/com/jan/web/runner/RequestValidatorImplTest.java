package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class RequestValidatorImplTest
{
    public static final long RANDOM_PROJECT_ID = 999L;
    public static final long CONTAINER_ENTITY_ID = 999L;
    private static final long RUNNER_ID = 999L;
    private static final String USERNAME = "user@email.com";
    private RequestValidator validator;
    private ProjectRepository projectRepository;
    private ContainerRepository containerRepository;
    private RunnerRepository runnerRepository;
    private UserRepository userRepository;
    private JsonWebTokenUtility tokenUtility;
    private User user;

    @BeforeEach
    public void before()
    {
        projectRepository = Mockito.mock(ProjectRepository.class);
        containerRepository = Mockito.mock(ContainerRepository.class);
        runnerRepository = Mockito.mock(RunnerRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        user = Mockito.mock(User.class);
        validator = new RequestValidatorImpl(projectRepository, containerRepository, runnerRepository, userRepository, tokenUtility);
    }

    @Test
    public void whenProjectIsValid_thenProjectIsReturned()
    {
        Project project = Mockito.mock(Project.class);
        Mockito.when(projectRepository.findByUserAndId(user, RANDOM_PROJECT_ID)).thenReturn(Optional.of(project));
        Assertions.assertThat(validator.validateProject(RANDOM_PROJECT_ID, user)).isEqualTo(project);
    }

    @Test
    public void whenProjectDoesNotExist_thenExceptionIsThrown()
    {
        Mockito.when(projectRepository.findById(RANDOM_PROJECT_ID)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> validator.validateProject(RANDOM_PROJECT_ID, user))
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
                .hasMessage("The container with id " + CONTAINER_ENTITY_ID + " cannot be found!");
    }

    @Test
    public void whenRunnerIsValid_thenRunnerIsReturned()
    {
        Project project = Mockito.mock(Project.class);
        Mockito.when(project.getId()).thenReturn(RANDOM_PROJECT_ID);
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(projectRepository.findByUserAndId(user, RANDOM_PROJECT_ID)).thenReturn(Optional.of(project));
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));
        Assertions.assertThat(validator.validateRunner(RUNNER_ID, user)).isEqualTo(runner);
    }

    @Test
    public void whenRunnerDoesNotExist_thenExceptionIsThrown()
    {
        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> validator.validateRunner(RUNNER_ID, user))
                .hasMessage("The runner with id " + RUNNER_ID + " cannot be found!");
    }

    @Test
    public void whenUserIsValid_thenUserIsReturned()
    {
        User user = new User();
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Assertions.assertThat(validator.validateUser(USERNAME)).isEqualTo(user);
    }

    @Test
    public void whenUserDoesNotExist_thenExceptionIsThrown()
    {
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> validator.validateUser(USERNAME))
                .hasMessage("The user with username " + USERNAME + " cannot be found!");
    }
}
