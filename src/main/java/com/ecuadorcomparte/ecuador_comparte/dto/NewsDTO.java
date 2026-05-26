package com.ecuadorcomparte.ecuador_comparte.dto;

import com.ecuadorcomparte.ecuador_comparte.model.News;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class NewsDTO {

    private String title;
    private String summary;
    private String content;
    private String imageUrl;
    private String author;
    private News.Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAt;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public News.Status getStatus() { return status; }
    public void setStatus(News.Status status) { this.status = status; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
