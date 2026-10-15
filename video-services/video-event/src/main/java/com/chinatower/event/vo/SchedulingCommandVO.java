package com.chinatower.event.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: SchedulingCommandVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/27 15:29
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SchedulingCommandVO implements Serializable {
    private String orderFlowId;

    private String processId;

    private String processName;

    private String flowNodeTime;

    private String flowEndTime;

    private String remark;

    private String resourceType;

    private String resourceUrl;

    private String fileName;

    private String pendingHandleId;

    private String pendingHandleName;

    private String orderFileProcessId;

    private String orderFileProcessName;
    private String orderFileRemark;
    private String status;
    private List<SchedulingCommandVO> orderFileList;
}
