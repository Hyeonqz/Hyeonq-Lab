package org.hyeonqz.redislab.nosql.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hyeonqz.redislab.rdb.entity.Pos;
import org.hyeonqz.redislab.rdb.repository.PosRepository;
import org.hyeonqz.redislab.rdb.service.PosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)  // Mockito 확장 사용
public class PosServiceUnitTest {

	@Mock
	private PosRepository posRepository;  // PosRepository 모킹

	@InjectMocks
	private PosService posService;  // 모킹된 의존성을 주입받은 PosService

	@Test
	@DisplayName("POS 를 생성한다")
	void createPos() {
		// given
		Pos mockPos = Pos.builder()
			.uuid(UUID.randomUUID())
			.created(LocalDateTime.now())
			.build();

		// 모킹 설정: save 호출 시 mockPos 반환
		when(posRepository.save(any(Pos.class))).thenReturn(mockPos);

		// when
		posService.createPos();

		// then
		verify(posRepository, times(1)).save(any(Pos.class));
	}
}
