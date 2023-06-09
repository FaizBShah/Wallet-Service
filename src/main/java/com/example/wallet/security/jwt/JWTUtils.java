package com.example.wallet.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.wallet.entity.User;
import com.example.wallet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private UserService userService;

    public String generateJwtToken(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        User user = (User) userService.loadUserByUsername(email);

        return JWT.create()
                .withSubject(email)
                .withPayload(generatePayload(user))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + jwtExpirationMs))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public Map<String,String> generatePayload(User user) {
        HashMap<String, String> payload = new HashMap<>();

        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("email", user.getEmail());

        return payload;
    }

    public String validateJwtToken(String jwtToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret)).build();
        DecodedJWT decodedJWT;

        try {
            decodedJWT = verifier.verify(jwtToken);
        } catch (Exception e) {
            return null;
        }

        return decodedJWT.getSubject();
    }

    public String parseJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.trim().length() > 7 && authHeader.startsWith("Bearer ")) {
            return authHeader.trim().substring(7);
        }

        return null;
    }
}
