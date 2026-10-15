package com.chinatower.airline.dto.response;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author lyy
 * @description
 * @date 2024/12/03 16:40
 */
@Data
public class AirVideoExtractTaskRep {
    //抽帧任务ID
    private String taskId;
    //抽帧任务名称
    private String taskName;
    //抽帧时间
    private String extractTime;
    //抽帧视频ID
    private String videoId;
    //抽帧图片结果集
    private List<ExtractPhotoResponse> list;

    // 抽帧图片实体类
    @Data
    public static class ExtractPhotoResponse {
        private String id; //抽帧图片id
        private String name; //抽帧图片名称
        private String photoAccessUrl; //图片访问地址
        private Integer eventStatus; //告警结果
        private String photoUrl; //图片存放地址
        private Integer seconds; //图片所属秒数
        private String spaceTime; //抽帧间隔时间（隔几秒一抽）
        private String thumbnailAccessUrl; //缩略图地址访问地址
        private String thumbnailBigAccessUrl; //缩略大图访问地址
        private String thumbnailBigUrl; //缩略图大图存放地址
        private String thumbnailUrl; //缩略图存放地址
    }
}
