package com.chinatower.gis.modules.gis.service;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.gis.modules.gis.mapper.GisMapper;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.dengxq.lnglat2Geo.GeoTrans;
import com.dengxq.lnglat2Geo.entity.Admin;
import com.dengxq.lnglat2Geo.entity.CoordinateSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.Enumeration;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName GisService
 * @Description GIS服务类
 * @Author D
 * @Date 2022/3/14 15:53
 */
@Service
@Slf4j
public class GisService {

    @Resource
    private GisMapper gisMapper;


    /**
     * 获得行政区经纬度
     * @param gisKeyWordQueryReq
     * @return
     */
    public Map<String,Object>  getLatitude(GisKeyWordQueryReq gisKeyWordQueryReq){
        return gisMapper.getLatitude(gisKeyWordQueryReq);
    }

}
