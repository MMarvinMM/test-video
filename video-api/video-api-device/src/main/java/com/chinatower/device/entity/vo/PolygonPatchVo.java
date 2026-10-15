package com.chinatower.device.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolygonPatchVo extends BaseDataScopeEntity implements Serializable {

    /**
     * 电子围栏id
     * @required false
     */
    private String polygonId;

    /**
     * 设备编号
     * @required true
     */
    @NotBlank
    private String deviceCode;

    /**
     * 通道编号
     * @required true
     */
    @NotBlank
    private String channelCode;

    /**
     * 预置位id
     * @required true
     */
    @NotBlank
    private String presetId;

    /**
     * 预置位名称
     * @required false
     */
    private String presetName;

    /**
     * 图层id
     * @required false
     */
    private String layerId;

    /**
     * 图斑id
     * @required false
     */
    private String featureId;

    /**
     * 电子围栏点位
     */
    private String polygonPoints;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 分页开关  1- 开启  0-关闭
     */
    private String pagingSwitch;

    private String sortParam;

    /**
     * 0-asc:正序  1-desc:倒序
     */
    private String sortType = "asc";

    private String monitorAreaId;


    @TableField(exist = false)
    private int queryType;

    @TableField(exist = false)
    private List<PolygonPatchVo> polygonPatchs;
}
