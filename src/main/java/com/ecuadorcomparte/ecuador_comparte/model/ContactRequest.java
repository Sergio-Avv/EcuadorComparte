package com.ecuadorcomparte.ecuador_comparte.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_requests")
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum Purpose {
        SERVICE,
        EDIFICA_PROGRAM,
        SHOWS_AND_CONFERENCES;

        public String getVisualName() {
            return switch (this) {
                case SERVICE -> "Servicio";
                case EDIFICA_PROGRAM -> "Programa EDIFICA";
                case SHOWS_AND_CONFERENCES -> "Shows y Conferencias";
            };
        }
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        DONE;

        public String getVisualName() {
            return switch (this) {
                case PENDING -> "Pendiente";
                case IN_PROGRESS -> "En proceso";
                case DONE -> "Atendido";
            };
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Purpose getPurpose() { return purpose; }
    public void setPurpose(Purpose purpose) { this.purpose = purpose; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
