package com.chinatower.industry.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevicesByConditionVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 标签编码list
     */
    private List<String> labelIdList;

    /**
     * 算法编码list
     */
    private List<String> algorithmCodeList;
    /**
     * 设备类型list
     */
    private List<String> deviceTypeList;

    /**
     * 组织机构编码
     */
    private List<String> deptList;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 页长
     */
    private Integer pageSize;

    private List<String> deviceCodeList;

}
