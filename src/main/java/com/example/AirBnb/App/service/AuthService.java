package com.example.AirBnb.App.service;

import com.example.AirBnb.App.Security.JwtService;
import com.example.AirBnb.App.dto.LoginDto;
import com.example.AirBnb.App.dto.LoginResponseDto;
import com.example.AirBnb.App.dto.SignUpRequestDto;
import com.example.AirBnb.App.dto.UserDto;
import com.example.AirBnb.App.entities.User;
import com.example.AirBnb.App.entities.enums.Role;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import com.example.AirBnb.App.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto){
        Optional<User> user=userRepository.findByEmail(signUpRequestDto.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("Email already Exist");
        }

        User newUser=modelMapper.map(signUpRequestDto,User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));

        newUser=userRepository.save(newUser);
        return modelMapper.map(newUser,UserDto.class);



    }

    public String[] login(LoginDto loginDto){

        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );

        User user= (User) authentication.getPrincipal();

        //Generate Access and Refresh Tokens
        String[] arr= new String[2];
        arr[0] =jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        // Extract UserID from refresh token
        Long userId=jwtService.getUserIdFromToken(refreshToken);


        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with this id:"+userId));

        //generate new access token
        String accessToken= jwtService.generateAccessToken(user);

        return new LoginResponseDto(accessToken);
    }

}
