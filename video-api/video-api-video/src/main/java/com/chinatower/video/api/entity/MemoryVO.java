package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * 功能描述: 用户记忆点vo
 *
 * @param
 * @return
 * @author zhaozj
 * @date 2022/03/22 11:28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemoryVO {
    //用户编码
    private String userId;
    //记忆点类型："1"：火线预警；"2"：摄像机；"3"：网格；"4"：网格员；"5"：资源
    private String memoryType;
    //记忆点值："1"：折叠;"2"：展开
    private String memoryValue;
    //记忆点类型集合
    @NotEmpty(message = "记忆点类型集合不能为空")
    private List<String> memoryTypeList;
}
