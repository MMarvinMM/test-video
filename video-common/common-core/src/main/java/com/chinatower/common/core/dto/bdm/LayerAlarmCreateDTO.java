package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 图层生成告警
 * @data 创建时间：2024/5/8 14:42
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAlarmCreateDTO {

    /**
     * 前类型
     */
    private Object preType;
    /**
     * 后类型
     */
    private Object postType;
    /**
     * 告警来源（固定传“9”）
     */
    private Object alarmSource = "9";

    /**
     * 告警类型（耕地卫片执法：4110101 矿产卫片执法：4110102 非农化卫片执法：4110103 非粮化卫片执法：4110104)
     */
    private Object warningTypeId;
    /**
     * 告警原因
     */
    private Object alarmReason;
    /**
     * 告警时间（用户上传图层数据的时间：矿产卫片执法/耕地卫片执法）；影像时间（非农化告警/非粮化告警）
     */
    private Object alarmTime;
    /**
     * 图版中心点经度
     */
    private Object spotCenterLongitude;
    /**
     * 图版中心点维度
     */
    private Object spotCenterLatitude;
    /**
     * 备注
     */
    private Object remark;
    /**
     * 图斑编号
     */
    private Object spotNo;
    /**
     * 城市名
     */
    private Object cityName;
    /**
     * 区县名
     */
    private Object countyName;
    /**
     * 乡镇街道
     */
    private Object townName;
    /**
     * 村
     */
    private Object villageName;
    /**
     * 卫片来源
     */
    private Object regionSource;
    /**
     * 前时相
     */
    private Object preChronology;
    /**
     * 前时相
     */
    private Object postChronology;
    /**
     * 图斑面积
     */
    private Object spotArea;
    /**
     * 图斑周长
     */
    private Object spotPerimeter;
    /**
     * 变化类型
     */
    private Object changeType;
    /**
     * 事件描述（展示该图层模板下的扩展字段）
     */
    private Object description;
    /**
     * 面积（显示用户导入图层数据中的“面积”字段值）
     */
    private Object area;
    /**
     *土地利用类型（显示用户导入图层数据中的“土地利用类型”字段值）
     */
    private Object landUseType;
    /**
     *行业编码
     */
    private Object industryCode;
    /**
     *行业版本
     */
    private Object appVerCode;
    /**
     *租户id
     */
    private Object tenantId;
    /**
     *影像时间（显示用户导入图层数据中的“影像时间”字段值）
     */
    private Object imageTime;
    /**
     *图斑唯一标识
     */
    private Object bsm;
    /**
     *图层id
     */
    private Object layerId;
    /**
     *是否已质检（显示用户导入图层数据中的“影像时间”字段值）
     */
    private Object detectionFlag;
    /**
     * 是否已质检（显示用户导入图层数据中的“影像时间”字段值）
     */
    private Object extend;
    /**
     * 是否二次识别 1-是；0-否
     */
    private String isSecondIdentify;

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
}
