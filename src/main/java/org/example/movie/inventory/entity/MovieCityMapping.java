package org.example.movie.inventory.entity;

import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Data
@Entity
@Table(name = "movie_city_mapping", schema = "movie_inventory")
public class MovieCityMapping {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "mcp_id", nullable = false)
    private Integer mcpId;

    @Basic
    @Column(name = "movie_unique_id", nullable = false, length = 255)
    private String movieUniqueId;

    @Basic
    @Column(name = "city_unique_id", nullable = false, length = 255)
    private String cityUniqueId;

    @Basic
    @Column(name = "movie_start_date", nullable = false)
    private Date movieStartDate;

    @Basic
    @Column(name = "movie_end_date", nullable = false)
    private Date movieEndDate;
}
