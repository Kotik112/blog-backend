package com.example.blogbackend.repository;

import com.example.blogbackend.domain.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findByName(String name);
}
