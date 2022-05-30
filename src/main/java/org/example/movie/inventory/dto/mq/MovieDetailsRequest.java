package org.example.movie.inventory.dto.mq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class MovieDetailsRequest {
    @JsonProperty("movieUniqueId")
    private String movieUniqueId;
}
