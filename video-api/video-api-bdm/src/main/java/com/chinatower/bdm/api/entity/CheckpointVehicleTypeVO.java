package com.chinatower.bdm.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 车辆类型表实体视图，对应“vm_checkpoint_vehicle_type_t”
 *
 * @author Liqi
 * @date 2024/01/25
 * @requestID
 */
@Data
@TableName("vm_checkpoint_vehicle_type_t")
public class CheckpointVehicleTypeVO extends BaseVehicle {

    private static final long serialVersionUID = 1L;

    /**
     * 车辆类型
     */
    @TableField("vehicle_name")
    private String vehicleType;

    /**
     * 车辆类型名称
     */
    private String vehicleName;




}
