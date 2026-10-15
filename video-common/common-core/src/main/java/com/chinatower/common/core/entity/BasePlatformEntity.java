package com.chinatower.common.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 业务实体基类
 * @Author guyuefei
 * @Date 2022/7/14
 * @Version release_v1.5
 **/
@Data
public class BasePlatformEntity implements Serializable {
    /** 创建人 */
    @ApiModelProperty("创建人")
    private String creator;
    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;
    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;
    /** 更新者 */
    @ApiModelProperty("更新者")
    private String modifier;
    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    /** 删除标记 */
    @ApiModelProperty("删除标记")
    private Integer deleteFlag;

}
