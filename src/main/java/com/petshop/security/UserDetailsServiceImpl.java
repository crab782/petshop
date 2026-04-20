package com.petshop.security;

import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.entity.Admin;
import com.petshop.mapper.UserMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByPhone(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getPhone(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_user")));
        }

        Merchant merchant = merchantMapper.selectByEmail(username);
        if (merchant != null) {
            return new org.springframework.security.core.userdetails.User(
                    merchant.getEmail(),
                    merchant.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_merchant")));
        }

        Admin admin = adminMapper.selectByUsername(username);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
