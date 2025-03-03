package org.hyeonqz.redislab.rdb.repository;

import java.time.LocalDate;
import java.util.List;

import org.hyeonqz.redislab.rdb.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

	List<Settlement> findByDate(LocalDate date);
}
