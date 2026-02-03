package pl.kurs.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.service.RefreshTokenService;

import java.util.UUID;

@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

    private final RefreshTokenService refreshTokenService;

    @GetMapping
    public ResponseEntity<Void> logout(@CookieValue ("refreshToken") UUID refreshToken, HttpServletResponse response) {
        refreshTokenService.delete(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // jezeli certyfikat/HTTPS to na true
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

}
