package com.ep.isw.infrastructure.persistence;

import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserAccount> storage = new ConcurrentHashMap<>();

    @Override
    public UserAccount save(UserAccount account) {
        storage.put(account.getUsername(), account);
        return account;
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(storage.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return storage.containsKey(username);
    }
}
