package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.dataobject.BaseWarningOrderDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderCollectionDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/1 10:40
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderCollectionDTO extends BaseWarningOrderDO {

    /**
     * 操作类型1：新增收藏 0：取消收藏
     * @required false
     */
    @ApiModelProperty(value = "操作类型", required = true)
    @NotNull(message = "操作类型不能为空")
    private String operationType;

    private Integer collectionId;

    /**
     * 形成告警时间（新增收藏时必传）
     * @required false
     */
    private String alarmTime;

    /**
     * 收藏人
     */
    private String collectionCreator;

    /**
     * 收藏创建时间
     */
    private Date collectionCreateTime;
}
