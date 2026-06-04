package io.github.springreactivelab.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("event")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Event {

    @Id
    private Long id;

    private String type;

    private String payload;

    @CreatedDate
    private Instant createdAt;

    @Builder
    public Event(Long id, String type, String payload, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
    }
}
