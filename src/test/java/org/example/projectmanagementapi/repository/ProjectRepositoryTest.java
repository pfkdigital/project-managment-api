package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeAll
    @Sql({"/schema.sql", "/data.sql"}) // Load test data from an SQL file
    static void setUp() {
    }

    @Test
    void testFindProjectWithOwnerById() {
        Optional<Project> project = projectRepository.findProjectWithOwnerById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getOwner()).isNotNull();
    }

    @Test
    void testFindProjectWithCollaboratorsById() {
        Optional<Project> project = projectRepository.findProjectWithCollaboratorsById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getCollaborators()).isNotEmpty();
    }

    @Test
    void testFindProjectWithTasksById() {
        Optional<Project> project = projectRepository.findProjectWithTasksById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getTasks()).isNotEmpty();
    }

    @Test
    void testFindProjectWithIssuesById() {
        Optional<Project> project = projectRepository.findProjectWithIssuesById(1);
        assertThat(project).isPresent();
        assertThat(project.get().getIssues()).isNotEmpty();
    }
}