package com.baglietto.dao.repository;

import com.baglietto.dao.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findUserByUsername(String name);
}
