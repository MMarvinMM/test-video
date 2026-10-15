package com.chinatower.video.api.entity;

import com.chinatower.common.core.exception.CommonErrorCode;
import com.chinatower.common.core.vo.ResultEntity;

/**
 * 自定义失败方法
 * Created by zhujun on 2022/1/20.
 */
public class VideoResultEntity extends ResultEntity {

    public static ResultEntity fail(String msg) {
        return new ResultEntity(CommonErrorCode.ERROR_CODE, msg);
    }
}
