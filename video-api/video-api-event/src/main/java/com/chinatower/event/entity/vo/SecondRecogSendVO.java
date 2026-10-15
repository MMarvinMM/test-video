package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.entity.dto.TranslnglatPeopleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 二次识别配置下发参数
 * @author cuisj
 * @date 2023-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogSendVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警id
     */
    private String taskId;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 二次分析文件信息
     */
    private SecondRecogFileVO fileInfo;
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;


    /**
     * 受理节点编码
     */
    private String acceptNode;


    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 联动摄像机
     */
    private String linkageCamera;

    /**
     * 联动动作 0:抓图 1:抓视频
     */
    private String linkageAction;

    /**
     * 选择算法
     */
    private String selectAlgorithm;

    /**
     * 透传字段
     */
    private SecondExtraFieldInfoVo extraFieldInfo;

    /**
     * 任务类型  1：告警 2：无人机  3.预置位
     */
    private String taskType;



    /**
     * 任务类型  1：对比图
     */
    private String uavRecogSource;

    /**
     * 基准图ID
     */
    private String referenceId;

    /**
     * 是否自动生成告警 0:否 1:是
     */
    private String makeAlarmFlag;

    /**
     * 租户名称
     */
    private String tenantName;

    //摄像机信息及预置位ptz信息
    private TranslnglatPeopleDTO translnglatPeopleDTO;


}
