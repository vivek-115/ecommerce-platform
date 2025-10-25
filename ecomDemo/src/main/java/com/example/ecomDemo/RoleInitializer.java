package com.example.ecomDemo;

import com.example.ecomDemo.Entity.AppRole;
import com.example.ecomDemo.Entity.Role;
import com.example.ecomDemo.Repositry.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        for (AppRole role : AppRole.values()) {
            insertRole(role);
        }
    }

    private void insertRole(AppRole roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        if (role.isEmpty()) {
            roleRepository.save(new Role(roleName));
            System.out.println("Inserted role: " + roleName);
        }
    }
}