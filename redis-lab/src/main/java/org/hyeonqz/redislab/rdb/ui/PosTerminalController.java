package org.hyeonqz.redislab.rdb.ui;

import java.util.UUID;

import org.hyeonqz.redislab.rdb.entity.Pos;
import org.hyeonqz.redislab.rdb.service.PosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PosTerminalController {
	private final PosService posService;

	@GetMapping("/apis/rdb/{id}")
	public ResponseEntity<?> getPosTerminal(@PathVariable("id") Long id) {
		long start = System.currentTimeMillis();
		UUID uuid = posService.getUuid(id);
		long end = System.currentTimeMillis();

		log.info("총 조회 걸린 시간: {}ms", end-start);

		return ResponseEntity.ok(uuid);
	}

	@GetMapping("/apis/rdb/all")
	public ResponseEntity<?> getAllPosTerminal() {
		long start = System.currentTimeMillis();
		int allPos = posService.getAllPos();
		long end = System.currentTimeMillis();

		log.info("총 조회 걸린 시간: {}ms", end-start);

		return ResponseEntity.ok(allPos);

	}
}
