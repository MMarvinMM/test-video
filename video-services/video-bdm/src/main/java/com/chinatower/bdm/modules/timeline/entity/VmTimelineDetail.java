package com.chinatower.bdm.modules.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.io.Serializable;

/**
 * 时间轴详情表(VmTimelineDetail)实体类
 *
 * @author makejava
 * @since 2024-05-27 17:16:15
 */
@Data
@Accessors(chain = true)
@TableName("vm_timeline_detail")
@ApiModel(value = "VmTimelineDetail", description = "时间轴详情表(VmTimelineDetail)实体类")
public class VmTimelineDetail implements Serializable {
    private static final long serialVersionUID = 877803022527824541L;
    /**
     * 时间轴详情ID
     */
    private Long timelineDetailId;
    /**
     * 时间轴信息ID
     */
    private Long timelineId;
    /**
     * 图层ID
     */
    @NotBlank(message = "图层不能为空")
    private String layer;
    /**
     * 图层名称
     */
    private String layerName;
    /**
     * 图层名字空间
     */
    private String layerNamespace;
    /**
     * 年
     */
    @NotBlank(message = "年份不能为空")
    private String year;
    /**
     * 月
     */
    private String month;
    /**
     * 日
     */
    private String day;
    /**
     * 排序
     */
    private String sortNum;
    /**
     * 二维对象属性
     */
    private String geoType;
    /**
     * 服务器地址
     */
    private String rootGeoserverUrl;

    /**
     *  图层名字
     */
    private String customName;
    /**
     * 最大经度
     */
    private String maxx;
    /**
     * 最大纬度
     */
    private String maxy;
    /**
     * 最小经度
     */
    private String minx;
    /**
     * 最小纬度
     */
    private String miny;
    /**
     * 停留时间
     */
    private String stayTime;
    /**
     * 行业信息
     */
    private String industryCode;
    /**
     * 行业应用版本编码
     */
    private String appVerCode;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    private String creator;

    private String modifier;
    /**
     * 0正常 1删除
     */
    private Integer deleteFlag;

    /**
     * 图层类型 1: '矢量图层' 2: '非矢量图层'.3-DEM 4-DSM 5-倾斜摄影 6-激光点云
     */
    private String category;

    /**
     * 3456 图层类型的文件地址
     */
    private String fileUrl;

}
