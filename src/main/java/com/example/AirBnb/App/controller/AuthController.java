package com.example.AirBnb.App.controller;

import com.example.AirBnb.App.dto.LoginDto;
import com.example.AirBnb.App.dto.LoginResponseDto;
import com.example.AirBnb.App.dto.SignUpRequestDto;
import com.example.AirBnb.App.dto.UserDto;
import com.example.AirBnb.App.service.AuthService;
import com.example.AirBnb.App.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto=authService.signUp(signUpRequestDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        //login
        String[] tokens=authService.login(loginDto);
        Cookie cookie=new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){
        //Extract refresh token from Cookie
        String refreshToken= Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName())).
                findFirst()
                .map(Cookie::getValue).orElseThrow(()-> new AuthenticationServiceException("Refresh token not fond inside cookie"));
        LoginResponseDto loginResponseDto=authService.refreshToken(refreshToken);
        return ResponseEntity.ok(loginResponseDto);
    }
}
