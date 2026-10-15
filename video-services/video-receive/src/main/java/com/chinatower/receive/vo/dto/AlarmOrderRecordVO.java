package com.chinatower.receive.vo.dto;


import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class AlarmOrderRecordVO extends OrderFlowRecordDO implements Serializable {

    private String judged;

    private String judgedType;

    // 领导批示信息
    //private List<Map> leaders;

    private String orderSubFlowId;

    private String subOrderId;

    private String mobilePhone;

    private String tableName;

    /**
     * 是否会签0:不会签  1:会签
     */
    private String isSign;

    /**
     * 会签时，会有多个processOrderId
     */
    private List<String> processOrderIds;
    /**
     * 会签时，会有多个processId
     */
    private List<String> processIds;


    /**
     * 调度指定待处理人账号合集
     */
    private String processNameList;

    /**
     * 真实环节id
     */
    private String realLinkType;

    /**
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;

    /**
     * 受理状态 0：不开启 1：开启（待受理）2：已受理 3：受理完成
     */
    private String enableAcceptanceMode;

    /**
     * 下个环节受理状态 0：不开启 1：开启（待受理）2：已受理 3：受理完成
     */
    private String nextEnableAcceptanceMode;

    private String acceptProcessId;

    /**
     * 关闭说明
     */
    private String closeRemark;
}
