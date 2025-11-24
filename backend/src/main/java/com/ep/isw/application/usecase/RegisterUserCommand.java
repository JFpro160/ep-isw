package com.ep.isw.application.usecase;

public record RegisterUserCommand(String username, String password, String displayName) {
}
