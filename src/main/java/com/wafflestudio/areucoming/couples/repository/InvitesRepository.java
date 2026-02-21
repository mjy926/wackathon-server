package com.wafflestudio.areucoming.couples.repository;

import com.wafflestudio.areucoming.couples.model.Invites;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvitesRepository extends CrudRepository<Invites, Long> {
    public Optional<Invites> findByCode(String code);
}
