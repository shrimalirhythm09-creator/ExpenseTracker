package com.example.app.repositary;

import com.example.app.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositary extends CrudRepository<UserInfo,Long> {
    public UserInfo findByUsername(String username);
}
