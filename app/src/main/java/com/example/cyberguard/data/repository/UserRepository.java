package com.example.cyberguard.data.repository;

import com.example.cyberguard.data.local.dao.UserDao;
import com.example.cyberguard.data.local.entity.UserEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {
    private final UserDao userDao;

    @Inject
    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean signup(String username, String password) {
        if (userDao.getUserByUsername(username) != null) {
            return false;
        }
        userDao.insert(new UserEntity(username, password));
        return true;
    }

    public UserEntity login(String username, String password) {
        return userDao.login(username, password);
    }
}
