package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 广播子平台
 * 4.26.删除广播任务请求参数实体类
 * @author huang.xiangyu
 */
@Data
public class HornDeleteTaskReqVO implements Serializable {


    private List<String> taskIdList;

    private String acceptNode;
}
