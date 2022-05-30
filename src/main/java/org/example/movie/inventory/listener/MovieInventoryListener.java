package org.example.movie.inventory.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.inventory.dto.mq.MovieDetailsResponse;
import org.example.movie.inventory.dto.mq.MovieInventoryRequest;
import org.example.movie.inventory.dto.mq.MovieInventoryResponse;
import org.example.movie.inventory.service.MovieInventoryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MovieInventoryListener {

  private final MovieInventoryService movieInventoryService;

  @RabbitListener(queues = "movieInventoryQueue")
  public MovieInventoryResponse receiveMovieMessage(MovieInventoryRequest movieInventoryRequest) {
    return movieInventoryService.fetchMovieSchedule(movieInventoryRequest);
  }
}
