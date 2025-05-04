package org.hyeonqz.redislab.rdb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hyeonqz.redislab.rdb.entity.Pos;
import org.hyeonqz.redislab.rdb.repository.PosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PosService {
	private final PosRepository posRepository;

	@Transactional
	public Pos createPos() {
		Pos pos = Pos.builder()
			.uuid(UUID.randomUUID())
			.created(LocalDateTime.now())
			.build();

		posRepository.save(pos);

		return pos;
	}

	@Transactional
	public UUID getUuid(Long id) {
		Pos pos = posRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("No such pos: " + id));

		return pos.getUuid();
	}

	public int getAllPos() {
		List<Pos> all = posRepository.findAll();
		return all.size();
	}

}
