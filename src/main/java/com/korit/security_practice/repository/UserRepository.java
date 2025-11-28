package com.korit.security_practice.repository;

import com.korit.security_practice.entity.User;
import com.korit.security_practice.mapper.UserMapper;
import com.korit.security_practice.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    public Optional<User> getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }
    public Optional<User> getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }
    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    public Optional<User> addUser(User user) {
        try {
            userMapper.addUser(user);
        } catch (DuplicateKeyException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public int updatePassword (User user) {
        return userMapper.updatePassword(user);
    }

    public int updateUsername (User user) {
        return userMapper.updateUsername(user);
    }
}
