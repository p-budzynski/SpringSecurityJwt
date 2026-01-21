package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.dto.AuthRequestDto;
import pl.kurs.dto.AuthResponseDto;
import pl.kurs.service.JwtService;
import pl.kurs.service.UserService;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @PostMapping
    public AuthResponseDto login(@RequestBody AuthRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getLogin(), requestDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (authenticate.isAuthenticated()) {
            return new AuthResponseDto(jwtService.generateToken(requestDto.getLogin()));
        }
        throw new UsernameNotFoundException("Invalid user login request");
    }



}
