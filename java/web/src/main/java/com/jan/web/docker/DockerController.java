package com.jan.web.docker;

import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/docker")
public class DockerController
{
    private final DockerService dockerService;
    private final UserRepository userRepository;
    private final JsonWebTokenUtility jsonWebTokenUtility;

    @Autowired
    public DockerController(DockerService dockerService, UserRepository userRepository, JsonWebTokenUtility jsonWebTokenUtility)
    {
        this.dockerService = dockerService;
        this.userRepository = userRepository;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
    }

    @GetMapping
    public ResponseEntity<?> prepareContainer(@RequestHeader(name = "Authorization") String token)
    {
        dockerService.buildDockerContainer(getUserId(token));
        return ResponseEntity.ok("Container has been prepared.");
    }

    private Long getUserId(String token)
    {
        String username = jsonWebTokenUtility.getUsernameFromJwtToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent())
        {
            return user.get().getId();
        }
        throw new RuntimeException("User not found!");
    }
}
