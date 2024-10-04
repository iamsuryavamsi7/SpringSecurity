package com.connekt.SpringSecurity_V_01.Repo;

import com.connekt.SpringSecurity_V_01.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
