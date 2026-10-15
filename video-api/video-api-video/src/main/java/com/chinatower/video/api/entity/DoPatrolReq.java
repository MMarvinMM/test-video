package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoPatrolReq extends VideoBaseControlReq{
    /**
     * 巡航路径id
     * */
    private Long patrolId;
    /**
     * 巡航控制类型
     *  1-停用；2-启用；
     * */
    private int patrolType;
    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;
}
