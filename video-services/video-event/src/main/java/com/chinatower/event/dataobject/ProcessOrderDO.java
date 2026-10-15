package com.chinatower.event.dataobject;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Getter
@Setter
@TableName("vm_process_order_t")
public class ProcessOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 告警单ID
     */
    private String warningOrderId;

    /**
     * 0 人工抢单   1上级指派
     */
    private String sendType;

    /**
     * 抢单时间
     */
    private Date sendOrderTime;

    /**
     * 处理完成时间
     */
    private Date processOrderTime;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 行政区域编码，以派工单为准
     */
    private String currenAreaId;

    /**
     * 是否空跑 0否  1是  
     */
    private String airRuning;

    /**
     * 处理意见
     */
    private String processOpinion;

    /**
     * 处理图片
     */
    private String processImg;

    /**
     * 处理视频
     */
    private String processVideo;

    /**
     * 处理单状态：0处理中 1处理完成 2无法处理
     */
    private String orderStatus;

    /**
     * 更新时间
     */
    private Date modifierDate;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    private String voice;


}
