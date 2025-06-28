package com.example.blogbackend.dto;

import com.example.blogbackend.domain.Image;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Details about an image attached to a blog post")
public record ImageDto(

		@Schema(description = "Unique ID of the image", example = "301")
		long id,

		@Schema(description = "Name of the image file", example = "featured-image.png")
		String name,

		@Schema(description = "MIME type of the image", example = "image/png")
		String type,

		@Schema(description = "Timestamp when the image was uploaded", example = "2025-06-26T03:15:19.293Z")
		Instant createdAt

) {
	public static ImageDto toDto(Image image) {
		return new ImageDto(
				image.getId(),
				image.getName(),
				image.getType(),
				image.getCreatedAt()
		);
	}
}
