package org.hyeonqz.architecturelab.eda.domainevent.notification.infra;

import org.hyeonqz.architecturelab.eda.domainevent.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
