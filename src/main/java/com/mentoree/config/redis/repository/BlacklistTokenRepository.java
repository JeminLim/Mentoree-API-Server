package com.mentoree.config.redis.repository;

import com.mentoree.config.redis.Entity.BlacklistToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistTokenRepository extends CrudRepository<BlacklistToken, String> {

    Optional<BlacklistToken> findByBlockedToken(String token);
}
