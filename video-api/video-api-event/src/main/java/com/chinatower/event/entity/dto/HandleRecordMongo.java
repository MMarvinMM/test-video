package com.chinatower.event.entity.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

@Data
@Component
public class HandleRecordMongo extends BaseMongoDTO {

    private String orderId;

    private String orderFlowId;

    private String linkId;

    private String handleLinkName;

    private String handleTime;

    private String handleUserId;

    private String handleRemark;

    private String deleteFlag;

    private String tenantId;

    private String industryCode;

}
