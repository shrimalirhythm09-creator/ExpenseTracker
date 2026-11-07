package com.example.app.model;

import com.example.app.entity.UserInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDto extends UserInfo {

    private String userName;
    private String lastName;
    private Long phoneNumber;
    private String email;
}