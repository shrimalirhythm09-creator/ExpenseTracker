package com.example.app.service;


import com.example.app.entity.RefreshToken;
import com.example.app.entity.UserInfo;
import com.example.app.repositary.RefreshTokenRepositary;
import com.example.app.repositary.UserRepositary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired RefreshTokenRepositary refreshTokenRepositary;
    @Autowired UserRepositary userRepositary;


    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtracted = userRepositary.findByUsername(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoExtracted)
                .token(String.valueOf(userInfoExtracted))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

        return refreshTokenRepositary.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepositary.delete(token);
            throw new RuntimeException(token.getToken() + "Refresh token is expired Please  reLogin.. !");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepositary.findByToken(token);
    }
}
