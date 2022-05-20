package com.jan.web.docker;

import com.jan.web.runner.RequestValidator;
import com.jan.web.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docker")
public class DockerController
{
    private final DockerService dockerService;
    private final RequestValidator validator;

    @Autowired
    public DockerController(DockerService dockerService, RequestValidator validator)
    {
        this.dockerService = dockerService;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<?> prepareContainer(@RequestHeader(name = "Authorization") String token)
    {
        User user = validator.validateUserFromJwtToken(token);
        dockerService.buildDockerContainer(user.getId());
        return ResponseEntity.ok("Container has been prepared.");
    }
}
