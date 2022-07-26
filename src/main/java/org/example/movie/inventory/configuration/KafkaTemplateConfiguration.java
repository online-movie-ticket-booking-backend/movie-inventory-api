package org.example.movie.inventory.configuration;

import org.example.movie.core.common.schedule.MovieDetailsListRequest;
import org.example.movie.core.common.schedule.MovieDetailsListResponse;
import org.example.movie.core.common.schedule.MovieInventoryRequest;
import org.example.movie.core.common.schedule.MovieInventoryResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaTemplateConfiguration {

    @Bean
    public KafkaTemplate<String, MovieInventoryResponse> kafkaMovieInventoryReplyTemplate(ProducerFactory<String,
            MovieInventoryResponse> producerFactoryMovieInventoryResponse) {
        return new KafkaTemplate<>(producerFactoryMovieInventoryResponse);
    }

    @Bean
    public ReplyingKafkaTemplate<String, MovieDetailsListRequest, MovieDetailsListResponse> kafkaMovieDetailsReplyTemplate(
            ProducerFactory<String, MovieDetailsListRequest> producerFactoryMovieDetailsListRequest,
            ConcurrentMessageListenerContainer<String, MovieDetailsListResponse> movieDetailsRequestListenerContainer) {
        return new ReplyingKafkaTemplate<>(producerFactoryMovieDetailsListRequest, movieDetailsRequestListenerContainer);
    }
}