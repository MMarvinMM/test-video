package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaol
 * @date 2022/4/26 17:37
 * @description
 */
@Data
public class AuthObjDataNewVO implements Serializable {
    /**
     * 管理区域
     */
    private List<AreaDataVO> areaList;
}
