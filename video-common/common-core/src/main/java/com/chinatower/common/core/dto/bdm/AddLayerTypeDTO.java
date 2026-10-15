package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription:
 * @data 创建时间：2023/4/26 17:30
 */
@Data
public class AddLayerTypeDTO {
    /**
     * 新增和修改的类型
     */
    private List<LayerTypeDTO> vos;
    /**
     * 需要删除的类型
     */
    private List<String> ids;
}
