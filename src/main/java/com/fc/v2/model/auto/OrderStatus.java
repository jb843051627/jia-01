package com.fc.v2.model.auto;

public enum OrderStatus {

    PENDING_PAYMENT("0", "待付款"),
    PAID("1", "已付款"),
    SERVING("2", "服务中"),
    COMPLETED("3", "已完成"),
    CANCELLED("4", "已取消");

    private final String code;
    private final String name;

    OrderStatus(String code, String name) {
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
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getName();
            }
        }
        return "未知";
    }

    public static OrderStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
