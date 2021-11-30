package com.jan.web.security;


import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController
{
    @Autowired
    JsonWebTokenUtility jsonWebTokenUtility;

    @GetMapping("/all")
    public String allAccess()
    {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess(@RequestHeader(name="Authorization") String token)
    {
        return "User Content. " + jsonWebTokenUtility.getUsernameFromJwtToken(token);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess()
    {
        return "Admin Board.";
    }
}
