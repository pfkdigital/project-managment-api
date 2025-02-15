package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.IssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.IssueService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Override
    public Issue createIssue(IssueDto issueDto) {
        User reportedByUser = findUserById(issueDto.getReportedById());
        User assignedToUser = findUserById(issueDto.getAssignedToId());
        Project project = findProjectById(issueDto.getProjectId());

        Issue newIssue = Issue.builder()
                .title(issueDto.getTitle())
                .description(issueDto.getDescription())
                .priorityStatus(issueDto.getPriorityStatus())
                .reportedBy(reportedByUser)
                .assignedTo(assignedToUser)
                .build();

        project.addIssue(newIssue);
        reportedByUser.addReportedIssue(newIssue);
        assignedToUser.addAssignedIssue(newIssue);

        Issue savedIssue = issueRepository.save(newIssue);

        createNotification("Issue " + savedIssue.getId() + " has been created", NotificationType.CREATION);

        return savedIssue;
    }

    @Override
    public Issue getIssue(Integer issueId) {
        return findIssueById(issueId);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Issue updateIssue(Integer issueId, IssueDto issueDto) {
        Issue selectedIssue = findIssueById(issueId);
        User assignedToUser = findUserById(issueDto.getAssignedToId());

        selectedIssue.setTitle(issueDto.getTitle());
        selectedIssue.setDescription(issueDto.getDescription());
        selectedIssue.setStatus(issueDto.getStatus());
        selectedIssue.setPriorityStatus(issueDto.getPriorityStatus());
        assignedToUser.addAssignedIssue(selectedIssue);

        Issue updatedIssue = issueRepository.save(selectedIssue);

        createNotification("Issue " + updatedIssue.getId() + " has been updated", NotificationType.UPDATE);

        return updatedIssue;
    }

    @Override
    public void deleteIssue(Integer issueId) {
        Issue selectedIssue = findIssueById(issueId);

        createNotification("Issue " + selectedIssue.getId() + " has been deleted", NotificationType.DESTRUCTION);

        issueRepository.delete(selectedIssue);
    }

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
    }

    private Project findProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project with id " + projectId + " not found"));
    }

    private Issue findIssueById(Integer issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue with id " + issueId + " not found"));
    }

    private void createNotification(String message, NotificationType type) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notificationService.createNotification(notification);
    }
}