package com.chinatower.video.api.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName: ChannelVideoPlanRequestVO
 * @Description: 通道录像计划实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
@ToString
@Alias("ChannelVideoPlanRequestVOVideo")
public class ChannelVideoPlanRequestVO {

    /**
     * 录像计划列表
     */
    @Valid
    private List<ChannelVideoPlanVO> recordPlanList;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    private String creator;
    private String appVerCode;
    private String username;

}
