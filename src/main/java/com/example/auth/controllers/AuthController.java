package com.example.auth.controllers;

import com.example.auth.domain.user.User;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.dto.RegisterRequestDTO;
import com.example.auth.dto.ResponseDTO;
import com.example.auth.infra.security.TokenService;
import com.example.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private  final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body){

        User user = repository.findByEmail(body.email())
                .orElseThrow(()-> new RuntimeException("Could not find user: " + body.email()));

        if(passwordEncoder.matches( body.password(),user.getPassword())){
            String token = this.tokenService.generateToken(user);
            return  ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) throws Exception {
        Optional<User> user = repository.findByEmail(body.email());
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            newUser.setPassword(passwordEncoder.encode(body.password()));
            try{
                repository.save(newUser);
                String token = this.tokenService.generateToken(newUser);
               return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));

            }catch(Exception exception){
                throw  new Exception("Error while creating new user");
            }

        }else {
            throw new RuntimeException("User with " + body.email() + " exist");
        }
    }
}
