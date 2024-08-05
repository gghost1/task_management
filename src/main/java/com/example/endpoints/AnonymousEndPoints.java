package com.example.endpoints;

import com.example.dto.UserDto;
import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.security.JwtUtils;
import com.example.security.SecurityConfig;
import com.example.security.UserEntityDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class AnonymousEndPoints {
    AuthenticationManager authenticationManager;

    JwtUtils jwtUtils;

    DataSource dataSource;

    SecurityConfig securityConfig;

    RpUser rpUser = new RpUser(dataSource);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) throws SQLException {

            Optional<User> user = rpUser.getByEmail(userDto.email());
            if (user.isPresent()) {
                return ResponseEntity.badRequest().body("User already exists");
            }

            rpUser.add(userDto);

        return login(userDto);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) throws SQLException {

        Optional<User> userOpt = rpUser.getByEmail(userDto.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.email(), userDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);


        return ResponseEntity.ok(jwt);

    }


}
