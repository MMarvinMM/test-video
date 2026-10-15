package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SysAlarmTags
 * @Deacription 告警类型标签关联
 * @Author chenmf
 * @Date 2022/4/26 16:01
 * @Version 1.0
 **/
@Data
@TableName("sys_alarm_tags_type_rel")
public class SysAlarmTagsTypeRelDO implements Serializable {
    
    /**
     *  ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     *  告警类型标签ID
     */
    private Long tagId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 告警类型ID
     */
    private String alarmTypeId;
    
    /**
     * 告警类型值
     */
    private String typeValue;

    /**
     * 租户id
     *
     */
    private String tenantId;
    
    /**
     * 创建人
     */
    private String creator;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新人
     */
    private String modifier;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 0正常 1删除
     */
    private String deleteFlag;
    

}
