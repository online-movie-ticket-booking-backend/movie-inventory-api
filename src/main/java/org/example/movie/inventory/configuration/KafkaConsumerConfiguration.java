package org.example.movie.inventory.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.movie.core.common.schedule.MovieDetailsListResponse;
import org.example.movie.core.common.schedule.MovieInventoryRequest;
import org.example.movie.core.common.schedule.MovieInventoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${kafka.movieBookingApi.groupName}")
    private String groupName;

    @Value("${kafka.movieBookingApi.movieDetails.topic.serialization-class}")
    private String movieDetailsSerializationClass;

    @Value("${kafka.movieBookingApi.movieDetails.topic.response}")
    private String movieDetailsResponseTopic;

    @Value("${kafka.movieBookingApi.movieInventory.topic.serialization-class}")
    private String movieInventorySerializationClass;

    @Value("${kafka.movieBookingApi.movieInventory.topic.request}")
    private String movieInventoryRequestTopic;

    @Bean
    public ConsumerFactory<String, MovieInventoryRequest> consumerFactoryMovieInventoryRequest() {
        return new DefaultKafkaConsumerFactory<>(
                getConfigurationMapForListener(movieInventorySerializationClass));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieInventoryRequest> movieInventoryRequestListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MovieInventoryRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMovieInventoryRequest());
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, MovieInventoryRequest> movieInventoryRequestListenerContainer(
            ConcurrentKafkaListenerContainerFactory<String, MovieInventoryRequest> movieInventoryRequestListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, MovieInventoryRequest> repliesContainer =
                movieInventoryRequestListenerContainerFactory.createContainer(movieInventoryRequestTopic);
        repliesContainer.getContainerProperties().setGroupId(groupName);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MovieInventoryRequest>>
    movieInventoryRequestListenerContainerFactory(KafkaTemplate<String, MovieInventoryResponse> kafkaMovieInventoryReplyTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, MovieInventoryRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMovieInventoryRequest());
        factory.setReplyTemplate(kafkaMovieInventoryReplyTemplate);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, MovieDetailsListResponse> consumerFactoryMovieDetailsListResponse() {
        return new DefaultKafkaConsumerFactory<>(
                getConfigurationMapForListener(movieDetailsSerializationClass));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MovieDetailsListResponse> movieDetailsResponseListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MovieDetailsListResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryMovieDetailsListResponse());
        return factory;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, MovieDetailsListResponse> movieDetailsResponseListenerContainer(
            ConcurrentKafkaListenerContainerFactory<String, MovieDetailsListResponse>
                    movieInventoryResponseResponseListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, MovieDetailsListResponse> repliesContainer =
                movieInventoryResponseResponseListenerContainerFactory.createContainer(movieDetailsResponseTopic);
        repliesContainer.getContainerProperties().setGroupId(groupName);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    private Map<String, Object> getConfigurationMapForListener(String defaultValueType) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, "java.lang.String");
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultValueType);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.movie.core.common.schedule.*");
        return configProps;
    }
}