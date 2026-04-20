package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.User;
import com.petshop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    public User login(String email, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, email));
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public User findByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
    }

    public User findById(Integer id) {
        return userMapper.selectById(id);
    }

    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> findAll(org.springframework.data.domain.Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> mpPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(mpPage, wrapper);
    }

    public User update(User user) {
        userMapper.updateById(user);
        return user;
    }

    public void delete(Integer id) {
        userMapper.deleteById(id);
    }
}
