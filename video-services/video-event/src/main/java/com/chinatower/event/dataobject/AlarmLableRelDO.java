package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@TableName("vm_alarm_lable_rel")
public class AlarmLableRelDO implements Serializable {

    /**
     *  ID
     */
    /*@TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     *  告警标签ID
     */
    private Long lableId;

    /**
     * 标签名称
     */
    private String lableName;

    /**
     * 告警ID
     */
    private String waringOrderId;


    /**
     * 表时间
     *
     */
    private String tableTime;

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


}
