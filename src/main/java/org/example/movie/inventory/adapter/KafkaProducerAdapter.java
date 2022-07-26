package org.example.movie.inventory.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.movie.core.common.schedule.MovieDetails;
import org.example.movie.core.common.schedule.MovieDetailsListRequest;
import org.example.movie.core.common.schedule.MovieDetailsListResponse;
import org.example.movie.core.common.schedule.MovieDetailsResponse;
import org.example.movie.core.common.schedule.MovieInventoryRequest;
import org.example.movie.core.common.schedule.MovieInventoryResponse;
import org.example.movie.inventory.service.MovieInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaProducerAdapter {

    private ReplyingKafkaTemplate<String, MovieDetailsListRequest, MovieDetailsListResponse> kafkaMovieDetailsReplyTemplate;

    @Value("${kafka.movieBookingApi.movieDetails.topic.request}")
    private String movieDetailsTopicName;

    private MovieInventoryService movieInventoryService;

    @Autowired
    public void setKafkaMovieDetailsReplyTemplate(
            ReplyingKafkaTemplate<String, MovieDetailsListRequest, MovieDetailsListResponse> kafkaMovieDetailsReplyTemplate) {
        this.kafkaMovieDetailsReplyTemplate = kafkaMovieDetailsReplyTemplate;
    }

    @Autowired
    public void setMovieDetailsTopicName(MovieInventoryService movieInventoryService) {
        this.movieInventoryService = movieInventoryService;
    }

    public MovieDetailsListResponse kafkaMovieDetailsListRequestReplyObject(String uniqueId,
                                                                            MovieDetailsListRequest movieDetailsRequest) throws ExecutionException, InterruptedException, TimeoutException {
        ProducerRecord<String, MovieDetailsListRequest> record =
                new ProducerRecord<>(movieDetailsTopicName, uniqueId, movieDetailsRequest);
        RequestReplyFuture<String, MovieDetailsListRequest, MovieDetailsListResponse> replyFuture =
                kafkaMovieDetailsReplyTemplate.sendAndReceive(record);
        SendResult<String, MovieDetailsListRequest> sendResult =
                replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);
        ConsumerRecord<String, MovieDetailsListResponse> consumerRecord =
                replyFuture.get(10, TimeUnit.SECONDS);
        return consumerRecord.value();
    }

    @KafkaListener(topics = "${kafka.movieBookingApi.movieInventory.topic.request}",
            containerFactory = "movieInventoryRequestListenerContainerFactory",
            groupId = "${kafka.movieBookingApi.groupName}"
    )
    @SendTo()
    public MovieInventoryResponse receive(
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uniqueId,
            MovieInventoryRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Received Message with : {}", uniqueId);
        MovieInventoryResponse movieInventoryResponse =
                movieInventoryService.fetchMovieSchedule(uniqueId, request);

        Map<String, MovieDetails> movieMap = kafkaMovieDetailsListRequestReplyObject(uniqueId,
                MovieDetailsListRequest.of().setMovieUniqueIdList(
                        movieInventoryResponse
                                .getMovieDetailsResponseList()
                                .stream()
                                .map(MovieDetailsResponse::getMovieUniqueKey)
                                .collect(Collectors.toList())))
                .getMovieDetailsMap();
        movieInventoryResponse
                .getMovieDetailsResponseList()
                .forEach(movieDetailsResponse -> {
                            MovieDetails movieDetails = movieMap.get(movieDetailsResponse.getMovieUniqueKey());
                            movieDetailsResponse.setMovieName(movieDetails.getMovieName());
                            movieDetailsResponse.setGenre(movieDetails.getGenre());
                            movieDetailsResponse.setMovieName(movieDetails.getMovieName());
                            movieDetailsResponse.setLanguage(movieDetails.getLanguage());
                        }
                );
        return movieInventoryResponse;
    }
}