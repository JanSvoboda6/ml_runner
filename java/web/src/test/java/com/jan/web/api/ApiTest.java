package com.jan.web.api;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.ClassificationLabelJson;
import com.jan.web.project.ProjectRepository;
import com.jan.web.runner.Runner;
import com.jan.web.runner.RunnerRepository;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest
{
    private static final String BASE_URL = "http://localhost:";
    private static final String TEST_USER = "user@email.com";
    private static final String PASSWORD = "password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserCreator userCreator;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RunnerRepository runnerRepository;

    private User user;
    private String jwtToken;
    private Runner runner;

    @BeforeEach
    public void before()
    {
        user = userCreator.createUser(TEST_USER, encoder.encode(PASSWORD));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow();
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        jwtToken = jsonWebTokenUtility.generateJwtToken(user);

        DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClientBuilder.getInstance(config.build()).build();
    }

    @AfterEach
    public void after()
    {
        if(runner != null)
        {
            runnerRepository.delete(runner);
        }
        projectRepository.deleteAll(projectRepository.findAllByUser(user));
        userRepository.delete(user);
    }

    @Test
    public void publicContent()
    {
        Assertions.assertThat(restTemplate.getForObject(BASE_URL + port + "/api/test/all", String.class))
                .contains("Public Content.");
    }

    @Test
    public void userContent()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        Assertions.assertThat(restTemplate
                        .exchange(BASE_URL + port + "/api/test/user", HttpMethod.GET, request, String.class)
                        .getBody())
                .contains("User Content.");
    }

    @Test
    public void whenUserTriesToCreateProject_thenProjectIsCreated() throws JSONException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject json = new JSONObject();
        json.put("projectName", "random name");
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        restTemplate.exchange(BASE_URL + port + "/api/project/saveproject", HttpMethod.POST, request, String.class);

        Assertions.assertThat(projectRepository.findAllByUser(user)).isNotNull();
    }

    @Test
    public void whenUserHasNoProjectsCreated_thenNoProjectIsReturned()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        Assertions.assertThat(restTemplate.exchange(BASE_URL + port + "/api/project", HttpMethod.GET, request, String.class).getBody())
                .isNotBlank();
    }

    @Test
    public void whenUserHasCreatedProjects_thenProjectsAreReturned() throws JSONException, IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONArray classificationLabels = new JSONArray();
        classificationLabels.put(new JSONObject().put("labelName", "Random Label Name").put("folderPath", "random/"));

        JSONObject json = new JSONObject();
        json.put("projectName", "Random Project Name");
        json.put("firstLabel", "first");
        json.put("secondLabel", "second");
        json.put("firstLabelFolder", "first/");
        json.put("secondLabelFolder", "second/");
        json.put("selectedModel", "Support Vector Machines");
        json.put("classificationLabels", classificationLabels);
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        restTemplate.exchange(BASE_URL + port + "/api/project/saveproject", HttpMethod.POST, request, String.class).getBody();
        String body = restTemplate.exchange(BASE_URL + port + "/api/project", HttpMethod.GET, request, String.class).getBody();

        JsonNode jsonResponse = new ObjectMapper().readTree(body);

        Assertions.assertThat(jsonResponse.get(0).get("name").toString()).contains("Random Project Name");
    }

    @Test
    public void whenProjectHasRunners_thenRunnersAreReturned() throws IOException, JSONException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONArray classificationLabels = new JSONArray();
        classificationLabels.put(new JSONObject().put("labelName", "Random Label Name").put("folderPath", "random/"));

        JSONObject projectRequestJson = new JSONObject();
        projectRequestJson.put("projectName", "Random Project Name");
        projectRequestJson.put("firstLabel", "first");
        projectRequestJson.put("secondLabel", "second");
        projectRequestJson.put("firstLabelFolder", "first/");
        projectRequestJson.put("secondLabelFolder", "second/");
        projectRequestJson.put("selectedModel", "Support Vector Machines");
        projectRequestJson.put("classificationLabels", classificationLabels);
        HttpEntity<String> request = new HttpEntity<>(projectRequestJson.toString(), headers);

        HttpEntity<String> projectRequest = new HttpEntity<>(projectRequestJson.toString(), headers);
        restTemplate.exchange(BASE_URL + port + "/api/project/saveproject", HttpMethod.POST, projectRequest, String.class);
        String projectResponseBody = restTemplate.exchange(BASE_URL + port + "/api/project", HttpMethod.GET, projectRequest, String.class).getBody();

        JsonNode projectJson = new ObjectMapper().readTree(projectResponseBody);
        Long projectId = Long.parseLong(projectJson.get(0).get("id").toString());

        runner = new Runner();
        runner.setProject(projectRepository.getById(projectId));
        runnerRepository.save(runner);

        HttpEntity<String> runnerRequest = new HttpEntity<>(headers);
        String runnerResponseBody = restTemplate.exchange(BASE_URL + port + "/api/project/runners?projectId=" + projectId, HttpMethod.GET, runnerRequest, String.class).getBody();
        JsonNode runnerJson = new ObjectMapper().readTree(runnerResponseBody);

        Assertions.assertThat(Long.parseLong(runnerJson.get(0).get("project").get("id").toString())).isEqualTo(projectId);
    }

    @Test
    public void whenProjectHasNoRunners_thenNoRunnersAreReturned() throws IOException, JSONException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONArray classificationLabels = new JSONArray();
        classificationLabels.put(new JSONObject().put("labelName", "Random Label Name").put("folderPath", "random/"));

        JSONObject projectRequestJson = new JSONObject();
        projectRequestJson.put("projectName", "Random Project Name");
        projectRequestJson.put("firstLabel", "first");
        projectRequestJson.put("secondLabel", "second");
        projectRequestJson.put("firstLabelFolder", "first/");
        projectRequestJson.put("secondLabelFolder", "second/");
        projectRequestJson.put("selectedModel", "Support Vector Machines");
        projectRequestJson.put("classificationLabels", classificationLabels);
        HttpEntity<String> request = new HttpEntity<>(projectRequestJson.toString(), headers);

        HttpEntity<String> projectRequest = new HttpEntity<>(projectRequestJson.toString(), headers);
        restTemplate.exchange(BASE_URL + port + "/api/project/saveproject", HttpMethod.POST, projectRequest, String.class);
        String projectResponseBody = restTemplate.exchange(BASE_URL + port + "/api/project", HttpMethod.GET, projectRequest, String.class).getBody();

        JsonNode projectJson = new ObjectMapper().readTree(projectResponseBody);
        long projectId = Long.parseLong(projectJson.get(0).get("id").toString());

        HttpEntity<String> runnerRequest = new HttpEntity<>(headers);
        Assertions.assertThat(restTemplate.exchange(BASE_URL + port + "/api/project/runners?projectId=" + projectId, HttpMethod.GET, runnerRequest, String.class).getBody())
                .isEqualTo("[ ]");
    }

    //TODO Jan: Why the test is commented?
