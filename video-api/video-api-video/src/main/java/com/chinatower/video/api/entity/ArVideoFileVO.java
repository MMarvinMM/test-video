package com.chinatower.video.api.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * AR标签视频文件VO
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
@Data
public class ArVideoFileVO implements Serializable {

    private String uid;

    private String fileName;

    private String fileSize;

    private String fileSuffix;

    private String recordType;

    private String fileUrl;

    private String speed;

    private String fileId;

}
