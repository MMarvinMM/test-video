package com.chinatower.industry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndustryAlgorithmDTO implements Serializable {

    private String algorithmCode;

    private String algorithmName;

    private String alarmType;

    private String typeName;

    private String deviceCode;

    /**
     * 算法任务状态：0-启用  1-停用
     */
    private String status;

    /**
     * 算法类型 0-后端 1-前端
     */
    private String algorithmType;

    private String extendJson;
}
