package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 图层类别管理
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="LayerType对象", description="图层类别管理")
public class LayerTypeDTO {

    /**
     * 数据ID 传了ID就是修改 没传就是新增
     */
    @ApiModelProperty(value = "数据ID")
    private String id;

    /**
     * 图层列表名称
     */
    @ApiModelProperty(value = "图层列表名称")
    @NotBlank(message = "类型名称不能为空")
    @Length(max = 20,message = "图层类型名称限制最多支持输入20个文字")
    private String typeName;

    @ApiModelProperty(value = "父类类型名称")
    private String superTypeName;

    @ApiModelProperty(value = "祖类类型名称")
    private String initialTypeName;

    @ApiModelProperty(value = "类型 0-默认 1-自定义")
    private Integer category;

    @ApiModelProperty(value = "简称")
    private String abbreviation;

    @ApiModelProperty(value = "类型字段")
    private List<AddOrModifyLayerTypeFieldsVO> fields = new ArrayList<>();

    /**
     * 需要删除的字段ID数据
     */
    private List<String> ids = new ArrayList<>();
}
