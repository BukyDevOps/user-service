package buky.example.userservice.service;

import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.model.User;
import buky.example.userservice.repository.UserRepository;
import buky.example.userservice.security.JWTUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("No user found with username '%s'.", username)));

        return new JWTUserDetails(user);
    }

}
