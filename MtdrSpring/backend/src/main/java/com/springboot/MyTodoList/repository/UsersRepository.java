package com.springboot.MyTodoList.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import java.util.List;


public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByTelegramId(String telegram_id);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByTelegramId(String telegram_id);

    List<Users> findByUserRole(UserRole userRole);
}
