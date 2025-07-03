package com.example.blogbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Represents an image stored in the system")
public class Image {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier for the image", example = "123")
  private long id;

  @Schema(description = "Name of the image file", example = "example.jpg")
  private String name;

  @Schema(description = "Type of the image file", example = "image/jpeg")
  private String type;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "image_data", length = 1000)
  @JdbcTypeCode(Types.VARBINARY)
  @Schema(description = "Binary data of the image file")
  private byte[] imageData;

  @Schema(
      description = "Timestamp when the image was created",
      example = "2025-06-26T03:15:19.293Z")
  @Column(name = "created_at")
  private Instant createdAt;
}
