package org.example.projectmanagementapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(TestJpaConfig.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;


    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void findTaskByIdWithAttachmentsReturnsTaskWithAttachments() {
        Optional<Task> result = taskRepository.findTaskByIdWithAttachments(1);

        assertThat(result).isPresent();
        assertThat(result.get().getAttachments()).hasSize(1);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void findTaskByIdWithCommentsReturnsTaskWithComments() {
        Optional<Task> result = taskRepository.findTaskByIdWithComments(1);

        assertThat(result).isPresent();
        assertThat(result.get().getComments()).hasSize(1);
    }
}