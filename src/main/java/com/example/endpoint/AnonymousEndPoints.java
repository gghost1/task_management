package com.example.endpoint;

import com.example.data.request.UserEntityRequest;
import com.example.data.entity.user.RpUser;
import com.example.data.entity.user.User;
import com.example.configuration.exception.NoDataException;
import com.example.configuration.exception.NotValidParam;
import com.example.data.response.BasicResponse;
import com.example.configuration.security.JwtUtils;
import com.example.configuration.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Tag(name = "Anonymous" , description = "Registration and login")
public class AnonymousEndPoints {

    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    DataSource dataSource;
    SecurityConfig securityConfig;
    RpUser rpUser = new RpUser(dataSource);

    @PostMapping("/register")
    @Operation(summary = "Register new user and login him")
    public ResponseEntity<?> register(@Valid @RequestBody UserEntityRequest userEntityRequest) throws SQLException, NoDataException, NotValidParam {
        Optional<User> user = rpUser.getByEmail(userEntityRequest.email());
        if (user.isPresent()) {
            throw new NotValidParam("User already exists");
        }

        rpUser.add(userEntityRequest);

        return login(userEntityRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<?> login(@Valid @RequestBody UserEntityRequest userEntityRequest) throws SQLException, NoDataException {
        Optional<User> userOpt = rpUser.getByEmail(userEntityRequest.email());
        if (userOpt.isEmpty()) {
            throw new NoDataException("User not found");
        }

        User user = userOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.email(), userEntityRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new BasicResponse<>(jwt, "User logged in successfully"));
    }
}
