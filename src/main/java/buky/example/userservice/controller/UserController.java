package buky.example.userservice.controller;

import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.model.User;
import buky.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('HOST')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.orElseThrow(() -> new NotFoundException("User with this id is not found!"));
    }

    @PutMapping()
    public User updateUserById(Authentication authentication, @RequestBody User updatedUser) {
        return userService.updateUser(authentication.getName(), updatedUser);
    }

    @DeleteMapping()
    public void deleteUserById(Authentication authentication) {
        userService.deleteUser(authentication.getName());
    }
}
