package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserDeviceCollectionJNDTO extends BaseDataScopeEntity {
    @ApiModelProperty(value = "收藏对象类型 1.行政区域 2.租户组织 3.标签 4.管理区域 5.摄像机 6.通道 7.物联设备 8.雷达 9.无人机通道 10.喇叭 11.无人机设备")
//    @NotEmpty(message = "收藏对象类型不能为空")
    private  String collObjType;

    @ApiModelProperty(value = "收藏对象编码")
//    @NotEmpty(message = "收藏对象编码不能为空")
    private  List<String> collObjCode;

    @ApiModelProperty(value = "操作标识 1：新增，2：删除")
    @NotNull(message = "操作标识不能为空")
    private Integer optType;


    private String provinceId; //省份编码
    private String cityId; //地市编码
    private String countyId; //区县编码
    private String townCode; //乡镇(街道)编
    private String treeType; //1区域树；2组织树 3标签树
    private String parentCode; //一级组织
    private String code; //二级组织
    private List<Integer> labelIdList; //标签编码集合
    private List<Integer> labelTypeIdList; //标签类型编码集合
}
