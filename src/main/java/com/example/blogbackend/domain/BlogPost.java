package com.example.blogbackend.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_post")
@Schema(description = "Represents a blog post in the system.")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the blog post.", example = "1")
    private Long id;
    @Schema(description = "Title of the blog post.", example = "My First Blog Post")
    private String title;
    @Schema(description = "Content of the blog post.", example = "This is the content of my first blog post.")
    private String content;
    @Schema(description = "Created at timestamp of the blog post.", example = "2023-10-01T12:00:00Z")
    private Instant createdAt;
    @Schema(description = "Last edited at timestamp of the blog post.", example = "2023-10-02T12:00:00Z")
    private Instant lastEditedAt = null;
    @Schema(description = "Indicates whether the blog post has been edited.", example = "false")
    private Boolean isEdited = false;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @Schema(description = "List of comments associated with the blog post.", example = "[{\"id\":1,\"content\":\"Great post!\"}]")
    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @Schema(description = "List of likes associated with the blog post.", example = "[{\"id\":1,\"userId\":1}]")
    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @Schema(description = "Image associated with the blog post.")
    @OneToOne(cascade = CascadeType.ALL)
    Image image;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BlogPost blogPost = (BlogPost) o;

        if (!getTitle().equals(blogPost.getTitle()))
            return false;
        return getContent().equals(blogPost.getContent());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + getContent().hashCode();
        return result;
    }
}
