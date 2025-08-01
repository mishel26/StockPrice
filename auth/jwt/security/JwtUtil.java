package auth.jwt.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class JwtUtil {

    @Autowired
    private PrivateKey privateKey;
    @Autowired
    private PublicKey publicKey;

    public String generateToken(String username,String expiryInMillis) {
         // Set timezone to UTC
        // Implementation for generating JWT token using privateKey
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        calendar.setTimeInMillis(System.currentTimeMillis() + Long.parseLong(expiryInMillis));

        return Jwts.builder().setSubject(username)
                .setIssuedAt(Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime())
                .setExpiration(calendar.getTime())
                .signWith(privateKey)
                .compact();
        //return null; // Placeholder return
    }

    private String validateToken(String token){

      return null;
    }
}
