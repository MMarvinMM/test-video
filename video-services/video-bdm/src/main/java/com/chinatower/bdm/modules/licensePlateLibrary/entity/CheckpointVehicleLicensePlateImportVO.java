package com.chinatower.bdm.modules.licensePlateLibrary.entity;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.bdm.api.entity.BaseVehicle;
import com.chinatower.common.annotation.Excel;
import lombok.Data;

@Data
@TableName("vm_checkpoint_vehicle_license_plate_t")
public class CheckpointVehicleLicensePlateImportVO extends BaseVehicle {

    /**
     * 车牌号
     */
    @Excel(name = "车牌号（必填）",type= Excel.Type.IMPORT)
    @TableField("vehicle_number")
    private String vehicleNumber;


    /**
     * 生效时段
     */
    @Excel(name = "生效时间段（必填）",type= Excel.Type.IMPORT)
    @TableField("valid_period")
    private String validPeriod;

}
