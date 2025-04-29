package org.hyeonqz.redislab.nosql.ui;

import org.hyeonqz.redislab.nosql.entity.PosTerminal;
import org.hyeonqz.redislab.nosql.service.PosQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PosController {
	private final PosQueryService posQueryService;

	@GetMapping("/apis/redis/{posId}")
	public ResponseEntity<?> get(@PathVariable("posId") String posId) {
		long start = System.currentTimeMillis();
		PosTerminal pos = posQueryService.getPos(posId);
		long end = System.currentTimeMillis();

		log.info("총 조회 걸린 시간 : {}ms", end - start);

		return ResponseEntity.ok(pos);
	}

	@GetMapping("/apis/redis/all")
	public ResponseEntity<?> getAll() {
		long start = System.currentTimeMillis();
		int posSize = posQueryService.getAllPos();
		long end = System.currentTimeMillis();

		log.info("총 조회 걸린 시간 : {}ms", end - start);

		return ResponseEntity.ok(posSize);
	}
}
