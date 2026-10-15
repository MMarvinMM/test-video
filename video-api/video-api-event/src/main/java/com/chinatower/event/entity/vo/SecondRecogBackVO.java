package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 二次识别配置参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogBackVO{

    /**
     * 告警id
     */
    private String taskId;

    /**
     * 二次分析结果
     */
    private String secondJudgeResult;

    /**
     * 二次分析图片
     */
    private String originalPictureUrl;


    /**
     * 二次分析框图url
     */
    private String alarmPictureUrl;

    /**
     * 二次分析视频
     */
    private String alarmVideoUrl;


    /**
     * 报警区域
     */
    private List<AlarmOriginalAreaVO> alarmArea;

    /**
     * 扩展字段
     */
    private List<SecondRecogBackExtendVO> extendField;


    private SecondExtraFieldInfoVo extraFieldInfo;


    /**
     * 扩展字段
     */
    private String extraFieldAlarm;


    /**
     * 分析状态   1-分析中，2-分析完成 3-分析失败
     */
    private String analyType;


    /**
     * 是否二次分析
     */
    private String ifSecondJudge;


    /**
     * 表名标识  1:压制 2.告警 3.研判
     * */
    private String secondRecogTableType;

    /**
     * 选择算法
     */
    private String selectAlgorithm;

    /**
     * 表名称
     */
    private String secondRecogTableName;

    /**
     * 节点编码
     * */
    private String acceptNode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 基准图url
     */
    private String baselinePicUrl;

}
