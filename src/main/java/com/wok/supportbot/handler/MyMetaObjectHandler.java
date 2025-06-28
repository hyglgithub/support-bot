package com.wok.supportbot.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 在PostgreSQL中，@TableField(fill = FieldFill.INSERT) 和 @TableField(fill = FieldFill.INSERT_UPDATE) 这样的注解本身并不能直接触发数据库级别的自动填充。
 * 这些注解是MyBatis-Plus框架的一部分，它们需要配合 MetaObjectHandler 实现类才能工作。这种机制是在Java应用层面实现的，而非数据库层面。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}