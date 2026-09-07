package com.projectmanager.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectmanager.backend.model.Project;

public interface ProjectRepository
        extends JpaRepository<Project, Long> {

    List<Project> findByOwnerUsername(
            String username
    );

    Optional<Project> findByIdAndOwnerUsername(
            Long id,
            String username
    );

    long countByOwnerUsername(
            String username
    );
}