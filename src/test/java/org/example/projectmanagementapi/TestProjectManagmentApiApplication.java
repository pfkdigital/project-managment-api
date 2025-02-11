package org.example.projectmanagmentapi;

import org.springframework.boot.SpringApplication;

public class TestProjectManagmentApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProjectManagmentApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
