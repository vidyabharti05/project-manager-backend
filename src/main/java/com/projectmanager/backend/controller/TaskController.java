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
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskController(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
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

    private Task getOwnedTask(
            Long taskId,
            Authentication authentication
    ) {
        return taskRepository
                .findByIdAndOwnerUsername(
                        taskId,
                        getUsername(authentication)
                )
                .orElseThrow(() ->
                        new RuntimeException("Task not found"));
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
    public List<Task> getAllTasks(
            Authentication authentication
    ) {
        return taskRepository.findByOwnerUsername(
                getUsername(authentication)
        );
    }

    @GetMapping("/count")
    public long getTaskCount(
            Authentication authentication
    ) {
        return taskRepository.countByOwnerUsername(
                getUsername(authentication)
        );
    }

    @GetMapping("/active-count")
    public long getActiveTaskCount(
            Authentication authentication
    ) {
        String username = getUsername(authentication);

        return taskRepository.countByOwnerUsernameAndStatus(
                username,
                "TODO"
        ) + taskRepository.countByOwnerUsernameAndStatus(
                username,
                "IN_PROGRESS"
        );
    }

    @GetMapping("/completed-count")
    public long getCompletedTaskCount(
            Authentication authentication
    ) {
        return taskRepository.countByOwnerUsernameAndStatus(
                getUsername(authentication),
                "COMPLETED"
        );
    }

    @GetMapping("/{id}")
    public Task getTaskById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return getOwnedTask(id, authentication);
    }

    @PostMapping
    public Task createTask(
            @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        Task task = new Task();

        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setPriority(request.priority);
        task.setStatus(request.status);
        task.setDueDate(request.dueDate);

        task.setOwner(
                getCurrentUser(authentication)
        );

        if (request.projectId != null) {
            Project project = getOwnedProject(
                    request.projectId,
                    authentication
            );

            task.setProject(project);
        }

        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        Task task = getOwnedTask(id, authentication);

        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setPriority(request.priority);
        task.setStatus(request.status);
        task.setDueDate(request.dueDate);

        if (request.projectId != null) {
            Project project = getOwnedProject(
                    request.projectId,
                    authentication
            );

            task.setProject(project);

        } else {
            task.setProject(null);
        }

        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Task task = getOwnedTask(id, authentication);

        taskRepository.delete(task);

        return "Task deleted successfully";
    }

    public static class TaskRequest {

        public String title;
        public String description;
        public String priority;
        public String status;
        public String dueDate;
        public Long projectId;
    }
}