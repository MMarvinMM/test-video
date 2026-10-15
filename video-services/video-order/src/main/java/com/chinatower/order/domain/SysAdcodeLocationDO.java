package com.chinatower.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName SysAdcodeLocationDO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/30 9:07
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_adcode_location")
public class SysAdcodeLocationDO implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long locationId;
    
    @TableField("location_name_en")
    private String locationNameEn;
    
    @TableField("location_name_zh")
    private String locationNameZh;
    
    @TableField("latitude")
    private String latitude;
    
    @TableField("longitude")
    private String longitude;
    
    @TableField("adcode")
    private String adcode;
    
    @TableField("adm1_name_en")
    private String adm1_name_en;
    
    @TableField("adm1_name_zh")
    private String adm1_name_zh;
    
    @TableField("adm2_name_en")
    private String adm2_name_en;
    
    @TableField("adm2_name_zh")
    private String adm2_name_zh;

    @TableField("adm1_name_en")
    private String adm1NameEn;

    @TableField("adm1_name_zh")
    private String adm1NameZh;

    @TableField("adm2_name_en")
    private String adm2NameEn;

    @TableField("adm2_name_zh")
    private String adm2NameZh;

}
