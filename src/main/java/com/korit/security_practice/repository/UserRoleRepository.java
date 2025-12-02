package com.korit.security_practice.repository;

import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public void addRoleUser(UserRole userRole) {
        userRoleMapper.addUserRole(userRole);
    }

    public void updateUserRole(UserRole userRole) {
        userRoleMapper.updateUserRole(userRole);
    }

}
