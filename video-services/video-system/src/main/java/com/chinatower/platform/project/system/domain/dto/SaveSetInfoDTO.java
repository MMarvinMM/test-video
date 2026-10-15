package com.chinatower.platform.project.system.domain.dto;

import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @desription: 保存个性化配置
 * @data 创建时间：2023/3/9 10:52
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SaveSetInfoDTO {
    /**
     * 个性化配置所属
     */
    @ApiModelProperty(value = "个性化配置所属 1-本地 3本组织 4-本客户")
    @NotNull(message = "配置归属不能为空")
    private String type;

    /**
     * 保存/修改的数据
     */
    @ApiModelProperty(value = "个性化配置数据")
    private List<SysInfoSetVO> voList;
}
