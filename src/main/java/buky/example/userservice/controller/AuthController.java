package buky.example.userservice.controller;

import buky.example.userservice.dto.LoginDto;
import buky.example.userservice.dto.Token;
import buky.example.userservice.model.User;
import buky.example.userservice.security.TokenUtils;
import buky.example.userservice.service.AuthService;
import buky.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final TokenUtils tokenUtils;

    @PostMapping("/login")
    public Token login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/registration")
    public User registration(@RequestBody User user) {
        return userService.registration(user);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<Void> authenticateUser(@RequestHeader("Authorization") String jwtToken,
                                                 @RequestHeader("X-User-Role") List<String> userRole) {
        if (tokenUtils.isValidUser(jwtToken, userRole)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
