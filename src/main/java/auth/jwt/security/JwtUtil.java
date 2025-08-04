package auth.jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Configuration
public class JwtUtil {

    @Autowired
    public PrivateKey privateKey;
    @Autowired
    public PublicKey publicKey;
    private static final long ACCESS_TOKEN_EXPIRY_SECONDS = 15 * 60;      // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY_SECONDS = 7 * 24 * 60 * 60; // 7 days


    public String generateAccessToken(String username) {
         // Set timezone to UTC
        // Implementation for generating JWT token using privateKey
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        calendar.setTimeInMillis(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_SECONDS*1000L);

       return  Jwts.builder().setSubject(username)
                .setIssuedAt(Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime())
                .setExpiration(calendar.getTime())
                .signWith(privateKey)
                .compact();
        //return null; // Placeholder return
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Instant getExpiry(String token) {
        return getClaims(token).getExpiration().toInstant();
    }

//    public Claims validateToken(String token){
//
//      return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
//    }
}
