package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ProjectMapper.class, UserMapper.class, UserMapper.class})
public interface IssueMapper {

  Issue toEntity(IssueRequestDto dto);

  DetailedIssueDto toDetailedIssueDto(Issue entity);

  Issue toEntity(IssueDto issueDto);

  IssueDto toDto(Issue issue);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Issue partialUpdate(IssueDto issueDto, @MappingTarget Issue issue);
}
