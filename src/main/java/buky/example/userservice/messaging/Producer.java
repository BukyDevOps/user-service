package buky.example.userservice.messaging;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@NoArgsConstructor
@Component
public class Producer {

    final String productTopic = "product";
    final String topic2 = "topic2";

    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, Serializable> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Message message) {
        CompletableFuture<SendResult<String, Serializable>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return kafkaTemplate.send(topic, message).get();
            } catch (Exception e) {
                //TODO OVDE handle fail to send message event i slicno...
                throw new CompletionException(e);
            }
        });

        future.thenAccept(result -> {
            log.info("Message sent successfully with offset = {}", result.getRecordMetadata().offset());
        }).exceptionally(throwable -> {
            // TODO This code will be executed if an exception occurred during the computation.
            Throwable originalException = throwable.getCause(); // Get the original exception
            log.error("Unable to send message = {} due to: {}", message.toString(), originalException.getMessage());
            return null; // TODO or provide a default value or throw another exception.
        });


    }
}
