package auth.jwt.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {


    @PostMapping("/api/auth/login")
    private String login(@RequestHeader("Authorization") String authorizationHeader) {
        // This is a placeholder for the actual login logic
        // In a real application, you would validate user credentials and return a JWT token
        return "Login successful";
    }
}
