package com.chinatower.video.modules.videopreset.dataobject;

import com.chinatower.mongodb.request.QueryEntity;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;

public class CustomQueryEntity extends QueryEntity {

    public CustomQueryEntity() {
    }

    public CustomQueryEntity(Class<?> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("未指定操作的集合类型");
        } else {
            this.put("entityClass", entityClass);
        }
    }

    public CustomQueryEntity between(String field, Date start, Date end) {
        Assert.notNull(field, "查询的 field 为空");
        Assert.notNull(start, "参数为空");
        Assert.notNull(end, "参数为空");
        Assert.isTrue(start.getTime() <= end.getTime(), "传入的开始时间需要小于结束时间");
        HashMap<String, Object> obj = new HashMap(2);
        obj.put("between", new Date[]{start, end});
        this.put(field, obj);
        return this;
    }

}
