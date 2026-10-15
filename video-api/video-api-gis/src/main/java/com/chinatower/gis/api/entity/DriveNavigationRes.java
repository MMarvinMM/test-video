package com.chinatower.gis.api.entity;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.JsonUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desription: 获得导航返回
 * @data 创建时间：2023/8/8 16:38
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DriveNavigationRes {

    /**
     * 起点经纬度
     * @required false
     */
    @ApiModelProperty(value = "起点经纬度")
    private String origin;

    /**
     * 终点经纬度
     * @required false
     */
    @ApiModelProperty(value = "终点经纬度")
    private String destination;

    /**
     * 预计出租车费用，单位：元
     * @required false
     */
    @ApiModelProperty(value = "预计出租车费用，单位：元 ")
    private String taxiCost;

    /**
     * 算路方案详情
     * @required true
     */
    @ApiModelProperty(value = "算路方案详情")
    private List<DriveNavigationPaths> paths;

}