//    @Test
//    public void whenRequestForRunningProject_thenRunnerIsCreated() throws JSONException, IOException
//    {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + jwtToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        JSONObject projectRequestJson = new JSONObject();
//        projectRequestJson.put("projectName", "random name");
//
//        HttpEntity<String> projectRequest = new HttpEntity<>(projectRequestJson.toString(), headers);
//        restTemplate.exchange(BASE_URL + port + "/api/project/saveproject", HttpMethod.POST, projectRequest, String.class);
//        String projectResponseBody = restTemplate.exchange(BASE_URL + port + "/api/project", HttpMethod.GET, projectRequest, String.class).getBody();
//
//        JsonNode projectJson = new ObjectMapper().readTree(projectResponseBody);
//        long projectId = Long.parseLong(projectJson.get(0).get("id").toString());
//
//        JSONObject runRequestJson = new JSONObject();
//        runRequestJson.put("projectId", projectId);
//
//        HttpEntity<String> runRequest = new HttpEntity<>(runRequestJson.toString(), headers);
//        String runnerResponseBody = restTemplate.exchange(BASE_URL + port + "/api/project/runner/run", HttpMethod.POST, runRequest, String.class).getBody();
//        JsonNode runnerJson = new ObjectMapper().readTree(runnerResponseBody);
//
//        containerEntity = containerRepository.findById(Long.parseLong(runnerJson.get(0).get("id").toString())).get();
//
//        Assertions.assertThat(runnerRepository.findAllByProjectId(projectId).get(0)).isNotNull();
//    }
}
