package org.example.movie.inventory.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${kafka.movieBookingApi.movieInventory.topic.request}")
    private String movieInventoryTopicName;


    @Bean
    public NewTopic topicMovieInventoryTopicName() {
        return TopicBuilder.name(movieInventoryTopicName).build();
    }


}