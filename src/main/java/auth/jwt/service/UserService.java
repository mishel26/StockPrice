package auth.jwt.service;


import auth.jwt.model.RefreshTokenInfo;
import auth.jwt.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    Map<String,User> users = new HashMap<>();
    private static final String FILE_PATH = "Users.json";
    private static final String REFRESH_TOKEN_FILE = "refresh-tokens.json";


    ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, RefreshTokenInfo> refreshTokens = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init(){
        loadUsers();
        loadRefreshTokens();
    }
    private void loadUsers(){
        File file = new File(FILE_PATH);
        if(file.exists()){
            try{
                Map<String, User>fileData = objectMapper.readValue(file, new TypeReference<>() {});
                users.putAll(fileData);
                log.info("loaded users {}",users.keySet());
            }
            catch (Exception e){
                log.error("Couldn't load users {}",e.getMessage());
            }
        }
    }
    private void loadRefreshTokens() {
        File file = new File(REFRESH_TOKEN_FILE);
        if (file.exists()) {
            try {
                Map<String, RefreshTokenInfo> loaded = objectMapper.readValue(file, new TypeReference<>() {});
                refreshTokens.putAll(loaded);
            } catch (IOException e) {
                System.err.println("Failed to load refresh tokens: " + e.getMessage());
            }
        }
    }







    public boolean userExists(String username){
        return users.containsKey(username);
    }

    public void saveUser(User user){
        String hashed = passwordEncoder.encode(user.getPassword());
        User userToSave = new User(user.getUserName(), hashed);
        users.put(user.getUserName(),userToSave);
        saveUsers();
    }
    private void saveRefreshTokens() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(REFRESH_TOKEN_FILE), refreshTokens);
        } catch (IOException e) {
            System.err.println("Failed to save refresh tokens: " + e.getMessage());
        }
    }

    public boolean isValidPassword(String username, String rawPassword) {
        User user = users.get(username);
        return user != null && passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void saveRefreshToken(String username, String token, Instant expiry) {
        refreshTokens.put(username, new RefreshTokenInfo(token, expiry.getEpochSecond()));
        saveRefreshTokens();
    }



    public void saveUsers(){
        try{
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }

    }
    public User findByUsername(String username) {
        return users.get(username);
    }



    public RefreshTokenInfo getRefreshToken(String username) {
        return refreshTokens.get(username);
    }

}
