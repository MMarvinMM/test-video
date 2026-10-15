package com.chinatower.event.api.model;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 请求入参模型
 * @author chmod
 * @version 1.0
 * @date 2022/3/24 16:15
 */
@Data
public class ApiAlarmOrderRecordModel extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = -6971430179421258416L;

    private String tableName;
    /**
     * 工单ID
     * @required true
     */
    @NotEmpty(message = "工单ID不能为空!")
    private String orderId;

    /**
     * 环节ID
     * @required true
     */
    private String linkId;

}
