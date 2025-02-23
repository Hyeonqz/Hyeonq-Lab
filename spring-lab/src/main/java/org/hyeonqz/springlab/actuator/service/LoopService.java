package org.hyeonqz.springlab.actuator.service;

import java.time.LocalDateTime;

import org.hyeonqz.springlab.actuator.entity.Actuator;
import org.hyeonqz.springlab.actuator.entity.ActuatorRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LoopService {
	private final ActuatorRepository actuatorRepository;

	public void insert() {
		for (int i = 1; i < 100000000; i++) {
			Actuator actuator = new Actuator((long)i, LocalDateTime.now());
			actuatorRepository.save(actuator);
		}
	}

}
