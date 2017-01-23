package org.jhipster.health.web.rest.dto;

import org.jhipster.health.domain.BloodPressure;

import java.util.List;

/**
 * Created by kadasoftware on 19/01/17.
 */
public class BloodPressureByPeriod {
    private String period;
    private List<BloodPressure> readings;

    public BloodPressureByPeriod(String period, List<BloodPressure> readings) {
        this.period = period;
        this.readings = readings;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<BloodPressure> getReadings() {
        return readings;
    }

    public void setReadings(List<BloodPressure> readings) {
        this.readings = readings;
    }

    public String toString() {
        return "BloodPressureByPeriod{" +
            "period='" + period + '\'' +
            ", readings=" + readings +
            '}';
    }
}
