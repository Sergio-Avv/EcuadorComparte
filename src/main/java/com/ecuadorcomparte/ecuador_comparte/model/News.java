package com.ecuadorcomparte.ecuador_comparte.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    private String author;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        DRAFT("Borrador"),
        PUBLISHED("Publicado");

        private final String visualName;

        Status(String visualName) {
            this.visualName = visualName;
        }

        public String getVisualName() {
            return visualName;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}