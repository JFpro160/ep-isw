package com.ep.isw.domain.repository;

import com.ep.isw.domain.model.UserAccount;
import java.util.Optional;

public interface UserRepository {

    UserAccount save(UserAccount account);

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
