package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.vo.BaseGridEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 网格配置vo
 * @author zl
 */
@Data
@ToString
public class GridVO extends BaseGridEntity {

    /**
     * 今日告警数
     * @required true
     */
    private String todayAlarmNum;

    /**
     * 网格员数量
     * @required true
     */
    private int keeperCount;


    /**
     * 网格员列表
     * @required true
     */
    private List<GridKeeperBaseVO> gridKeeperlist;


}
