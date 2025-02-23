package org.hyeonqz.springlab.actuator.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActuatorRepository extends JpaRepository<Actuator, Long> {
}
