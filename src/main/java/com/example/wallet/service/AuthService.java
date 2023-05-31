package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.AppException;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.security.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AppException(HttpStatus.CONFLICT, "User Already Exists");
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .wallet(new Wallet())
                .locked(false)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    public String loginUser(String email, String password, HttpServletRequest request) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "User does not have an account");
        }

        String jwtToken = jwtUtils.parseJwtToken(request);

        if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken) != null) {
            throw new AppException(HttpStatus.FORBIDDEN, "User already logged in");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtUtils.generateJwtToken(authentication);
    }
}
