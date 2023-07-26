package buky.example.userservice.service;

import buky.example.userservice.dto.LoginDto;
import buky.example.userservice.dto.Token;
import buky.example.userservice.exceptions.BadCredentialsException;
import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.model.User;
import buky.example.userservice.repository.UserRepository;
import buky.example.userservice.security.JWTUserDetails;
import buky.example.userservice.security.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    public Token login(LoginDto loginDto) {
        User usr = userRepository.findUserByUsername(loginDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User is not found!"));

        if(!usr.getActive()) throw new NotFoundException("User is not found!");

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials!");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usr.getRole());

        var user = (JWTUserDetails) authentication.getPrincipal();

        String jwt = tokenUtils.generateToken(user.getUsername(), claims);

        return new Token(jwt);
    }

}
