package com.korit.security_practice.mapper;

import com.korit.security_practice.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> getUserByUserId(Integer userId);
    Optional<User> getUserByEmail(String Email);
    Optional<User> getUserByUsername(String Email);
    int addUser(User user);
    int updatePassword(User user);
    int updateUsername(User user);

}
