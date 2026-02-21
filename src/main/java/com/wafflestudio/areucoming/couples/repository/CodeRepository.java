package com.wafflestudio.areucoming.couples.repository;

import com.wafflestudio.areucoming.couples.model.Code;
import com.wafflestudio.areucoming.couples.model.Invites;
import org.springframework.data.repository.CrudRepository;


public interface CodeRepository extends CrudRepository<Code, Long> {
    public Code findByCode(String code);
    public boolean existsByCode(String code);
}
