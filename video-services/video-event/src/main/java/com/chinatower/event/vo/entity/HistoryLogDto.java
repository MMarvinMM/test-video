package com.chinatower.event.vo.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName ReceiveSatelliteAlarmVo
 * @Deacription 卫星火情报警接收参数
 * @Author guyuefei
 * @Date 2022-05-12 09:49:20
 * @Version 1.0
 */
@Data
@ApiModel(value = "推送日志记录对象",description = "推送日志记录对象")
@Document(collection="history_logs_t")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryLogDto extends BaseMongoDTO {

    /** 推送类型 */
    private String type;
    /** 请求地址 */
    private String url;
    /** 原始报文 */
    private String originalJson;
    /** 请求报文 */
    private String requestJson;
    /** 返回报文 */
    private String responseJson;
    /** 创建时间 */
    private Date createTime;
    /** 耗时（毫秒） */
    private String expendTime;
    /** 0失败 1成功 */
    private String status;
    /** 报错信息 */
    private String error;
    /** 客户编码 */
    private String customerCode;
    /** 告警编码 */
    private String alarmCode;

}