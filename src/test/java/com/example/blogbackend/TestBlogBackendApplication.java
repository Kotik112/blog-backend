package com.example.blogbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestBlogBackendApplication {
    public static void main(String[] args) {
        SpringApplication.from(BlogBackendApplication::main).with(TestBlogBackendApplication.class).run(args);
    }
}
