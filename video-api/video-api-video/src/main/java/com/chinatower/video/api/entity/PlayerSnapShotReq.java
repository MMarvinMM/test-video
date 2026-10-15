package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerSnapShotReq extends VideoBaseReq{
    /**
     * 码流类型
     *  1-主码流；2-子码流
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_STREAM_TYPE)
    private int streamType;
    /**
     * 图片回传服务地址
     *  抓拍图⽚推送上传服务
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_IMAGE_UP_URL)
    private String imageUpUrl;
    /**
     * 图片名称
     *  有预置位：设备编码_通道编码_预置位id_2位序号；
     *  无预置位：设备编码_通道编码_2位序号；
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_IMAGE_NAME)
    private String imageName;

    private String tenantId;

    @Data
    public static class PlayerSnapShotReqA extends CommonResultEntiry {
        private PlayerSnapShotReq data;
    }

    //是否返回ofs地址 1：是
    private String isNeedOfs;

    //受理节点
    private String acceptNode;

    /**
     * 1-双通道抓图；0-单通道抓图;默认单通道抓图
     */
    private Integer doubleChannelSnapshotType;

}