package io.github.hyeonqz.archlab.eda_arch.domainevent.notification.infra;

import io.github.hyeonqz.archlab.eda_arch.domainevent.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
