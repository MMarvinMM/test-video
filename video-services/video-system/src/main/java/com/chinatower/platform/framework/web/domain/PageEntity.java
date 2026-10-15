package com.chinatower.platform.framework.web.domain;

import lombok.Data;

/**
 * @Description:
 * @Author: zs
 * @Date: 2021/04/02 14:09
 **/
@Data
public class PageEntity extends SystemBaseEntity {
    private Integer pageNum=1;
    private Integer pageSize=10;
    private Long total=0l;
}
