package com.tjk.dao;

import com.tjk.vo.User;

import java.util.List;

public interface UserDao {
//    List<User> getUserList();

    List<String> getRolesByUserName(String username);

    List<User> getUserByUsername(String username);

    List<String> getPermissionsByUserName(String username);
}
