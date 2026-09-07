package com.projectmanager.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanager.backend.model.Project;
import com.projectmanager.backend.model.Task;
import com.projectmanager.backend.model.User;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.TaskRepository;
import com.projectmanager.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ProjectController(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private String getUsername(Authentication authentication) {
        return authentication.getName();
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository
                .findByUsername(getUsername(authentication))
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private Project getOwnedProject(
            Long projectId,
            Authentication authentication
    ) {
        return projectRepository
                .findByIdAndOwnerUsername(
                        projectId,
                        getUsername(authentication)
                )
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));
    }

    @GetMapping
    public List<Project> getAllProjects(
            Authentication authentication
    ) {
        return projectRepository.findByOwnerUsername(
                getUsername(authentication)
        );
    }

    @GetMapping("/count")
    public long getProjectCount(
            Authentication authentication
    ) {
        return projectRepository.countByOwnerUsername(
                getUsername(authentication)
        );
    }

    @GetMapping("/{id}")
    public Project getProjectById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return getOwnedProject(id, authentication);
    }

    @GetMapping("/{id}/tasks")
    public List<Task> getProjectTasks(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Project project = getOwnedProject(id, authentication);

        return taskRepository.findByProjectIdAndOwnerUsername(
                project.getId(),
                getUsername(authentication)
        );
    }

    @PostMapping
    public Project createProject(
            @RequestBody Project project,
            Authentication authentication
    ) {
        project.setOwner(
                getCurrentUser(authentication)
        );

        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    public Project updateProject(
            @PathVariable Long id,
            @RequestBody Project project,
            Authentication authentication
    ) {
        Project existingProject = getOwnedProject(
                id,
                authentication
        );

        existingProject.setName(project.getName());
        existingProject.setDescription(
                project.getDescription()
        );

        return projectRepository.save(existingProject);
    }

    @DeleteMapping("/{id}")
    public String deleteProject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Project project = getOwnedProject(
                id,
                authentication
        );

        projectRepository.delete(project);

        return "Project deleted successfully";
    }
}