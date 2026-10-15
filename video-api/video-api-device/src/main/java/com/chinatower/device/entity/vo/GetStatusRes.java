package com.chinatower.device.entity.vo;

import com.chinatower.video.api.entity.CommonResultEntiry;
import lombok.Data;

@Data
public class GetStatusRes {
    /**
     * 任务状态：1进行中；2成功；3失败
     */
    private Integer status;

    /**
     * 视频定向信息，只有状态为成功才返回
     */
    private VideoDirectionalInfo videoDirectionalInfo;

    private CoordinateSystem coordinateSystem;

    @Data
    public static class VideoDirectionalInfo {
        /**
         * 视频定向信息ID
         */
        private String Id;

        /**
         * 时间戳
         */
        private String TimeSpan;

        /**
         * POS信息X坐标
         */
        private Integer X;

        /**
         * POS信息Y坐标
         */
        private Integer Y;

        /**
         * POS信息Z坐标
         */
        private Integer Z;

        /**
         * 焦距
         */
        private Integer FocalLength;

        /**
         * 创建时间
         */
        private String CreateTime;

        /**
         * 宽
         */
        private Integer Width;

        /**
         * 高
         */
        private Integer Height;

        /**
         * 资源id
         */
        private String ResourceId;

        /**
         * 是否删除
         */
        private Boolean IsDeleted;

        /**
         * 俯仰角
         */
        private Integer Omega;

        /**
         * 航向角
         */
        private Integer Phi;

        /**
         * 翻滚角
         */
        private Integer Kappa;

        /**
         * 视频文件id
         */
        private String VideoFileId;

        /**
         * 模型矩阵
         */
        private String ViewMatrix;

        /**
         * 投影矩阵
         */
        private String ProjectMatrix;

        /**
         * 渲染纹理文件
         */
        private String TextureBaseFileId;

        /**
         * 是否关键帧
         */
        private Boolean KeyFrame;
    }

    @Data
    public static class GetStatusResEntity extends CommonResultEntiry {
        private GetStatusRes data;
    }
}
