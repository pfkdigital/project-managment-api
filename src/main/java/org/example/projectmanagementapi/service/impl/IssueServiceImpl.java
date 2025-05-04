package org.example.projectmanagementapi.service.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.IssueMapper;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.IssueService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

  private final IssueRepository issueRepository;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final NotificationService notificationService;
  private final IssueMapper issueMapper;

  @Override
  public IssueDto createIssue(IssueRequestDto issueRequestDto) {
    User reportedByUser = findUserById(issueRequestDto.getReportedById());
    User assignedToUser = findUserById(issueRequestDto.getAssignedToId());
    Project project = findProjectById(issueRequestDto.getProjectId());

    Issue newIssue =
        Issue.builder()
            .title(issueRequestDto.getTitle())
            .description(issueRequestDto.getDescription())
            .priorityStatus(issueRequestDto.getPriorityStatus())
            .reportedBy(reportedByUser)
            .assignedTo(assignedToUser)
            .status(issueRequestDto.getStatus())
            .build();

    project.addIssue(newIssue);
    reportedByUser.addReportedIssue(newIssue);
    assignedToUser.addAssignedIssue(newIssue);

    Issue savedIssue = issueRepository.save(newIssue);

    notificationService.createNotification(
        "Issue " + savedIssue.getId() + " has been created", NotificationType.CREATION);

    return issueMapper.toDto(savedIssue);
  }

  @Override
  @CachePut(value = "issues", key = "#issueId")
  public DetailedIssueDto getIssue(Integer issueId) {
    return issueMapper.toDetailedIssueDto(findIssueById(issueId));
  }

  @Override
  @Cacheable(value = "issues")
  public List<IssueDto> getAllIssues() {
    return issueRepository.findAll().stream().map(issueMapper::toDto).toList();
  }

  @Override
  @CachePut(value = "issues", key = "#issueId")
  public DetailedIssueDto updateIssue(Integer issueId, IssueRequestDto issueRequestDto) {
    Issue selectedIssue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new EntityNotFoundException("Issue with id " + issueId + " not found"));
    Project project = findProjectById(issueRequestDto.getProjectId());
    User assignedToUser = findUserById(issueRequestDto.getAssignedToId());

    if (selectedIssue.getProject().getId().equals(project.getId())) {
      project.removeIssue(selectedIssue);
      project.addIssue(selectedIssue);
    }

    selectedIssue.setTitle(issueRequestDto.getTitle());
    selectedIssue.setDescription(issueRequestDto.getDescription());
    selectedIssue.setStatus(issueRequestDto.getStatus());
    selectedIssue.setPriorityStatus(issueRequestDto.getPriorityStatus());
    assignedToUser.addAssignedIssue(selectedIssue);

    Issue updatedIssue = issueRepository.save(selectedIssue);

    notificationService.createNotification(
        "Issue " + updatedIssue.getId() + " has been updated", NotificationType.UPDATE);

    return issueMapper.toDetailedIssueDto(updatedIssue);
  }

  @Override
  @CacheEvict(value = "issues", key = "#issueId")
  public void deleteIssue(Integer issueId) {
    Issue selectedIssue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new EntityNotFoundException("Issue with id " + issueId + " not found"));

    notificationService.createNotification(
        "Issue " + selectedIssue.getId() + " has been deleted", NotificationType.DESTRUCTION);

    issueRepository.delete(selectedIssue);
  }

  private User findUserById(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
  }

  private Project findProjectById(Integer projectId) {
    return projectRepository
        .findById(projectId)
        .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " not found"));
  }

  private Issue findIssueById(Integer issueId) {
    Issue issue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new EntityNotFoundException("Issue with id " + issueId + " not found"));

    issue.setComments(
        issueRepository
            .findTaskByIdWithComments(issueId)
            .map(Issue::getComments)
            .orElse(new ArrayList<>()));

    issue.setAttachments(
        issueRepository
            .findTaskByIdWithAttachments(issueId)
            .map(Issue::getAttachments)
            .orElse(new ArrayList<>()));

    return issue;
  }
}
