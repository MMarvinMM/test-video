package com.chinatower.system.api.resp;

import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.model.resp.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 字典数据表 sys_dict_data响应实体
 *
 * @author iwhalecloud
 */
@Data
@ApiModel(value = "SysDictDataResponse", description = "字典值返回参数")
public class SysDictDataResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @ApiModelProperty(value = "字典编码")
    private Long dictCode;

    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序")
    private Long dictSort;

    /**
     * 字典标签
     */
    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @ApiModelProperty(value = "字典键值")
    private String dictValue;

    /**
     * 字典类型
     */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @ApiModelProperty(value = "样式属性")
    private String cssClass;

    /**
     * 表格字典样式
     */
    @ApiModelProperty(value = "表格字典样式")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @ApiModelProperty(value = "是否默认（Y是 N否）")
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("dictCode", getDictCode())
                .append("dictSort", getDictSort())
                .append("dictLabel", getDictLabel())
                .append("dictValue", getDictValue())
                .append("dictType", getDictType())
                .append("cssClass", getCssClass())
                .append("listClass", getListClass())
                .append("isDefault", getIsDefault())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
