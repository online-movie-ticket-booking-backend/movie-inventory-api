package org.example.movie.inventory.dto.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class MovieDetailsResponse {
    private String movieUniqueKey;
    private String movieName;
    private String movieRunTime;
    private String movieReleaseDate;
    private String movieCertificationType;
    private String language;
    private String genre;
    private String movieCityIdMapping;
}
