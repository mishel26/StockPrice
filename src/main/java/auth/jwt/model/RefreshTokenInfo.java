package auth.jwt.model;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshTokenInfo {
    private String token;
    private Long expiry;

    public RefreshTokenInfo() {
    }

    public RefreshTokenInfo(String token, Long expiry) {
        this.token = token;
        this.expiry = expiry;
    }
}
