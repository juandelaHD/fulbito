package ar.uba.fi.ingsoft1.football5.user.refresh_token;

import org.springframework.data.jpa.repository.JpaRepository;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
