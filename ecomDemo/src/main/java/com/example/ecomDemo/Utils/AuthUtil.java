package com.example.ecomDemo.Utils;

import com.example.ecomDemo.Entity.User;
import com.example.ecomDemo.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthUtil {

    @Autowired
    private UserRepo userRepo;

    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getEmail();
    }

    public Integer loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      User user=  userRepo.findByUsername(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

      return  user.getUserId();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     User user=   userRepo.findByUsername(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

     return user;
    }
}
