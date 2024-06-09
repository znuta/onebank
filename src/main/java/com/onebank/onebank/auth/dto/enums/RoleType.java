package com.onebank.onebank.auth.dto.enums;

public enum RoleType {
    ROLE_USER("ROLE_USER");

    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
