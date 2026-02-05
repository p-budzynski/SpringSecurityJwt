package pl.kurs.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.AuthRequestDto;
import pl.kurs.dto.AuthResponseDto;
import pl.kurs.entity.RefreshToken;
import pl.kurs.entity.User;
import pl.kurs.service.JwtService;
import pl.kurs.service.RefreshTokenService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto requestDto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (authenticate.isAuthenticated()) {

            User principal = (User) authenticate.getPrincipal();
            AuthResponseDto authResponseDto = new AuthResponseDto(jwtService.generateToken(principal));
            RefreshToken refreshToken = refreshTokenService.create(principal);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getId().toString())
                    .httpOnly(true)
                    .secure(false) // jezeli certyfikat/HTTPS to na true
                    .sameSite("Strict")
                    .path("/auth")
                    .maxAge(Duration.ofMinutes(30L))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return authResponseDto;

        }
        throw new UsernameNotFoundException("Invalid user login request");
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@CookieValue("refreshToken") UUID tokenId, HttpServletResponse response) {

        User user = refreshTokenService.consume(tokenId);

        RefreshToken refreshToken = refreshTokenService.create(user);
        String jwt = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getId().toString())
                .httpOnly(true)
                .secure(false) // jezeli certyfikat/HTTPS to na true
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofMinutes(30L))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthResponseDto(jwt);

    }


}
