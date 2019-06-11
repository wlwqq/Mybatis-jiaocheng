package com.wanglei.dao;

import com.wanglei.domain.Queryvo;
import com.wanglei.domain.User;
import com.wanglei.domain.User1;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao {

    void saveUser(User user);

    void deleteUser(Integer id);

    void updateUser(User user);

    User findById(Integer id);

    List<User> findByName(String username);

    Integer findTotal();

    List<User> findByVo(Queryvo queryvo);

    User1 findUser1ById(Integer id);
}
