package com.fc.v2.model.custom;

public class ServiceTypeStatsVO {

    private String name;
    private Integer count;

    public ServiceTypeStatsVO() {
        this.count = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
