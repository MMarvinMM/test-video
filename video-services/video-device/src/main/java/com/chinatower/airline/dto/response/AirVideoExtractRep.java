package com.chinatower.airline.dto.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @author lyy
 * @description
 * @date 2024/12/03 10:22
 */
@Data
public class AirVideoExtractRep {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 抽帧频率
     */
    private String frequency;

    /**
     * 抽帧时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date extractTime;

    /**
     * 抽帧图片实际数量
     */
    private Integer extractCount;

    /**
     * 抽帧图片总数量
     */
    private Integer extractTotalCount;

    /**
     * 抽帧状态：0 运行中，1 已完成，2 已取消
     */
    private String extractStatus;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 删除标记位
     */
    private String deleteFlag;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 行业
     */
    private String industryCode;

    /**
     * 租户ID
     */
    private String tenantId;

}
