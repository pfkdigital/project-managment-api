package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.IssueDto;
import org.example.projectmanagementapi.entity.Issue;

import java.util.List;

public interface IssueService {
    Issue createIssue(IssueDto issueDto);
    Issue getIssue(Integer issueId);
    List<Issue> getAllIssues();
    Issue updateIssue(Integer issueId, IssueDto issueDto);
    void deleteIssue(Integer issueId);
}
