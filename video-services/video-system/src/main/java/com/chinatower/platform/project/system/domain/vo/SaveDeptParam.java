package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.custom.entity.SatelliteInfoVO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

@Data
public class SaveDeptParam extends SysDept {

    @ApiModelProperty("摄像机权限")
    private List<String> videoList;

    @ApiModelProperty("物联设备权限")
    private List<String> iotList;

    @ApiModelProperty("雷达权限")
    private List<String> radarList;

    @ApiModelProperty("管理区域")
    private List<String> streetList;

    @ApiModelProperty("卫星火点数据")
    private List<SatelliteInfoVO> satelliteIList;

    @ApiModelProperty("无人机权限")
    private List<String> uavList;

    @ApiModelProperty("大喇叭权限")
    private List<String> hornList;

    @ApiModelProperty("执法记录仪权限")
    private List<String> LERList;

    /**
     * 告警类型权限
     */
    private List<OrganAlarmTypeVO> alarmTypeList;

    @ApiModelProperty("图层权限")
    private List<String> layerList;

    @ApiModelProperty("机场权限")
    private List<String> uavNestList;

}
