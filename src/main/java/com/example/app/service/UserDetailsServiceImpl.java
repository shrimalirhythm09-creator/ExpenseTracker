package com.example.app.service;

import com.example.app.entity.UserInfo;
import com.example.app.model.UserInfoDto;
import com.example.app.repositary.UserRepositary;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositary userRepositary;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserInfo user = userRepositary.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user!");
        }

        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto) {
        return userRepositary.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signUpUser(UserInfoDto userInfoDto) {

        if (Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))) {
            return false;
        }

        String encodedPassword =
                passwordEncoder.encode(userInfoDto.getPassword());

        String userId = UUID.randomUUID().toString();

        userRepositary.save(
                new UserInfo(
                        userId,
                        userInfoDto.getUsername(),
                        encodedPassword,
                        new HashSet<>()
                )
        );

        return true;
    }
}