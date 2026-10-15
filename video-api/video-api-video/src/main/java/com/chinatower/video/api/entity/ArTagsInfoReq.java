package com.chinatower.video.api.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * AR标签信息表
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
@Data
@ApiModel(value = "ArTagsInfo对象", description = "AR标签信息表")
public class ArTagsInfoReq implements Serializable {

    @ApiModelProperty("唯一标识")
    private String id;

    @ApiModelProperty("AR实景ID")
    private String arViewId;

    @ApiModelProperty("标签类型（5：摄像机标签 6：物联标签 7：雷达标签 8： 喇叭标签 9：商场 10：景区 11：办公楼 12：学校 13：医院  14:公交站 15：停车场 16：收费站 17：隧道 18：桥梁 19：文本）")
    private Integer tagsType;

    @ApiModelProperty("标签位置（1左上2右上3左下4右下）")
    private Integer tagsPosition;

    @ApiModelProperty("标签名称")
    private String tagsName;

    @ApiModelProperty("标签描述")
    private String tagsDesc;

    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("通道编码")
    private String channelCode;

    @ApiModelProperty("坐标")
    private String coordinate;

    @ApiModelProperty("分辨率x")
    private Integer resolutionx;

    @ApiModelProperty("分辨率y")
    private Integer resolutiony;

    @ApiModelProperty("是否铁塔视角，1是0否")
    private int isTowerView;

    @ApiModelProperty("预置位id")
    private Integer presetId;
    
    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("行业编码")
    private String industryCode;
    
    @ApiModelProperty("用户名")
    private String userName;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 1-点位 2-区域
     */
    private Integer tagPosType;

    /**
     * 区域点位合集
     */
    private String regionPoints;

    /**
     * 额外字段（建筑、道路等标签使用）
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject extraJson;

    /**
     * 关联类型：1-摄像机 2-物联设备 3-雷达设备 4-喇叭设备
     */
    private Integer linkType;

    /**
     * 标签图标url
     */
    private String labelIconUrl;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 标签类别，101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
     */
    private String tagClass;

    /**
     * 标签图标id
     */
    private String labelIconId;

    /**
     * 图标类型url，1.内置图标2.图标库图标
     */
    private String labelIconType;


    private List<String> tagIdList;

    /**
     * 视频,音频文件
     */
    @TableField(exist = false)
    private List<ArVideoFileVO> videoFile;

    /**
     * 视频,音频文件
     */
    @TableField(exist = false)
    private String videoFileString;
    
    @Override
    public String toString() {
        return "ArTagsInfo{" +
        "id=" + id +
        ", arViewId=" + arViewId +
        ", tagsType=" + tagsType +
        ", tagsPosition=" + tagsPosition +
        ", tagsName=" + tagsName +
        ", tagsDesc=" + tagsDesc +
        ", deviceCode=" + deviceCode +
        "}";
    }
}
