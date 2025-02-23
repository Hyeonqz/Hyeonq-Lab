package org.hyeonqz.springlab.actuator.controller;

import org.hyeonqz.springlab.actuator.service.LoopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/act")
@RestController
public class ActuatorController {
	private final LoopService loopService;

	@GetMapping("/loop")
	public void forLoop() {
		loopService.insert();
	}
}
