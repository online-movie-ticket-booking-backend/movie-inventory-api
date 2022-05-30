package org.example.movie.inventory.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class MovieInventoryResponse {
  private String cityId;
  private List<MovieDetailsResponse> movieDetailsResponseList;
}
