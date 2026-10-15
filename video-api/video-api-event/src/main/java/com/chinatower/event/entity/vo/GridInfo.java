package com.chinatower.event.entity.vo;

import com.chinatower.common.core.vo.BaseGridEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 网格配置vo
 * @author zl
 */
@Data
@ToString
public class GridInfo extends BaseGridEntity {

    /**
     * 网格长姓名
     * @required true
     */
    private String gridKeeperName;

    /**
     * 事发地点距离 单位: 米
     * @required true
     */
    private String incidentDistance;

}
