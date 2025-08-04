package auth.jwt.controller;


import auth.jwt.security.JwtUtil;
import auth.jwt.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class ApiController {



    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String>mp) {
        String userName = mp.get("username");
        String pass = mp.get("password");
        return loginService.verifyAndLoginUser(userName,pass);


    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Map<String,String>mp) {

        String userName = mp.get("user");
        String pass = mp.get("pass");
        return loginService.signUpUser(userName,pass);


    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

       return loginService.checkForRefresh(refreshToken);


    }


    @GetMapping("/healthCheck")
    public String dummy() {
        log.info("received api request ");
         return "ping-pong!!";

    }

    @GetMapping("/hello")
    public Map<String, String> hello(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return Map.of("error", "Missing or invalid Authorization header");
        }

       return loginService.checkHello(header);


    }
}
