package com.fc.v2.model.custom;

import java.math.BigDecimal;

public class DashboardOverviewVO {

    private Integer todayAppointments;
    private Integer weekAppointments;
    private Integer monthOrders;
    private BigDecimal monthRevenue;
    private Integer pendingDispatch;
    private Integer pendingFollowUp;

    public DashboardOverviewVO() {
        this.todayAppointments = 0;
        this.weekAppointments = 0;
        this.monthOrders = 0;
        this.monthRevenue = BigDecimal.ZERO;
        this.pendingDispatch = 0;
        this.pendingFollowUp = 0;
    }

    public Integer getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(Integer todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public Integer getWeekAppointments() {
        return weekAppointments;
    }

    public void setWeekAppointments(Integer weekAppointments) {
        this.weekAppointments = weekAppointments;
    }

    public Integer getMonthOrders() {
        return monthOrders;
    }

    public void setMonthOrders(Integer monthOrders) {
        this.monthOrders = monthOrders;
    }

    public BigDecimal getMonthRevenue() {
        return monthRevenue;
    }

    public void setMonthRevenue(BigDecimal monthRevenue) {
        this.monthRevenue = monthRevenue;
    }

    public Integer getPendingDispatch() {
        return pendingDispatch;
    }

    public void setPendingDispatch(Integer pendingDispatch) {
        this.pendingDispatch = pendingDispatch;
    }

    public Integer getPendingFollowUp() {
        return pendingFollowUp;
    }

    public void setPendingFollowUp(Integer pendingFollowUp) {
        this.pendingFollowUp = pendingFollowUp;
    }
}
