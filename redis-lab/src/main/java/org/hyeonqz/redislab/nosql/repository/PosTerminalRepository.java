package org.hyeonqz.redislab.nosql.repository;

import java.util.Optional;

import org.hyeonqz.redislab.nosql.entity.PosTerminal;
import org.springframework.data.repository.CrudRepository;

public interface PosTerminalRepository extends CrudRepository<PosTerminal, String> {
	// pos:<posId> 로 조회
	Optional<PosTerminal> findByPosId(String posId);

	// findAll(): pos:* 패턴으로 모든 객체를 가져옴
	// Redis 는 기본적으로 전체 스캔에 적합하지 않음
}
