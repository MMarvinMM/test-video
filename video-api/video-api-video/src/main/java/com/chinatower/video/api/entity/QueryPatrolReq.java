package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

/**
 * @ClassName QueryPatrolRel
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/28
 **/

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("QueryPatrolReqVideo")
public class QueryPatrolReq extends DeviceSouthCommonDTO{

    @ApiModelProperty(value = "巡航路径id", required = true)
    private Long patrolId;
}
