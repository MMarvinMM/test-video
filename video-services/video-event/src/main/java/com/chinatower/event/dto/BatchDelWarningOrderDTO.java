package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BatchDelWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/29 16:18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BatchDelWarningOrderDTO extends BaseDataScopeEntity implements Serializable {

    //表名
    private String tableName;

    //需要删除的ID列表
    private List<String> warningOrderIdList;

    //租户ID
    private String tenantId;

    //行业ID
    private String tradeId;

    //行业版本
    private String appVerCode;

    private String userId;

    /**
     * 删除说明
     */
    private String deleteRemark;
}
