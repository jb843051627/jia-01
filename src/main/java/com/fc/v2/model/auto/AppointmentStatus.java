package com.fc.v2.model.auto;

public enum AppointmentStatus {

    PENDING("0", "待确认"),
    CONFIRMED("1", "已确认"),
    SERVING("2", "服务中"),
    COMPLETED("3", "已完成"),
    CANCELLED("4", "已取消");

    private final String code;
    private final String name;

    AppointmentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        if (code == null) {
            return "未知";
        }
        for (AppointmentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getName();
            }
        }
        return "未知";
    }

    public static AppointmentStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AppointmentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
