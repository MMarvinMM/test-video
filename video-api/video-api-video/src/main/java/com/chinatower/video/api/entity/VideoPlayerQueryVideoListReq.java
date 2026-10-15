package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: VideoPlayerQueryVideoListReq
 * @Description: 视频查询通道录像列表实体
 * @Author: ldl
 * @Date: 2022/1/18
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoPlayerQueryVideoListReq extends VideoBaseReq{
    /**
     * 当前页数
     *  查询起始行
     *  @required false
     * */
    private int currentPage;

    /**
     * 页大小
     *  @required false
     * */
    private int pageSize;

    /**
     * 录像类型
     *  录像类型：0-普通录像，1-报警录像，2-全部查询
     *  @required  true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_RECORD_TYPE)
    private int recordType;
    /**
     * 开始时间
     *  开始时间(格式：yyyy-MM-dd HH:mm:ss）
     *  @required  true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_START_TIME)
    private String startTime;
    /**
     * 结束时间
     *  结束时间(格式：yyyy-MM-dd HH:mm:ss）
     *  @required  true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_END_TIME)
    private String endTime;


    private List<Map<String, String>> times;

}