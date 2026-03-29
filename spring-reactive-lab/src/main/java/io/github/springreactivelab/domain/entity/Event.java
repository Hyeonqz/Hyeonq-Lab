package io.github.springreactivelab.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("event")
@Getter
public class Event {

    @Id
    private Long id;

    private String type;

    private String payload;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Event(Long id, String type, String payload, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
    }
}
