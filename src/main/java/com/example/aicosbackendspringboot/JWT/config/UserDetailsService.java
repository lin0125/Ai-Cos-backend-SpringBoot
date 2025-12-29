package com.example.aicosbackendspringboot.JWT.config;

import com.example.aicosbackendspringboot.JWT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userrepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userrepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with User Email: " + userEmail));
    }
}