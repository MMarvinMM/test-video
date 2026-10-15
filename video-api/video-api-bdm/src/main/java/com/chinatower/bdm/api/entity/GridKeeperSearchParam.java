package com.chinatower.bdm.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author qq on 2023/5/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GridKeeperSearchParam extends CommonFeignEntity implements Serializable {
    /**
     * 网格员姓名
     */
    private String keeperName;
    /**
     * 身份证号
     */
    private String keeperIdCardNumber;
    /**
     * 网格id
     */
    private String gridId;
    /**
     * 1 在线 0 离线
     */
    private String isLine;
    /**
     * 网格员类型：1-网格长；2-网格员
     */
    private String keeperType;
    /**
     * 页码
     * @required true
     */
    private Integer pageNum;
    /**
     * 每页数量
     * @required true
     */
    private Integer pageSize;

}
