package buky.example.userservice;

import buky.example.userservice.model.User;
import buky.example.userservice.model.enums.Role;
import buky.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class UserServiceApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var host = User.builder()
				.username("host")
				.password(passwordEncoder.encode("123"))
				.role(Role.HOST)
				.active(true)
				.build();

		var guest = User.builder()
				.username("guest")
				.password(passwordEncoder.encode("123"))
				.role(Role.GUEST)
				.active(true)
				.build();

		userRepository.saveAll(List.of(host, guest));
	}
}
