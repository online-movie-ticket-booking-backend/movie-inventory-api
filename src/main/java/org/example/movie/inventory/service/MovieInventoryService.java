package org.example.movie.inventory.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.movie.core.common.schedule.MovieDetailsResponse;
import org.example.movie.core.common.schedule.MovieInventoryRequest;
import org.example.movie.core.common.schedule.MovieInventoryResponse;
import org.example.movie.inventory.repository.MovieCityMappingRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static org.example.movie.inventory.utility.Converter.convertToSQLDate;

@Data
@Slf4j
@Service
@AllArgsConstructor
public class MovieInventoryService {

    private final MovieCityMappingRepository movieCityMappingRepository;

    public MovieInventoryResponse fetchMovieSchedule(String uniqueId, MovieInventoryRequest movieInventoryRequest) {
        return MovieInventoryResponse.of()
                .setMovieDetailsResponseList(
                        movieCityMappingRepository
                                .findMovieIdByCityAndScheduleDate(
                                        movieInventoryRequest.getCityId(),
                                        convertToSQLDate(movieInventoryRequest.getScheduleDate()))
                                .stream()
                                .map(movieCityMapping ->
                                        MovieDetailsResponse
                                                .of()
                                                .setMovieUniqueKey(movieCityMapping.getMovieUniqueId())
                                                .setMovieCityIdMapping(movieCityMapping.getMcpId() + ""))
                                .collect(Collectors.toList()));
    }
}
