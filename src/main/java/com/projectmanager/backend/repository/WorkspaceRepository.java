package com.projectmanager.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectmanager.backend.model.Workspace;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findByOwnerUsername(String username);
}