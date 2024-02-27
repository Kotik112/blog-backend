package com.example.blogbackend.dto;

import com.example.blogbackend.domain.Image;

import java.time.Instant;

public record ImageDto(long id, String name, String type, Instant createdAt) {
	public static ImageDto toDto(Image image) {
		return new ImageDto(
				image.getId(),
				image.getName(),
				image.getType(),
				image.getCreatedAt()
		);
	}
	
	public Image toDomain() {
		return new Image(
				id,
				name,
				type,
				null,
				createdAt
		);
	}
}
