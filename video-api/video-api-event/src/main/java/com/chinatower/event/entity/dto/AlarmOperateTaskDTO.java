package com.chinatower.event.entity.dto;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警研判任务入参
 */
@Data
public class AlarmOperateTaskDTO extends BaseDataScopeEntity {


    /**
     * 标签。若传的是labelId，则只传最底层labelId即可；若不传labelId，则传上所有层级的label信息。
     *
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
     * 允许传入 App 定义的各变量值。 inputs 参数包含了多组键值对（Key/Value pairs），
     * 每组的键对应一个特定变量，每组的值则是该变量的具体值。 如果变量是文件类型，
     * 请指定一个包含以下 files 中所述键的对象。 默认 {}
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
