package com.chinatower.event.vo.entity;

import lombok.Data;

/**
 * @className: SuppressPointVo
 * @description: 压制点对象
 * @author: li.xufu
 * @date: 2022/5/14
 * @requestID 需求单号及需求名称
 */
@Data
public class SuppressPointVO {

    String suppressPointId;
    Float percentX;
    Float percentY;

}
