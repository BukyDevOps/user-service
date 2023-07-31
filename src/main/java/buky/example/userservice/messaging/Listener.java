package buky.example.userservice.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Listener {
    @KafkaListener(topics = {"product", "topic2"}, containerFactory = "kafkaListenerContainerFactory")
    public void newProductListener(Message product) {
        log.info("\n*\n*\n*\n*\n*\n*\n*\n*");
        log.info("----------------------------------");
        log.info("Get request " + product.toString());
    }}
