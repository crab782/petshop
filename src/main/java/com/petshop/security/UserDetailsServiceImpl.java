package com.petshop.security;

import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.entity.Admin;
import com.petshop.repository.UserRepository;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.AdminRepository;
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
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_user")));
        }

        Merchant merchant = merchantRepository.findByEmail(username).orElse(null);
        if (merchant != null) {
            return new org.springframework.security.core.userdetails.User(
                    merchant.getEmail(),
                    merchant.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_merchant")));
        }

        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
