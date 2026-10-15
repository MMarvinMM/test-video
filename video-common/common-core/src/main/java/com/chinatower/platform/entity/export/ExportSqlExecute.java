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
 * @TableName bs_export_sql_execute
 */
@Data
@Accessors(chain = true)
@TableName("bs_export_sql_execute")
@ApiModel(value = "ExportSqlExecute",description = "导出sql记录实体")
public class ExportSqlExecute extends BaseEntity{
    /**
     * 主键id 任务编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * sql语句
     */
    @ApiModelProperty(value = "sql语句")
    private String sqlValue;

    /**
     * 请求参数记录
     */
    @ApiModelProperty(value = "请求参数记录")
    private String requestParam;

    /**
     * 导出模块
     */
    @ApiModelProperty(value = "导出归属模块")
    private String exportModule;

    /**
     * 导出的实体类
     */
    @ApiModelProperty(value = "导出的实体类")
    private String exportClass;

    /**
     * 导出数据处理策略实体类型
     */
    private String exportStrategyClass;

    /**
     * 导出数据处理策略参数
     */
    private String exportStrategyParam;

    /**
     * 是否已执行(0未执行,1执行中,2已执行,3执行失败)
     */
    @ApiModelProperty(value = "是否已执行(0未执行,1执行中,2已执行,3执行失败)")
    private Byte isExecute;

    /**
     * 导出次数
     */
    @ApiModelProperty(value = "导出次数")
    private Byte exportTimes;

    /**
     * 是否删除(0否,2删除)
     */
    @ApiModelProperty(value = "是否删除(0否,2删除)")
    private Byte delFlag;

    /**
     * 数据库名
     */
    @ApiModelProperty(value = "数据库名")
    private String exportDatabase;

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
        ExportSqlExecute other = (ExportSqlExecute) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSqlValue() == null ? other.getSqlValue() == null : this.getSqlValue().equals(other.getSqlValue()))
            && (this.getRequestParam() == null ? other.getRequestParam() == null : this.getRequestParam().equals(other.getRequestParam()))
            && (this.getExportModule() == null ? other.getExportModule() == null : this.getExportModule().equals(other.getExportModule()))
            && (this.getExportClass() == null ? other.getExportClass() == null : this.getExportClass().equals(other.getExportClass()))
            && (this.getExportStrategyClass() == null ? other.getExportStrategyClass() == null : this.getExportStrategyClass().equals(other.getExportStrategyClass()))
            && (this.getExportStrategyParam() == null ? other.getExportStrategyParam() == null : this.getExportStrategyParam().equals(other.getExportStrategyParam()))
            && (this.getIsExecute() == null ? other.getIsExecute() == null : this.getIsExecute().equals(other.getIsExecute()))
            && (this.getExportTimes() == null ? other.getExportTimes() == null : this.getExportTimes().equals(other.getExportTimes()))
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
        result = prime * result + ((getSqlValue() == null) ? 0 : getSqlValue().hashCode());
        result = prime * result + ((getRequestParam() == null) ? 0 : getRequestParam().hashCode());
        result = prime * result + ((getExportModule() == null) ? 0 : getExportModule().hashCode());
        result = prime * result + ((getExportClass() == null) ? 0 : getExportClass().hashCode());
        result = prime * result + ((getExportStrategyClass() == null) ? 0 : getExportStrategyClass().hashCode());
        result = prime * result + ((getExportStrategyParam() == null) ? 0 : getExportStrategyParam().hashCode());
        result = prime * result + ((getIsExecute() == null) ? 0 : getIsExecute().hashCode());
        result = prime * result + ((getExportTimes() == null) ? 0 : getExportTimes().hashCode());
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
        sb.append(", sqlValue=").append(sqlValue);
        sb.append(", requestParam=").append(requestParam);
        sb.append(", exportModule=").append(exportModule);
        sb.append(", exportClass=").append(exportClass);
        sb.append(", exportStrategyClass=").append(exportStrategyClass);
        sb.append(", exportStrategyParam=").append(exportStrategyParam);
        sb.append(", isExecute=").append(isExecute);
        sb.append(", exportTimes=").append(exportTimes);
        sb.append(", delFlag=").append(delFlag);
        sb.append("]");
        return sb.toString();
    }
}