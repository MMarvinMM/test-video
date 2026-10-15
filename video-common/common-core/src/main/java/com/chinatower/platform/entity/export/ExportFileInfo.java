package com.chinatower.platform.entity.export;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * @TableName bs_export_file_info
 */
@Data
@Accessors(chain = true)
@TableName("bs_export_file_info")
@ApiModel(value = "ExportFileInfo",description = "导出文件信息实体")
public class ExportFileInfo extends BaseEntity{
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;

    /**
     * 导出模块
     */
    @ApiModelProperty(value = "导出模块")
    private String exportModule;

    /**
     * sql执行id
     */
    @ApiModelProperty(value = "执行id")
    private Long sqlId;

    /**
     * 是否已下载(0未下载,1已下载)
     */
    @ApiModelProperty(value = "是否已下载(0未下载,1已下载)")
    private Byte isDownload;

    /**
     * 是否删除(0否,2删除)
     */
    @ApiModelProperty(value = "是否删除(0否,2删除)")
    private Byte delFlag;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ExportFileInfo other = (ExportFileInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getFileUrl() == null ? other.getFileUrl() == null : this.getFileUrl().equals(other.getFileUrl()))
            && (this.getExportModule() == null ? other.getExportModule() == null : this.getExportModule().equals(other.getExportModule()))
            && (this.getSqlId() == null ? other.getSqlId() == null : this.getSqlId().equals(other.getSqlId()))
            && (this.getIsDownload() == null ? other.getIsDownload() == null : this.getIsDownload().equals(other.getIsDownload()))
            && (this.getDelFlag() == null ? other.getDelFlag() == null : this.getDelFlag().equals(other.getDelFlag()))
            && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateBy() == null ? other.getUpdateBy() == null : this.getUpdateBy().equals(other.getUpdateBy()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getFileUrl() == null) ? 0 : getFileUrl().hashCode());
        result = prime * result + ((getExportModule() == null) ? 0 : getExportModule().hashCode());
        result = prime * result + ((getSqlId() == null) ? 0 : getSqlId().hashCode());
        result = prime * result + ((getIsDownload() == null) ? 0 : getIsDownload().hashCode());
        result = prime * result + ((getDelFlag() == null) ? 0 : getDelFlag().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateBy() == null) ? 0 : getUpdateBy().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", exportModule=").append(exportModule);
        sb.append(", sqlId=").append(sqlId);
        sb.append(", isDownload=").append(isDownload);
        sb.append(", delFlag=").append(delFlag);
        sb.append("]");
        return sb.toString();
    }
}