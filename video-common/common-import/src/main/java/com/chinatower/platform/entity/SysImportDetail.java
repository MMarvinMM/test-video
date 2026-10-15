package com.chinatower.platform.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.platform.common.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 导入记录明细对象 sys_import_detail
 * @author wangzic
 * @date 2020-09-07
 */
@ApiModel("导入记录明细")
@Data
public class SysImportDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "导入主键")
    private Long id;

    @ApiModelProperty(value = "导入记录编码")
    private String importId;

    @ApiModelProperty(value = "导入行号")
    private Integer importRow;

    @ApiModelProperty(value = "处理结果",allowableValues = "S成功,F失败")
    private String handleResult;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    public SysImportDetail() {
    }

    public SysImportDetail(String importId, Integer importRow, String handleResult, String failReason) {
        this.importId = importId;
        this.importRow = importRow;
        this.handleResult = handleResult;
        this.failReason = failReason;
    }

    public SysImportDetail(String importId, Integer importRow) {
        this.importId = importId;
        this.importRow = importRow;
    }

    public void setFailReason(String failReason) {
        this.setHandleResult(Constants.F);
        this.failReason = failReason;
    }

}
