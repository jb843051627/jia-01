package com.fc.v2.common.conf;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fc.v2.model.auto.TSysUser;
import com.fc.v2.shiro.util.ShiroUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充 createBy、createTime、updateBy、updateTime 字段
 *
 * @author fuce
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    private static final String CREATE_BY = "createBy";
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_BY = "updateBy";
    private static final String UPDATE_TIME = "updateTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        String username = getCurrentUsername();

        if (metaObject.hasSetter(CREATE_BY)) {
            this.strictInsertFill(metaObject, CREATE_BY, String.class, username);
        }
        if (metaObject.hasSetter(CREATE_TIME)) {
            this.strictInsertFill(metaObject, CREATE_TIME, Date.class, now);
        }
        if (metaObject.hasSetter(UPDATE_BY)) {
            this.strictInsertFill(metaObject, UPDATE_BY, String.class, username);
        }
        if (metaObject.hasSetter(UPDATE_TIME)) {
            this.strictInsertFill(metaObject, UPDATE_TIME, Date.class, now);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        String username = getCurrentUsername();

        if (metaObject.hasSetter(UPDATE_BY)) {
            this.strictUpdateFill(metaObject, UPDATE_BY, String.class, username);
        }
        if (metaObject.hasSetter(UPDATE_TIME)) {
            this.strictUpdateFill(metaObject, UPDATE_TIME, Date.class, now);
        }
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录返回 system
     */
    private String getCurrentUsername() {
        try {
            TSysUser user = ShiroUtils.getUser();
            if (user != null && user.getUsername() != null) {
                return user.getUsername();
            }
        } catch (UnavailableSecurityManagerException e) {
            log.debug("非Web上下文，无法获取当前用户，使用 system 作为默认操作人");
        } catch (Exception e) {
            log.debug("获取当前用户失败，使用 system 作为默认操作人: {}", e.getMessage());
        }
        return "system";
    }
}
