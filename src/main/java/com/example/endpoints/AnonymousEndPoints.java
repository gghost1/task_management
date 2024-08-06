package com.example.endpoints;

import com.example.dto.UserDto;
import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotValidParam;
import com.example.response.BasicResponce;
import com.example.security.JwtUtils;
import com.example.security.SecurityConfig;
import com.example.security.UserEntityDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Tag(name = "Anonymous")
public class AnonymousEndPoints {
    AuthenticationManager authenticationManager;

    JwtUtils jwtUtils;

    DataSource dataSource;

    SecurityConfig securityConfig;

    RpUser rpUser = new RpUser(dataSource);

    @PostMapping("/register")
    @Operation(summary = "Register new user and login him")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) throws SQLException, NoDataException, NotValidParam {

            Optional<User> user = rpUser.getByEmail(userDto.email());
            if (user.isPresent()) {
                throw new NotValidParam("User already exists");
            }

            rpUser.add(userDto);

        return login(userDto);
    }
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto) throws SQLException, NoDataException {

        Optional<User> userOpt = rpUser.getByEmail(userDto.email());
        if (userOpt.isEmpty()) {
            throw new NoDataException("User not found");
        }

        User user = userOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.email(), userDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);


        return ResponseEntity.ok(new BasicResponce<>(jwt, "User logged in successfully"));

    }


}
