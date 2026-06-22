package com.fc.v2.model.custom;

import java.math.BigDecimal;

public class DailyTrendVO {

    private String date;
    private Integer appointmentCount;
    private BigDecimal revenue;

    public DailyTrendVO() {
        this.appointmentCount = 0;
        this.revenue = BigDecimal.ZERO;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(Integer appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
