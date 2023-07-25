package buky.example.userservice.controller;

import buky.example.userservice.dto.LoginDto;
import buky.example.userservice.model.User;
import buky.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/registration")
    public User registration(@RequestBody User user) {
        return userService.registration(user);
    }
}
