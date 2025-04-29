package org.hyeonqz.redislab.rdb.repository;

import org.hyeonqz.redislab.rdb.entity.Pos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosRepository extends JpaRepository<Pos, Long> {
}
