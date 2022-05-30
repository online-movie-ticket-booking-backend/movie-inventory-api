package org.example.movie.inventory.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.inventory.configuration.ExchangeConfiguration;
import org.example.movie.inventory.dto.mq.MovieDetailsRequest;
import org.example.movie.inventory.dto.mq.MovieDetailsResponse;
import org.example.movie.inventory.dto.mq.MovieInventoryRequest;
import org.example.movie.inventory.dto.mq.MovieInventoryResponse;
import org.example.movie.inventory.repository.MovieCityMappingRepository;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.movie.inventory.utility.Converter.convertToSQLDate;

@Data
@Slf4j
@Service
@AllArgsConstructor
public class MovieInventoryService {

  private final RabbitTemplate rabbitTemplate;
  private final ExchangeConfiguration movieDetailsExchange;
  private final MovieCityMappingRepository movieCityMappingRepository;

  public MovieInventoryResponse fetchMovieSchedule(MovieInventoryRequest movieInventoryRequest) {
    return MovieInventoryResponse.of()
       .setMovieDetailsResponseList(
           movieCityMappingRepository
               .findMovieIdByCityAndScheduleDate(
                   movieInventoryRequest.getCityId(),
                   convertToSQLDate(movieInventoryRequest.getScheduleDate()))
               .stream()
               .map(
                   movieCityMapping ->
                       getMovieDetails(
                               MovieDetailsRequest.of()
                                   .setMovieUniqueId(movieCityMapping.getMovieUniqueId()))
                           .setMovieCityIdMapping(Integer.toString(movieCityMapping.getMcpId())))
               .collect(Collectors.toList()));
  }

  private MovieDetailsResponse getMovieDetails(MovieDetailsRequest movieDetailsRequest) {
    CorrelationData coreCorrelationData = new CorrelationData(UUID.randomUUID().toString());
    return sendMessageToExchange(movieDetailsRequest, coreCorrelationData);
  }

  private MovieDetailsResponse sendMessageToExchange(
      MovieDetailsRequest message, CorrelationData correlationData) {
    return rabbitTemplate.convertSendAndReceiveAsType(
        movieDetailsExchange.getExchange(),
        movieDetailsExchange.getRoutingKey(),
        message,
        messageProperties -> {
          messageProperties.getMessageProperties().setReplyTo("movieDetailsQueue");
          return messageProperties;
        },
        correlationData,
        ParameterizedTypeReference.forType(MovieDetailsResponse.class));
  }
}
