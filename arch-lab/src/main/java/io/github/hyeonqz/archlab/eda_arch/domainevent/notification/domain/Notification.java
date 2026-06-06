package io.github.hyeonqz.archlab.eda_arch.domainevent.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eda_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;

    public static Notification create(Long orderId, String message, String type) {
        Notification notification = new Notification();
        notification.orderId = orderId;
        notification.message = message;
        notification.type = type;
        return notification;
    }
}
