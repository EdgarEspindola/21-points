package org.jhipster.health.web.rest.dto;


import org.jhipster.health.domain.User;

import java.time.LocalDate;

/**
 * Created by kadasoftware on 19/01/17.
 */
public class PointsPerWeek {
    private LocalDate week;
    private Integer points;

    public PointsPerWeek(LocalDate week, Integer points) {
        this.week = week;
        this.points = points;
    }

    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "PointsPerWeek{" +
            "week=" + week +
            ", points=" + points +
            '}';
    }
}
