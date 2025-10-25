package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.AppRole;
import com.example.ecomDemo.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {
   Optional<Role> findByRoleName(AppRole rolename);


}
