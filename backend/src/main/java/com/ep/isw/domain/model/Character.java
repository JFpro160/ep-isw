package com.ep.isw.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Character {

    private final UUID id;
    private final String name;
    private final String village;
    private final String rank;
    private final int powerLevel;
    private final Instant createdAt;
    private final UUID createdBy;

    private Character(UUID id, String name, String village, String rank, int powerLevel, Instant createdAt,
            UUID createdBy) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.village = Objects.requireNonNull(village, "village");
        this.rank = Objects.requireNonNull(rank, "rank");
        this.powerLevel = powerLevel;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy");
    }

    public static Character create(String name, String village, String rank, int powerLevel, UUID createdBy,
            Instant createdAt) {
        if (powerLevel < 1 || powerLevel > 100) {
            throw new IllegalArgumentException("El poder debe estar entre 1 y 100");
        }
        return new Character(UUID.randomUUID(), name.trim(), village.trim(), rank.trim(), powerLevel, createdAt,
                createdBy);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVillage() {
        return village;
    }

    public String getRank() {
        return rank;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }
}
