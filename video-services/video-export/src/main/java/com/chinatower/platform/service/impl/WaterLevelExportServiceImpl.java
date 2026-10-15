package com.chinatower.platform.service.impl;

import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.WaterLevelQueryExcel;
import com.chinatower.platform.entity.WaterLevelQueryLatestMongo;
import com.chinatower.platform.entity.WaterLevelQueryRecordMongo;
import com.chinatower.platform.entity.WaterLevelQueryVO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.WaterLevelExportMapper;
import com.chinatower.platform.mongodb.WaterLevelQueryLatestMongoMapper;
import com.chinatower.platform.mongodb.WaterLevelQueryRecordMongoMapper;
import com.chinatower.platform.service.WaterLevelExportService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: 水位监测导出实现类
 * @Author: wangyt
 * @CreateTime: 2024-12-16  14:41
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class WaterLevelExportServiceImpl implements WaterLevelExportService {

    @Autowired
    private WaterLevelExportMapper waterLevelExportMapper;

    @Autowired
    WaterLevelQueryLatestMongoMapper waterLevelQueryLatestMongoMapper;

    @Autowired
    WaterLevelQueryRecordMongoMapper waterLevelQueryRecordMongoMapper;

    private static final String WATER_LEVEL = "waterLevel";
    private static final String GATHER_TIME = "gatherTime";
    private static final String ASC = "asc";
    private static final String RECORD = "record";

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public Result exportLatestWaterLevel(WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        waterLevelQueryVO.setTenantId(user.getTenantId());
        waterLevelQueryVO.setIndustryCode(user.getIndustryCode());
        return exportWaterLevelData(waterLevelQueryVO, "latest", response);
    }

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public Result exportWaterLevelRecord(WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        waterLevelQueryVO.setTenantId(user.getTenantId());
        waterLevelQueryVO.setIndustryCode(user.getIndustryCode());
        return exportWaterLevelData(waterLevelQueryVO, RECORD, response);
    }

    private Result exportWaterLevelData(WaterLevelQueryVO waterLevelQueryVO, String flag, HttpServletResponse response){
        List<WaterLevelQueryVO> waterLevelInfoList = waterLevelExportMapper.queryWaterLevelDeviceInfoList(waterLevelQueryVO);
        List<String> deviceCodeList = waterLevelInfoList.stream().map(WaterLevelQueryVO::getDeviceCode).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(deviceCodeList)){
            return Result.fail("未查询到相关数据，请重新筛选后导出！");
        }
        // 再查询水位数据
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        QueryEntity query;
        if(RECORD.equals(flag)){
            query = new QueryEntity(WaterLevelQueryRecordMongo.class);
        }else{
            query = new QueryEntity(WaterLevelQueryLatestMongo.class);
        }
        String[] deviceCodeArray = (String[])deviceCodeList.toArray(new String[deviceCodeList.size()]);
        query.in(Constants.DEVICECODE, deviceCodeArray);
        try {
            if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeStart()) && StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeEnd())){
                query.between(GATHER_TIME, sdf.parse(waterLevelQueryVO.getGatherTimeStart()), sdf.parse(waterLevelQueryVO.getGatherTimeEnd()));
            }else{
                if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeStart())){
                    query.gte(GATHER_TIME, sdf.parse(waterLevelQueryVO.getGatherTimeStart()));
                }
                if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeEnd())){
                    query.lte(GATHER_TIME, sdf.parse(waterLevelQueryVO.getGatherTimeEnd()));
                }
            }
        }catch (Exception e){
            log.error("时间格式转换异常",e);
        }

        SortEntity sort = new SortEntity();
        if(StringUtils.isNotEmpty(waterLevelQueryVO.getWaterLevelSort()) ){
            if(waterLevelQueryVO.getWaterLevelSort().equalsIgnoreCase(ASC)){
                sort.asc(WATER_LEVEL);
            }else{
                sort.desc(WATER_LEVEL);
            }
        }
        if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeSort()) && waterLevelQueryVO.getGatherTimeSort().equalsIgnoreCase(ASC)){
            sort.asc(GATHER_TIME);
        }else{
            sort.desc(GATHER_TIME);
        }

        List<WaterLevelQueryExcel> waterLevelExportList = new ArrayList<>();
        if(RECORD.equals(flag)){
            List<WaterLevelQueryRecordMongo> mongoList = waterLevelQueryRecordMongoMapper.findList(query, sort, MongoDBType.PRIMARY);
            if(mongoList.size() > 5000){
                return Result.fail("导出数据量最多5000条，请按照查询条件导出。");
            }

            mongoList.forEach(waterLevel -> {
                WaterLevelQueryExcel waterLevelExport = new WaterLevelQueryExcel();
                BeanUtils.copyProperties(waterLevel,waterLevelExport);
                waterLevelExport.setGatherTime(sdf.format(waterLevel.getGatherTime()));
                for (WaterLevelQueryVO queryVO:waterLevelInfoList) {
                    if(waterLevel.getDeviceCode().equals(queryVO.getDeviceCode())){
                        waterLevelExport.setDeviceName(queryVO.getDeviceName());
                        waterLevelExport.setDeviceStatusName(queryVO.getDeviceStatus());
                        waterLevelExport.setDeviceAddress(queryVO.getDeviceAddress());
                    }
                }
                waterLevelExportList.add(waterLevelExport);
            });
        }else{
            List<WaterLevelQueryLatestMongo> mongoList = waterLevelQueryLatestMongoMapper.findList(query, sort, MongoDBType.PRIMARY);
            if(mongoList.size() > 5000){
                return Result.fail("导出数据量最多5000条，请按照查询条件导出。");
            }

            mongoList.forEach(waterLevel -> {
                WaterLevelQueryExcel waterLevelExport = new WaterLevelQueryExcel();
                BeanUtils.copyProperties(waterLevel,waterLevelExport);
                waterLevelExport.setGatherTime(sdf.format(waterLevel.getGatherTime()));
                for (WaterLevelQueryVO queryVO:waterLevelInfoList) {
                    if(waterLevel.getDeviceCode().equals(queryVO.getDeviceCode())){
                        waterLevelExport.setDeviceName(queryVO.getDeviceName());
                        waterLevelExport.setDeviceStatusName(queryVO.getDeviceStatus());
                        waterLevelExport.setDeviceAddress(queryVO.getDeviceAddress());
                    }
                }
                waterLevelExportList.add(waterLevelExport);
            });
        }
        byte[] data = new ExcelUtil<>(WaterLevelQueryExcel.class).exportBytes(waterLevelExportList);
        FileUtils.download(response, data);
        if(RECORD.equals(flag)){
            return Result.success("导出水位历史数据成功!");
        }
        return Result.success("导出最新水位数据成功!");
    }
}
