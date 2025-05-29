package ar.uba.fi.ingsoft1.football5.config.security;

public record JwtUserDetails (
        String username,
        String role
) {}