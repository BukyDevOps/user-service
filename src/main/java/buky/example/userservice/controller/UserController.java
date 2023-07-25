package buky.example.userservice.controller;

import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.model.User;
import buky.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.orElseThrow(() -> new NotFoundException("User with this id is not found!"));
    }

    @PutMapping("/{username}")
    public User updateUserById(@PathVariable String username, @RequestBody User updatedUser) {
        //TODO iz tokena uzimati
        return userService.updateUser(username, updatedUser);
    }

    @DeleteMapping("/{username}")
    public void updateUserById(@PathVariable String username) {
        //TODO iz tokena uzimati kad se odradi security
        userService.deleteUser(username);
    }
}
