package com.chinatower.event.entity.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderInfoImgVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 11:32
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderInfoImgVO implements Serializable {

    /**
     * 文件地址
     */
    private String fileUrl;
}
