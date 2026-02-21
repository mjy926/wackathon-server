package com.wafflestudio.areucoming.users.repository;

import com.wafflestudio.areucoming.users.model.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    public User findByEmail(String email);
    public Optional<User> findById(Long id);
}
