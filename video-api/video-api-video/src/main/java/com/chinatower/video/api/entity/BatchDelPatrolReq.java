package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("BatchDelPatrolReqVideo")
public class BatchDelPatrolReq extends DeviceSouthCommonDTO {

    /**
     * 巡航计划编码
     * @required true
     */
    @ApiModelProperty(value = "巡航路径id列表", required = true)
    @NotEmpty(message ="巡航路径id列表不能为空")
    private List<Long> patrolIds;
    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;

}
