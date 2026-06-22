package com.fc.v2.common.exception;

/**
 * 错误码枚举
 *
 * @author fuce
 */
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请重新登录"),
    FORBIDDEN(403, "权限不足，无法访问"),
    NOT_FOUND(404, "请求资源不存在"),

    INTERNAL_ERROR(500, "系统内部错误"),

    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_ACCOUNT_REPEAT(1004, "用户账号重复"),
    USER_NOT_LOGIN(1005, "用户未登录"),

    PARAM_ERROR(2001, "参数校验失败"),
    PARAM_EMPTY(2002, "参数不能为空"),
    PARAM_INVALID(2003, "参数格式不正确"),

    DATA_NOT_EXIST(3001, "数据不存在"),
    DATA_ALREADY_EXIST(3002, "数据已存在"),
    DATA_DELETE_FAIL(3003, "数据删除失败"),
    DATA_UPDATE_FAIL(3004, "数据更新失败"),
    DATA_INSERT_FAIL(3005, "数据新增失败"),

    FILE_UPLOAD_FAIL(4001, "文件上传失败"),
    FILE_DOWNLOAD_FAIL(4002, "文件下载失败"),
    FILE_SIZE_EXCEED(4003, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(4004, "文件类型不允许"),

    DEMO_MODE(5001, "演示模式，不允许操作"),

    OPERATION_FAIL(9999, "操作失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
