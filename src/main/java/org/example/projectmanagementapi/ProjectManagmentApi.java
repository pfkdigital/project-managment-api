package org.example.projectmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ProjectManagmentApi {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagmentApi.class, args);
    }

}
