package com.example.blogbackend.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_post")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant lastEditedAt = null;
    private Boolean isEdited = false;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();
    
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
