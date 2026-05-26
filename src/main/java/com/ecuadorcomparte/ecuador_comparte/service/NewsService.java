package com.ecuadorcomparte.ecuador_comparte.service;

import com.ecuadorcomparte.ecuador_comparte.dto.NewsDTO;
import com.ecuadorcomparte.ecuador_comparte.model.News;
import com.ecuadorcomparte.ecuador_comparte.repository.NewsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    public News save(NewsDTO dto) {
        News entity = new News();
        applyDto(entity, dto);
        return repository.save(entity);
    }

    public List<News> findAll() {
        return repository.findAll();
    }

    public List<News> findByStatus(News.Status status) {
        return repository.findByStatus(status);
    }

    public List<News> filterNews(String title, News.Status status,
                                  LocalDateTime dateFrom, LocalDateTime dateTo) {
        String titleTrim = (title != null && !title.isBlank()) ? title.trim() : null;
        return repository.filterNews(titleTrim, status, dateFrom, dateTo);
    }

    public Optional<News> findById(Long id) {
        return repository.findById(id);
    }

    public News update(Long id, NewsDTO dto) {
        News existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Noticia no encontrada"));
        applyDto(existing, dto);
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void applyDto(News entity, NewsDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setSummary(dto.getSummary());
        entity.setContent(dto.getContent());
        entity.setImageUrl(dto.getImageUrl());
        entity.setAuthor(dto.getAuthor());
        entity.setStatus(dto.getStatus());
        entity.setPublishedAt(dto.getPublishedAt());
    }
}
