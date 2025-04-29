package org.hyeonqz.redislab.nosql.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.hyeonqz.redislab.rdb.entity.Pos;
import org.hyeonqz.redislab.rdb.repository.PosRepository;
import org.hyeonqz.redislab.rdb.service.PosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PosServiceIntegrationTest {

	@Autowired
	private PosService posService;

	@Autowired
	private PosRepository posRepository;

	@Test
	@Commit
	@DisplayName("POS를 1개 생성한다")
	void createPos() {
		Pos pos = posService.createPos();

		assertNotNull(pos.getUuid());
		assertNotNull(pos.getCreated());
	}

	@Test
	@Commit
	@DisplayName("POS 를 여러개 생성한다.")
	void multipleCreatePos() {
		var list = new ArrayList<Pos>();

		for (int i = 0; i <= 100000 ; i++) {
			list.add(posService.createPos());
		}

		posRepository.saveAll(list);
	}




}