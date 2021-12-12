package com.baglietto.dao.repository;

import com.baglietto.dao.entity.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TokenRepository extends CrudRepository<Token, Integer> {

    List<Token> findTokenByUid(String uid);
}
