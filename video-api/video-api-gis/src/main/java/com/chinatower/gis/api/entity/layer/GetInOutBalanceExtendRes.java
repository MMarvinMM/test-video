package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetInOutBalanceExtendRes extends GetInOutBalanceRes{
    /**
     * 记录更新时间
     * */
    private Date updateTime;
}
