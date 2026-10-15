package com.chinatower.event.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能研判任务请求对象
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntelligentJudgeTaskRequest {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 算法编码
     */
    private String algorithmCode;
    
    /**
     * 文件信息
     */
    private FileInfo fileInfo;
    
    /**
     * 前端告警透传字段
     */
    private ExtraInfo extraInfo;
    
    /**
     * 透传字段
     */
    private ExtraFieldInfo extraFieldInfo;
    
    /**
     * 文件信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        /**
         * 文件类型（image/video）
         */
        private String type;
        
        /**
         * 图片URL
         */
        private String imgUrls;
        
        /**
         * 视频URL
         */
        private String videoUrls;
    }
    
    /**
     * 前端告警透传字段
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraInfo {
        /**
         * 告警类型
         */
        private String alarmType;
        
        /**
         * 告警内容
         */
        private String alarmBody;
        
        /**
         * 告警时间
         */
        private String alarmTime;
        
        /**
         * 告警区域
         */
        private Object alarmArea;
    }
    
    /**
     * 透传字段
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtraFieldInfo {
        /**
         * 租户ID
         */
        private String tenantId;
        
        /**
         * 行业编码
         */
        private String industryCode;
        
        /**
         * 设备编码
         */
        private String deviceCode;
        
        /**
         * 通道编码
         */
        private String channelCode;
    }
}
