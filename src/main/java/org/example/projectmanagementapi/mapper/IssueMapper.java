package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.NestedIssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ProjectMapper.class, UserMapper.class})
public interface IssueMapper {

  DetailedIssueDto toDetailedIssueDto(Issue entity);

  IssueDto toDto(Issue issue);

  NestedIssueDto toNestedIssueDto(Issue issue);
}
