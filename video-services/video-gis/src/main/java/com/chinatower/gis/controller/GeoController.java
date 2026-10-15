package com.chinatower.gis.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.dataobject.Latitude;
import com.chinatower.gis.dto.ScreenDTO;
import com.chinatower.gis.mapper.GeoMapper;
import com.chinatower.gis.utils.DateUtil;
import com.chinatower.platform.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName GeoController
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/18
 **/

@Slf4j
@RestController
public class GeoController {

    @Resource
    GeoMapper geoMapper;

    @PostMapping("/geo/getNumForScreen")
    public Result getNumForScreen(ScreenDTO screenDTO){

        String hms = " 23:59:59";
        log.info("************Method:getNumForScreen入参"+ JSON.toJSONString(screenDTO));
        //1:省份 2:地市
        String level = screenDTO.getLevel();
        screenDTO.setAreaCode(screenDTO.getAreaList());

        //add by hued 20211129 R-ZBZB-202111180812188 增加支持查询今日、3日内、7日内、更多时间段告警事件 start
        Date endDate;
        Date startDate;
        String webStartDate = screenDTO.getWebStartDate();
        String webEndDate = screenDTO.getWebEndDate();
        if(!StringUtils.isEmpty(webStartDate) && !StringUtils.isEmpty(webEndDate)){
            startDate = DateUtil.stringToDateYMDHMS(webStartDate);
            endDate = DateUtil.string2Date(webEndDate + hms);
        }else{
            endDate = new Date();
            startDate = new Date();
        }
        screenDTO.setWebStartDate(DateUtil.dateToStringYMD(startDate));
        screenDTO.setWebEndDate(DateUtil.dateToStringNoMS(endDate));
        //add by hued 20211129 R-ZBZB-202111180812188 增加支持查询今日、3日内、7日内、更多时间段告警事件 end

        List<Latitude> area = new ArrayList<>();
        if (Constants.ONE.equals(level)){
            area=(geoMapper.getNumByProvinceTT(screenDTO));
        }else if (Constants.TWO.equals(level)){
            if(screenDTO.getProvinceList()!=null&&screenDTO.getProvinceList().size()>0){
                screenDTO.setAreaCode(geoMapper.getSubLevelCode(screenDTO.getProvinceList()));
            }
            area = (geoMapper.getNumByCityTT(screenDTO));
        }

        log.info("************Method:getNumForScreen返回:"+area);
        return Result.data(area);
    }

}
