package org.example.projectmanagementapi.service;

import java.util.List;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;

public interface IssueService {
  IssueDto createIssue(IssueRequestDto issueRequestDto);

  DetailedIssueDto getIssue(Integer issueId);

  List<IssueDto> getAllIssues();

  DetailedIssueDto updateIssue(Integer issueId, IssueRequestDto issueRequestDto);

  void deleteIssue(Integer issueId);
}
