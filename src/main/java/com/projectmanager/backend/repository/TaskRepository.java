package com.projectmanager.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectmanager.backend.model.Task;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    long countByStatus(String status);

    List<Task> findByProjectId(Long projectId);


    List<Task> findByOwnerUsername(
            String username
    );

    Optional<Task> findByIdAndOwnerUsername(
            Long id,
            String username
    );

    List<Task> findByProjectIdAndOwnerUsername(
            Long projectId,
            String username
    );

    long countByOwnerUsername(
            String username
    );

    long countByOwnerUsernameAndStatus(
            String username,
            String status
    );
}