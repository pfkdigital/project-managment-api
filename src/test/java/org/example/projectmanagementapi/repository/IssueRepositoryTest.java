package org.example.projectmanagementapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.entity.Issue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(TestJpaConfig.class)
class IssueRepositoryTest {

    @Autowired
    private IssueRepository issueRepository;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void findTaskByIdWithCommentsReturnsIssueWithComments() {

        Optional<Issue> result = issueRepository.findTaskByIdWithComments(1);

        assertThat(result).isPresent();
        assertThat(result.get().getComments()).hasSize(1);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void findTaskByIdWithAttachmentsReturnsIssueWithAttachments() {

        Optional<Issue> result = issueRepository.findTaskByIdWithAttachments(2);

        assertThat(result).isPresent();
        assertThat(result.get().getAttachments()).hasSize(1);
    }
}