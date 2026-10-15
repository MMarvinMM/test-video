package com.chinatower.common.core.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ProcessOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/15 17:36
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class ProcessOrderDTO extends ProcessOrderDO {
    private String operator;


    private List resourceList;

    private List processVideoList;
    private List processImgList;

    private String warningTypeName;
    private String provinceName;
    private String cityName;
    private String countyName;
    private String duration;

    private String processName;

    // 当前处置是否核实完成,1-完成； 2- 未完成
    private String isEnd;

    private String sendMan;

    private String approveStatus;
    private String approveTime;
    private String approvePerson;
    private String sendRemark;
    private String approveResult;

    // 0-主流程 1-多部门调度流程
    private String flowType;

    private String mobilePhone;

    private String voice;

    /**
     * 是否会签0:不会签  1:会签
     */
    private String isSign;

    /**
     * 扩展字段
     */
    private String ext1;
}
