package auth.jwt.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        String authHeader = req.getHeader("Authorization");
        if(authHeader == null && (req.getRequestURI().contains("signUp")||req.getRequestURI().contains("healthCheck") || req.getRequestURI().contains("login"))){
            return true;
        }
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Missing or Invalid Authorization header");
            return false;
        }
        String token =authHeader.substring(7);
        try {
            return jwtUtil.validateToken(token);
//            req.setAttribute("user", claim.getSubject());
//            return true;
        }catch (Exception e){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Invalid or expired token");
            return false;
        }
    }

}
