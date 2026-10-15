package com.chinatower.device.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dto.QueryDTO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.mapper.IotDeviceMapper;

import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.constants.Constants;
import com.github.pagehelper.page.PageMethod;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/updateAreaCode")
public class AreaCodeUpdateController {


    /**
     * sys_adcode_location
     * UPDATE sys_adcode_location set adcode = '232718' where adcode = '232761'
     * UPDATE sys_adcode_location set adm1_name_zh = '济源市' where adcode = '419001'
     * UPDATE sys_adcode_location set adcode = '411202560000' where adcode = '411271'
     * UPDATE sys_adcode_location set adcode = '340209401000' where adcode = '340272'
     * UPDATE sys_adcode_location set adm1_name_zh = '济源市' where adcode = '419001'
     */

    @Autowired
    private IotDeviceMapper iotDeviceMapper;

    @Resource
    GisMapApi gisMapApi;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String AD_CODE = "adcode";

    private static final String COUNTY_ID = "countyId";

    private static final String UPDATE_SUCCESS_MSG = "修改成功！";

    @PostMapping("/updateDevice")
    public Result updateIotAreaCode(@RequestBody IotDeviceVO iotDeviceVO){

        try {
            //先把表中所有设备查出来--根据传入的pageNum,pageSize分批查
            PageMethod.startPage(iotDeviceVO.getPageNum(),iotDeviceVO.getPageSize());
            List<IotDeviceVO> deviceList = new ArrayList<>();
            if ("1".equals(iotDeviceVO.getFlag())){
                deviceList = iotDeviceMapper.queryIotList(iotDeviceVO);
            }
            if ("2".equals(iotDeviceVO.getFlag())){
                deviceList = iotDeviceMapper.queryUavList(iotDeviceVO);
            }
            if ("3".equals(iotDeviceVO.getFlag())){
                deviceList = iotDeviceMapper.queryHornList(iotDeviceVO);
            }
            if ("4".equals(iotDeviceVO.getFlag())){
                deviceList = iotDeviceMapper.queryRecorderList(iotDeviceVO);
            }
            log.info("设备集合:{},设备数量:{}",JSON.toJSONString(deviceList),deviceList.size());
            List<IotDeviceVO> deviceVOList = Lists.newArrayList();
            for (IotDeviceVO iotDevice : deviceList){
                Region region = gisApi(iotDevice.getLongitude(), iotDevice.getLatitude());
                if (region != null){
                    if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                        iotDevice.setProvinceId(region.getProvinceCode());
                    }
                    if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                        iotDevice.setCityId(region.getCityCode());
                    }
                    if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                        iotDevice.setCountyId(region.getCountyCode());
                    }
                    if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                        iotDevice.setTownCode(region.getTownCode());
                    }
                    if (!org.springframework.util.StringUtils.isEmpty(region.getTown())) {
                        iotDevice.setTownName(region.getTown());
                    }
                    if (!org.springframework.util.StringUtils.isEmpty(region.getLocation())) {
                        iotDevice.setLocation(region.getLocation());
                    }
                    deviceVOList.add(iotDevice);
                }
            }
            //批量刷库
            if (!CollectionUtils.isEmpty(deviceVOList)){
                if ("1".equals(iotDeviceVO.getFlag())){
                    iotDeviceMapper.updateIotDeviceBatch(deviceVOList);
                }
                if ("2".equals(iotDeviceVO.getFlag())){
                    iotDeviceMapper.updateUavDeviceBatch(deviceVOList);
                }
                if ("3".equals(iotDeviceVO.getFlag())){
                    iotDeviceMapper.updateHornDeviceBatch(deviceVOList);
                }
                if ("4".equals(iotDeviceVO.getFlag())){
                    iotDeviceMapper.updateRecorderDeviceBatch(deviceVOList);
                }

            }
            return Result.success(UPDATE_SUCCESS_MSG);
        }catch (Exception e){
            log.error("刷数异常:",e);
            return Result.fail("刷数异常！");
        }
    }

    public Region gisApi(String longitude,String latitude){
        //根据经纬度转省市区编码
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        Result<List<PoiContraryGeocodingQueryRes>> resultData = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("setSensorStatusReportData gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(resultData));
        Region region = null;
        if (resultData != null && ResultCode.SUCCESS.getCode() == resultData.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(resultData.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = resultData.getData().get(0);
                if (data.getRegion() != null) {
                    region = data.getRegion();
                }
            } catch (Exception e) {
                log.error("setSensorStatusReportData gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        return region;
    }

    /**
     * 1-weather_air_day_info,2-weather_air_hour_info,3-weather_day_info
     * @param queryDTO
     * @return
     */
    @PostMapping("/updateWeather")
    public Result updateMongoDb(@RequestBody QueryDTO queryDTO){

        try{
            String collectionName = "";
            if ("1".equals(queryDTO.getFlag())){
                collectionName = "weather_air_day_info";
            }
            if ("2".equals(queryDTO.getFlag())){
                collectionName = "weather_air_hour_info";
            }
            if ("3".equals(queryDTO.getFlag())){
                collectionName = "weather_day_info";
            }
            if ("4".equals(queryDTO.getFlag())){
                collectionName = "weather_future_day_info";
                if (!StringUtils.isEmpty(queryDTO.getDay())){
                    collectionName = "weather_future_day_info_" + queryDTO.getDay();
                }
            }
            if ("5".equals(queryDTO.getFlag()) && !StringUtils.isEmpty(queryDTO.getDay())){
                collectionName = "vm_iot_device_t_" + queryDTO.getDay();
                Query query = new Query();
                query.with(Sort.by(Sort.Direction.DESC,"deviceCode"));
                long skip = (queryDTO.getPageNum() -1) * queryDTO.getPageSize();
                query.skip(skip).limit(queryDTO.getPageSize());
                List<IotDeviceVO> deviceVOList = mongoTemplate.find(query,IotDeviceVO.class, collectionName);
                List<IotDeviceVO> newDeviceList = Lists.newArrayList();
                for (IotDeviceVO iotDevice : deviceVOList){
                    Region region = gisApi(iotDevice.getLongitude(), iotDevice.getLatitude());
                    if (region != null){
                        if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                            iotDevice.setProvinceId(region.getProvinceCode());
                        }
                        if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                            iotDevice.setCityId(region.getCityCode());
                        }
                        if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                            iotDevice.setCountyId(region.getCountyCode());
                        }
                        if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                            iotDevice.setTownCode(region.getTownCode());
                        }
                        if (!org.springframework.util.StringUtils.isEmpty(region.getLocation())) {
                            iotDevice.setLocation(region.getLocation());
                        }
                    }
                    newDeviceList.add(iotDevice);
                }
                //批量修改mongodb
                BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, IotDeviceVO.class, collectionName);
                for (IotDeviceVO iotDeviceVO : newDeviceList){
                    Query updateQuery = new Query(Criteria.where(Constants.DEVICECODE).is(iotDeviceVO.getDeviceCode()));
                    Update updates = new Update();
                    //添加需要修改的字段
                    updates.set("provinceId",iotDeviceVO.getProvinceId());
                    updates.set("cityId",iotDeviceVO.getCityId());
                    updates.set(COUNTY_ID,iotDeviceVO.getCountyId());
                    updates.set("townCode",iotDeviceVO.getTownCode());
                    updates.set("address",iotDeviceVO.getLocation());
                    // 将更新操作加入批量操作
                    bulkOperations.updateOne(updateQuery, updates);
                }
                //执行批量更新操作
                BulkWriteResult execute = bulkOperations.execute();
                log.info("批量更新操作结果：{}",JSON.toJSONString(execute.getModifiedCount()));
                return Result.success(UPDATE_SUCCESS_MSG);
            }
            //设置修改内容
            Update update1 = new Update();
            update1.set(AD_CODE,"232718");
            update1.set(COUNTY_ID,"232718");

            Update update2 = new Update();
            update2.set(AD_CODE,"411202560000");
            update2.set(COUNTY_ID,"411202560000");

            Update update3 = new Update();
            update3.set(AD_CODE,"340209401000");
            update3.set(COUNTY_ID,"340209401000");

            Update update4 = new Update();
            update4.set("cityName","济源市");

            Update update5 = new Update();
            update5.set("countyName","米林市");

            Update update6 = new Update();
            update6.set("countyName","错那市");

            Criteria criteria1 = Criteria.where(AD_CODE).is("232761");
            Criteria criteria2 = Criteria.where(AD_CODE).is("411271");
            Criteria criteria3 = Criteria.where(AD_CODE).is("340272");
            Criteria criteria4 = Criteria.where("cityId").is("419000");
            Criteria criteria5 = Criteria.where(COUNTY_ID).is("540422");
            Criteria criteria6 = Criteria.where(COUNTY_ID).is("540530");

            Query updateQuery1 = new Query(criteria1);
            Query updateQuery2 = new Query(criteria2);
            Query updateQuery3 = new Query(criteria3);
            Query updateQuery4 = new Query(criteria4);
            Query updateQuery5 = new Query(criteria5);
            Query updateQuery6 = new Query(criteria6);

            UpdateResult updateResult1 = mongoTemplate.updateMulti(updateQuery1,update1,collectionName);
            UpdateResult updateResult2 = mongoTemplate.updateMulti(updateQuery2,update2,collectionName);
            UpdateResult updateResult3 = mongoTemplate.updateMulti(updateQuery3,update3,collectionName);
            UpdateResult updateResult4 = mongoTemplate.updateMulti(updateQuery4,update4,collectionName);
            UpdateResult updateResult5 = mongoTemplate.updateMulti(updateQuery5,update5,collectionName);
            UpdateResult updateResult6 = mongoTemplate.updateMulti(updateQuery6,update6,collectionName);

            log.info("updateResult1 修改数量:{}",updateResult1.getModifiedCount());
            log.info("updateResult2 修改数量:{}",updateResult2.getModifiedCount());
            log.info("updateResult3 修改数量:{}",updateResult3.getModifiedCount());
            log.info("updateResult4 修改数量:{}",updateResult4.getModifiedCount());
            log.info("updateResult5 修改数量:{}",updateResult5.getModifiedCount());
            log.info("updateResult6 修改数量:{}",updateResult6.getModifiedCount());

            return Result.success(UPDATE_SUCCESS_MSG);
        }catch (Exception e){
            log.error("修改失败:",e);
            return Result.fail("修改失败！");
        }
    }
}
