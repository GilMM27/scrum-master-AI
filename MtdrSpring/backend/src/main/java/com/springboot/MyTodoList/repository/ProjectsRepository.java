package com.springboot.MyTodoList.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Projects;

public interface ProjectsRepository extends JpaRepository<Projects, UUID> {
    
    Optional<Projects> findByName(String name);

    Boolean existsByName(String name);
}
