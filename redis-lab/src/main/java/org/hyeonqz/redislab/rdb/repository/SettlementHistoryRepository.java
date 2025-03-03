package org.hyeonqz.redislab.rdb.repository;

import org.hyeonqz.redislab.rdb.entity.SettlementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementHistoryRepository extends JpaRepository<SettlementHistory, Long> {
}
