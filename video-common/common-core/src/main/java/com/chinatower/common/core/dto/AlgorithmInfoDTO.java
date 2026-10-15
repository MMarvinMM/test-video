package com.chinatower.common.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.chinatower.common.core.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 算法信息推送信息.
 *
 * @author: fangjun
 */
@Getter
@Setter
@SuppressWarnings("all")
public class AlgorithmInfoDTO implements Serializable {

    /**
     * 算法编码 唯一编码 Y
     */
    @NotBlank(message = "算法编码不能为空")
    private String algorithmCode;

    /**
     * 算法名称 Y
     */
    @NotBlank(message = "算法名称不能为空")
    private String algorithmName;

    /**
     * 算法大类 1-人体识别；2-车辆识别；3-行为分析；4-物体识别；5-目标分割； Y
     */
    @NotNull(message = "算法大类不能为空")
    private Integer algorithmType;

    /**
     * 算法厂家名称 Y
     */
    @NotBlank(message = "productName不能为空")
    private String productName;

    /**
     * 算法厂家编码 唯一编码 Y
     */
    @NotBlank(message = "算法厂家编码不能为空")
    private String productCode;

    /**
     * 芯片平台 1-英伟达T4/P4;2-海思359;3-比特大陆SC5+; N
     */
    private Integer chipPlatform;

    /**
     * 算法描述 Y
     */
    @NotNull(message = "算法描述不能为空")
    private String algorithmDescription;

    /**
     * 应用场景 Y
     */
    @NotNull(message = "应用场景不能为空")
    private String appScenarios;

    /**
     * 演示文档url 暂时放在算法描述中 N
     */
    private String documentUrl;

    /**
     * Logo图标 N
     */
    private String logoUrl;

    /**
     * 算法状态 1-可用；2-停用； Y
     * 备注：及时状态（算法能力服务当前可不可调用状态，这个状态变动频率稍高，反应的是当前服务运行状态）
     */
    private Integer algorithmStatus;

    /**
     * 版本号 Y
     */
    @NotBlank(message = "版本号不能为空")
    private String version;

    /**
     * 发布时间 标准时间格式：yyyy-MM-dd HH:mm:ss Y
     */
    @NotNull(message = "发布时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date publishTime;

    /**
     * 算法上下架 Y
     * 备注：能力状态（算法仓中已集成哪些算法，这个状态变动频率很低，比如算法厂家授权到期，算法被删除后回变为下架已删除）
     * 1-代表算法已删除；2-代表算法正常
     */
    @Max(value = 2, message = "algorithmShape值错误")
    @Min(value = 1, message = "algorithmShape值错误")
    @NotNull(message = "algorithmShape值错误")
    private Integer algorithmShape;

    @Override
    public String toString() {
        try {
            return JsonUtils.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析算法编码返回functionCode.
     *
     * @return String
     */
    public String generateFunctionCode() {
        try {
            return this.algorithmCode.split("-")[1].substring(0, 7);
        } catch (Exception e) {
            // do nothing
        }
        return "";
    }
}
