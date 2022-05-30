package org.example.movie.inventory.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class MovieInventoryRequest {
  private String cityId;
  private String scheduleDate;
}
