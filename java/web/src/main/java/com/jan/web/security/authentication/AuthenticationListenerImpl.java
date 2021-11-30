package com.jan.web.security.authentication;

import com.jan.web.docker.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListenerImpl implements AuthenticationListener
{
    private final DockerService dockerService;

    @Autowired
    public AuthenticationListenerImpl(DockerService dockerService)
    {
        this.dockerService = dockerService;
    }

    @Override
    public void onSuccessfulLogin(Long userId)
    {
        dockerService.buildDockerContainer(userId);
    }
}
