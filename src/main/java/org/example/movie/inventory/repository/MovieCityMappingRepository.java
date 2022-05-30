package org.example.movie.inventory.repository;

import org.example.movie.inventory.entity.MovieCityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface MovieCityMappingRepository extends JpaRepository<MovieCityMapping, Integer> {

  @Query(
      "select mcm from MovieCityMapping mcm"
          + " where UPPER(mcm.cityUniqueId)= UPPER(:cityId) "
          + "and (mcm.movieStartDate<=:scheduleDate and mcm.movieEndDate>=:scheduleDate)")
  List<MovieCityMapping> findMovieIdByCityAndScheduleDate(
      @Param("cityId") String uniqueId, @Param("scheduleDate") Date scheduleDate);
}
