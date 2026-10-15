package com.chinatower.video.modules.query.entity;

import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.video.api.entity.PresetData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArTagMoveRes {
    /**
     * 标签类别 101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
     */
    private String tagClass;

    /**
     * 标签类型vm_business_dict_type表
     */
    private String tagsType;

    /**
     * 标签编码
     */
    private String tagId;

    /**
     * 标签名称
     */
    private String tagsName;

    private String percentU;

    private String percentV;

    // 打点经度
    private double longitude;
    // 打点纬度
    private double latitude;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PresetData> vertexPtzList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeviceModelVO deviceModel;

    //中心点ptz
    private PresetData centerPointPtz;

    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private Float pan;
    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private Float tilt;
    /**
     * ⽔平参数[0, 3600]
     * @required true
     */
    private Float zoom;

    private String polygonId;

    private String featureId;

    private double maxx;
    private double maxy;
    private double minx;
    private double miny;


    /**
     * 图层类型名称
     */
    private String layerTypeId;

    /**
     * 图层类型名称
     */
    private String layerTypeName;

    private String centerLongitude;

    private String centerLatitude;

    private String deviceCode;

}
