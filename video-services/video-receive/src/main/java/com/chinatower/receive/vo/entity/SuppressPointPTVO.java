package com.chinatower.receive.vo.entity;

import lombok.Data;

/**
 * @className: SuppressPointVo
 * @description: 压制点PT对象
 * @author: li.xufu
 * @date: 2022/5/14
 * @requestID 需求单号及需求名称
 */
@Data
public class SuppressPointPTVO {

    private String suppressPointId;

    private String pointP;

    private String pointT;

}
