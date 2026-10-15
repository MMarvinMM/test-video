package com.chinatower.video.modules.artags.dataobject;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.chinatower.video.api.entity.ArVideoFileVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArTagTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private String typeName;

    private String parentId;

    private List<ArTagTree> children;

    private String id;

    @ApiModelProperty("AR实景ID")
    private String arViewId;

    @ApiModelProperty("标签类型（5摄像机标签 6物联标签 7雷达标签 8喇叭标签 9商场 10景区 11办公楼 " +
            "12学校 13医院  14公交站 15停车场 16收费站 17隧道 18桥梁 19文本 " +
            "390无人机标签 391图片标签 392网页标签 393PDF标签 394视频标签 395音频标签")
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

    @ApiModelProperty("删除标志位：0正常、1：删除")
    private Integer deleteFlag;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String modifier;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("收藏标志位：0未收藏、1：已收藏")
    @TableField(exist = false)
    private Integer markFlag;

    @ApiModelProperty("客户编码")
    private String tenantId;

    @ApiModelProperty("行业版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;

    @ApiModelProperty("预置位id")
    @TableField(value = "preset_id", updateStrategy= FieldStrategy.IGNORED)
    private Integer presetId;

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
    @TableField(exist = false)
    private String labelIconUrl;

    private String latitude;

    private String longitude;

    private String tagClass;

    @TableField(exist = false)
    private String tagId;

    /**
     * 标签图标id
     */
    private String labelIconId;

    /**
     * 图标类型url，1.内置图标2.图标库图标
     */
    private String labelIconType;

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

}
