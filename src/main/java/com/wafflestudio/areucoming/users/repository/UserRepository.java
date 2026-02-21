package com.wafflestudio.areucoming.users.repository;

import com.wafflestudio.areucoming.users.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    public User findByEmail(String email);
}
