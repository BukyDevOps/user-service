package buky.example.userservice.service;

import buky.example.userservice.dto.LoginDto;
import buky.example.userservice.exceptions.BadCredentialsException;
import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.exceptions.UsernameExistsException;
import buky.example.userservice.model.User;
import buky.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User registration(User user) {
        Optional<User> usr = userRepository.findUserByUsername(user.getUsername());

        if(usr.isPresent()) throw new UsernameExistsException("Username already exists!");

        user.setActive(true);
        return saveUser(user);
    }

    public String login(LoginDto loginDto) {
        User usr = userRepository.findUserByUsername(loginDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User is not found!"));

        if(!usr.getActive()) throw new NotFoundException("User is not found!");

        //TODO security

        if(!usr.getPassword().equals(loginDto.getPassword())) throw new BadCredentialsException("Bad credentials!");

        return "Success!";
    }
}
