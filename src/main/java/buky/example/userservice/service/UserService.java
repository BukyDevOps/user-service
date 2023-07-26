package buky.example.userservice.service;

import buky.example.userservice.exceptions.ActiveReservationExistsException;
import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.exceptions.UsernameExistsException;
import buky.example.userservice.model.User;
import buky.example.userservice.model.enums.Role;
import buky.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if(userHasActiveReservations(user))
            throw new ActiveReservationExistsException("Active reservation exists!");


        user.setActive(false);
        if(user.getRole().equals(Role.HOST))
            deleteMyAccomodations(user.getId());

        userRepository.save(user);
    }

    private void deleteMyAccomodations(Long id) {
        //TODO obrisati sav smjestaj, kafka opet
    }

    private boolean userHasActiveReservations(User user) {
        //TODO kafka i to
        return false;
    }

    public User registration(User user) {
        Optional<User> usr = userRepository.findUserByUsername(user.getUsername());

        if(usr.isPresent()) throw new UsernameExistsException("Username already exists!");

        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return saveUser(user);
    }

    public User updateUser(String username, User updatedUser) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User is not found!"));

        if(!username.equals(updatedUser.getUsername())
                && userRepository.findUserByUsername(updatedUser.getUsername()).isPresent())
            throw new UsernameExistsException("Username already exists!");

        if(!updatedUser.getPassword().equals(user.getPassword())) {
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(updatedUser);
    }
}
