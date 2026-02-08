package pl.kurs.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.AuthRequestDto;
import pl.kurs.dto.AuthResponseDto;
import pl.kurs.entity.RefreshToken;
import pl.kurs.entity.User;
import pl.kurs.security.UserLogin;
import pl.kurs.security.JwtService;
import pl.kurs.service.RefreshTokenService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        UserLogin principal = (UserLogin) authenticate.getPrincipal();

        AuthResponseDto authResponseDto = new AuthResponseDto(jwtService.generateToken(principal));
        RefreshToken refreshToken = refreshTokenService.create(principal.getId());

        addRefreshTokenCookie(response, refreshToken.getId());

        return authResponseDto;
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@CookieValue(value = "refreshToken", required = false) UUID tokenId, HttpServletResponse response) {
        if (tokenId == null) {
            throw new AuthenticationServiceException("Refresh token is missing");
        }

        User user = refreshTokenService.consume(tokenId);
        RefreshToken refreshToken = refreshTokenService.create(user.getId());

        UserLogin userLogin = new UserLogin(user);
        String jwt = jwtService.generateToken(userLogin);

        addRefreshTokenCookie(response, refreshToken.getId());
        return new AuthResponseDto(jwt);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(@CookieValue(value = "refreshToken", required = false) UUID refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            refreshTokenService.delete(refreshToken);
        }
        clearCookie(response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout-all")
    public void logoutAll(@AuthenticationPrincipal UserLogin principal, HttpServletResponse response) {

        if (principal != null) {
            refreshTokenService.deleteAllByUserId(principal.getId());
        }

        clearCookie(response);
    }

    private void clearCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // jezeli certyfikat/HTTPS to na true
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void addRefreshTokenCookie(HttpServletResponse response, UUID tokenId) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenId.toString())
                .httpOnly(true)
                .secure(false) // jezeli certyfikat/HTTPS to na true
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMillis(refreshTokenExpirationMs))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
