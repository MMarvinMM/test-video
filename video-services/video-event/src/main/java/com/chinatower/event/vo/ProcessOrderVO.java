package com.chinatower.event.vo;

import com.chinatower.event.dataobject.ProcessOrderDO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@ApiModel(value = "", description = "处理单")
public class ProcessOrderVO extends ProcessOrderDO implements Serializable {

    private List<String> resourceList;

    private List<String> processVideoList;
    private List<String> processImgList;
    private String operator;


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

    /**
     * 扩展字段
     */
    private String ext1;

}
