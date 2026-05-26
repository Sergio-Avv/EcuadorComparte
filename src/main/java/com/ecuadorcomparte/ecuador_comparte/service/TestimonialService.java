package com.ecuadorcomparte.ecuador_comparte.service;

import com.ecuadorcomparte.ecuador_comparte.dto.TestimonialDTO;
import com.ecuadorcomparte.ecuador_comparte.model.Testimonial;
import com.ecuadorcomparte.ecuador_comparte.repository.TestimonialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestimonialService {

    private final TestimonialRepository repository;

    public TestimonialService(TestimonialRepository repository) {
        this.repository = repository;
    }

    public Testimonial save(TestimonialDTO dto) {
        Testimonial entity = new Testimonial();
        applyDto(entity, dto);
        return repository.save(entity);
    }

    public List<Testimonial> findAll() {
        return repository.findAll();
    }

    public List<Testimonial> filterTestimonials(String name, LocalDateTime dateFrom, LocalDateTime dateTo) {
        String nameTrim = (name != null && !name.isBlank()) ? name.trim() : null;
        return repository.filterTestimonials(nameTrim, dateFrom, dateTo);
    }

    public Optional<Testimonial> findById(Long id) {
        return repository.findById(id);
    }

    public Testimonial update(Long id, TestimonialDTO dto) {
        Testimonial existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Testimonio no encontrado"));
        applyDto(existing, dto);
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void applyDto(Testimonial entity, TestimonialDTO dto) {
        entity.setName(dto.getName());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setInstagramUrl(dto.getInstagramUrl());
        entity.setFacebookUrl(dto.getFacebookUrl());
    }
}
