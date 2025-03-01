package org.hyeonqz.redislab.nosql.repository;

import org.hyeonqz.redislab.nosql.entity.Pos;
import org.springframework.data.repository.CrudRepository;

public interface PosRepository extends CrudRepository<Pos, String> {

}
