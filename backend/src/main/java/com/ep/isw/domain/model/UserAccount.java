package com.ep.isw.domain.model;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAccount implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final String displayName;
    private final Set<UserRole> roles;
    private final Instant createdAt;
    private final boolean active;

    private UserAccount(UUID id, String username, String password, String displayName, Set<UserRole> roles,
            Instant createdAt, boolean active) {
        this.id = Objects.requireNonNull(id, "id");
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.roles = Objects.requireNonNull(roles, "roles");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.active = active;
    }

    public static UserAccount create(String username, String encodedPassword, String displayName, Set<UserRole> roles) {
        return new UserAccount(UUID.randomUUID(), username.trim(), encodedPassword, displayName.trim(), roles,
                Instant.now(), true);
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
