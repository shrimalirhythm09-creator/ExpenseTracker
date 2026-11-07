package com.example.app.repositary;

import com.example.app.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepositary extends CrudRepository<RefreshToken,Integer> {

    Optional<RefreshToken> findByToken(String token);
}
