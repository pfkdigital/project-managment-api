package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaConfig.class)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testFindProjectWithOwnerById() {
        Optional<Project> project = projectRepository.findProjectWithOwnerById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getOwner()).isNotNull();
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testFindProjectWithCollaboratorsById() {
        Optional<Project> project = projectRepository.findProjectWithCollaboratorsById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getCollaborators()).isNotEmpty();
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testFindProjectWithTasksById() {
        Optional<Project> project = projectRepository.findProjectWithTasksById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getTasks()).isNotEmpty();
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testFindProjectWithIssuesById() {
        Optional<Project> project = projectRepository.findProjectWithIssuesById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getIssues()).isNotEmpty();
    }
}