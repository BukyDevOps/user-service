package buky.example.userservice.messaging;

import buky.example.userservice.messaging.messages.AccommodationRatingMessage;
import buky.example.userservice.messaging.messages.HostRatingMessage;
import buky.example.userservice.messaging.messages.UserDeletionResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    public <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> messageType) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consuming");

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> classMap = new HashMap<>();
        classMap.put(messageType.getName(), messageType);
        typeMapper.setIdClassMapping(classMap);
        typeMapper.addTrustedPackages("*");

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(messageType);
        jsonDeserializer.setTypeMapper(typeMapper);
        jsonDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jsonDeserializer);
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consuming");
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HostRatingMessage> hostRatingListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HostRatingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(HostRatingMessage.class));
        log.info("Configure concurrent consumer Kafka");
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccommodationRatingMessage> accommodationRatingListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccommodationRatingMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(AccommodationRatingMessage.class));
        log.info("Configure concurrent consumer Kafka");
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDeletionResponseMessage> userDeletionResponse() {
        ConcurrentKafkaListenerContainerFactory<String, UserDeletionResponseMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(UserDeletionResponseMessage.class));
        log.info("Configure concurrent consumer Kafka");
        return factory;
    }

}

