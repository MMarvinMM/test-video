package com.chinatower.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 导入记录对象 sys_import_record
 * @author wangzic
 * @date 2020-09-07
 */
@ApiModel("导入记录")
@Data
public class SysImportRecord extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "主键编码")
    private String importId;

    @ApiModelProperty(value = "导入类型")
    private String importType;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "文件地址")
    private String fileUrl;

    @ApiModelProperty(value = "导入总数")
    private Integer importTotal;

    @ApiModelProperty(value = "成功总数")
    private Integer successTotal;

    @ApiModelProperty(value = "失败总数")
    private Integer failureTotal;

    @ApiModelProperty(value = "执行状态",allowableValues = "Y已结束,N处理中")
    private String processState;

    @ApiModelProperty(value = "执行信息")
    private String processInfo;

    @ApiModelProperty(value = "所属部门")
    private String deptId;
}
