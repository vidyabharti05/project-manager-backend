package com.projectmanager.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.projectmanager.backend.model.User;
import com.projectmanager.backend.model.Workspace;
import com.projectmanager.backend.repository.UserRepository;
import com.projectmanager.backend.repository.WorkspaceRepository;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceController(
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository) {

        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Workspace> getAllWorkspaces(Authentication authentication) {

        String username = authentication.getName();

        return workspaceRepository.findByOwnerUsername(username);
    }

    @PostMapping
    public Workspace createWorkspace(
            @RequestBody Workspace workspace,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        workspace.setOwner(user);

        return workspaceRepository.save(workspace);
    }
}