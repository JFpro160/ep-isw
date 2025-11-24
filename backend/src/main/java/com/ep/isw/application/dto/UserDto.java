package com.ep.isw.application.dto;

import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserDto(UUID id, String username, String displayName, Set<UserRole> roles, Instant createdAt) {

    public static UserDto from(UserAccount account) {
        return new UserDto(account.getId(), account.getUsername(), account.getDisplayName(), account.getRoles(),
                account.getCreatedAt());
    }
}
