package com.wafflestudio.areucoming.couples.repository;

import com.wafflestudio.areucoming.couples.model.Couples;
import org.springframework.data.repository.CrudRepository;

public interface CouplesRepository extends CrudRepository<Couples, Long>{
    Couples findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
