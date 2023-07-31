package buky.example.userservice;

import buky.example.userservice.messaging.Message;
import buky.example.userservice.messaging.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class UserServiceApplication implements CommandLineRunner {

	private final Producer producer;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("\n\n\n\n\n\n\n\n");
		log.info("[User-Service] send message 229L -> yay to topic PRODUCT");
		producer.send("product", Message.builder().id(229L).content("yey").build());
		log.info("\n\n\n\n\n\n\n\n");
		log.info("[User-Service] send message 229L -> yay to topic TOPIC2");
		producer.send("topic2", Message.builder().id(229L).content("yey").build());
	}
}
