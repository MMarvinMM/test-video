package com.chinatower.receive.vo.dto;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.*;

/**
 * 操作任务列表查询请求参数
 */
@Data
public class OperationTaskListRequest {

    /**
     * 标签列表
     */
    private List<LabelInfo> labels;

    /**
     * 用户列表
     */
    private List<String> userList;

    /**
     * 用户
     */
    private String user;

    /**
     * 输入参数
     */
    private Map<String, Object> inputs = new HashMap<>();

    /**
     * 标签信息
     */
    @Data
    public static class LabelInfo {
        /**
         * 标签值
         */
        private String labelValue;
    }

    public void setSingleLabel(String labelValue) {
        LabelInfo labelInfo = new LabelInfo();
        labelInfo.setLabelValue(labelValue);
        if (CollUtil.isEmpty(this.labels)) {
            this.labels = new ArrayList<>();
        }
        this.labels.add(labelInfo);
    }
}
