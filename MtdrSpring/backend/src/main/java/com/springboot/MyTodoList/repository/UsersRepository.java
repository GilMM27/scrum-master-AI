package com.springboot.MyTodoList.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import java.util.List;


public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByTelegramId(Long telegramId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Boolean existsByTelegramId(Long telegramId);

    List<Users> findByUserRole(UserRole userRole);

    Optional<Users> findByCellPhone(String cellPhone);

    long countByUserIdIn(Collection<UUID> userIds);
}
