package org.example.projectmanagementapi;

import org.springframework.boot.SpringApplication;

public class TestProjectManagmentApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProjectManagmentApi::main).with(TestcontainersConfiguration.class).run(args);
    }

}
