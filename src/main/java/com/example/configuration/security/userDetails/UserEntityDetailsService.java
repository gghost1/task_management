package com.example.configuration.security.userDetails;

import com.example.data.entity.user.RpUser;
import com.example.data.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class UserEntityDetailsService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        RpUser rpUser = new RpUser(dataSource);
        Optional<User> user = null;
        try {
            user = rpUser.getByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        return new UserEntityDetails(user.get());
    }
}
