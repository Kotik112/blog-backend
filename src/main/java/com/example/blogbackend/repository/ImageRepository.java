package com.example.blogbackend.repository;

import com.example.blogbackend.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
	
	Optional<Image> findByName(String name);
}
