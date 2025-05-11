package org.example.final_project.services.impl;


import lombok.AllArgsConstructor;
import org.example.final_project.exception.ResourceNotFoundException;
import org.example.final_project.repositories.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {


    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "userName", username));
    }
}