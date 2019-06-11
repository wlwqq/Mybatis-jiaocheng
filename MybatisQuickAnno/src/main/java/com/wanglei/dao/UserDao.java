package com.wanglei.dao;

import com.wanglei.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao {

    @Select("select * from user")
    List<User> findAll();
}
