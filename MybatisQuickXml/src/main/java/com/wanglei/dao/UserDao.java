package com.wanglei.dao;

import com.wanglei.domain.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();
}
