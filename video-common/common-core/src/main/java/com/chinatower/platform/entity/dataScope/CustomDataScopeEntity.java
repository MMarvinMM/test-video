package com.chinatower.platform.entity.dataScope;

import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomDataScopeEntity implements Serializable {

    /**
     * 支持自定义修改属性
     * deptColumn
     * userColumn
     * tenantColumn
     * deviceColumn
     * alarmSourceColumn
     * alarmSourceValColumn
     * industryCodeColumn
     * appVerCode
     * processIdColumn
     * acceptColumn
     * gridColumn
     * gridKeeperColumn
     * resourceColumn
     * typeValueColumn
     * layerColumn
     */
    private Set<String> supportAttrs;


    // -----------------修改后的自定义权限数据----------------------
    /**
     * 部门
     * 对应 deptColumn
     */
    private Set<String> deptIdSet;

    /**
     * 租户
     * 对应 tenantColumn
     */
    private String tenantId;

    /**
     * 对应 userColumn
     */
    private String userId;

    /**
     * 对应 deviceColumn
     */
    private List<String> deviceCodes;


    /**
     * 告警来源 表加字段名
     * 暂为卫星告警租户普通用户查询告警列表权限过滤使用
     * 逗号分隔
     * 对应 alarmSourceColumn
     */
    private String alarmSource;

    /**
     * 告警来源值
     * 逗号分隔
     */
    private List<String> alarmSourceList;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 指定用户人
     */
    private String processId;

    /**
     * 受理节点
     */
    private String accept;

    /**
     * 网格
     * 对应 grid
     */
    private List<String> gridCodes;

    /**
     * 网格员
     * 对应 gridKeeper
     * 逗号分隔
     */
    private List<String> gridKeeperCodes;

    /**
     * 资源
     * 对应 resource
     * 逗号分隔
     */
    private List<String> resourceCodes;

    /**
     * 告警类型
     * 对应 typeValue
     */
    private List<ScreenDataConfigParticipationVO> alarmTypeConfigList;

    /**
     * 告警类型
     * 对应 typeValue
     */
    private String typeValue;

    /**
     * 图层
     * 对应 layer
     *
     */
    private List<String> layerIds;

    /**
     * 铁塔视角用
     * areaPro
     */
    private Set<String> areaPro;

    /**
     * 铁塔视角用
     * areaCity
     */
    private Set<String> areaCity;

    /**
     * 铁塔视角用
     * areaCode
     */
    private Set<String> areaCode;

    private List<String> tenantIds;

}
