package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SysAlarmTags
 * @Deacription 告警类型标签
 * @Author chenmf
 * @Date 2022/4/26 16:01
 * @Version 1.0
 **/
@Data
@TableName("sys_alarm_tags")
public class SysAlarmTagsDO implements Serializable {
    
    /**
     *  告警类型标签ID
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
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

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

}
