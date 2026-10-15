package com.chinatower.device.service.impl;

import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.device.dto.*;
import com.chinatower.device.mapper.WaterLevelInfoMapper;
import com.chinatower.device.mapper.WaterLevelLatestMongoMapper;
import com.chinatower.device.mapper.WaterLevelRecordMongoMapper;
import com.chinatower.device.service.WaterLevelMongoBase;
import com.chinatower.device.service.WaterLevelQueryService;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.deal.ObsFileUrlEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: 水位监测查询实现类
 * @Author: wangyt
 * @CreateTime: 2024-12-14  16:46
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Service
public class WaterLevelQueryServiceImpl implements WaterLevelQueryService {

    @Autowired
    private WaterLevelInfoMapper waterLevelInfoMapper;

    @Resource
    private WaterLevelLatestMongoMapper levelLatestMongoMapper;

    @Resource
    private WaterLevelRecordMongoMapper recordMongoMapper;

    @Autowired
    private ObsFileUrlEncryptUtil obsFileUrlEncryptUtil;

    private static final String GATHERTIME = "gatherTime";
    private static final String WATERLEVEL = "waterLevel";
    private static final String ASC = "asc";

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public Page<WaterLevelLatestMongo> queryLatestWaterLevelPageData(WaterLevelQueryVO waterLevelQueryVO) {
        return queryWaterLevelPageData(waterLevelQueryVO, WaterLevelLatestMongo.class);
    }

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public Page<WaterLevelRecordMongo> queryWaterLevelRecordPageData(WaterLevelQueryVO waterLevelQueryVO) {
        return queryWaterLevelPageData(waterLevelQueryVO, WaterLevelRecordMongo.class);
    }

    private <T> Page<T> queryWaterLevelPageData(WaterLevelQueryVO waterLevelQueryVO, Class<T> entityClass) {
        WaterLevelInfo waterLevelInfo = new WaterLevelInfo();
        BeanUtils.copyProperties(waterLevelQueryVO, waterLevelInfo);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        waterLevelInfo.setTenantId(user.getTenantId());
        waterLevelInfo.setIndustryCode(user.getIndustryCode());
        List<WaterLevelInfo> waterLevelInfoList = waterLevelInfoMapper.queryWaterLevelDeviceInfoList(waterLevelInfo);
        Page<T> pageInfo = new Page<>();
        if (!CollectionUtils.isEmpty(waterLevelInfoList)) {
            List<String> deviceCodeList = waterLevelInfoList.stream().map(WaterLevelInfo::getDeviceCode).collect(Collectors.toList());
            String[] deviceCodeArray = deviceCodeList.toArray(new String[0]);
            Integer pageNum = waterLevelQueryVO.getPageNum() != null ? waterLevelQueryVO.getPageNum() : 1;
            Integer pageSize = waterLevelQueryVO.getPageSize() != null ? waterLevelQueryVO.getPageSize() : 10;
            waterLevelQueryVO.setPageNum(pageNum - 1);
            waterLevelQueryVO.setPageSize(pageSize);
            Page page = new Page(waterLevelQueryVO.getPageNum(), waterLevelQueryVO.getPageSize());
            QueryEntity query = new QueryEntity(entityClass.equals(WaterLevelLatestMongo.class)?WaterLevelLatestMongo.class:WaterLevelRecordMongo.class);
            query.in(Constants.DEVICECODE, deviceCodeArray);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = null;
            Date endDate = null;
            try {
                if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeStart()) && StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeEnd())){
                    startDate = formatter.parse(waterLevelQueryVO.getGatherTimeStart());
                    endDate =  formatter.parse(waterLevelQueryVO.getGatherTimeEnd());
                    query.between(GATHERTIME, startDate, endDate);
                }
            } catch (Exception e) {
                log.info("日期格式报错"+e);
            }
            // 排序
            SortEntity sortEntity = new SortEntity();
            if(StringUtils.isNotEmpty(waterLevelQueryVO.getWaterLevelSort()) ){
                if(waterLevelQueryVO.getWaterLevelSort().equalsIgnoreCase(ASC)){
                    sortEntity.asc(WATERLEVEL);
                }else{
                    sortEntity.desc(WATERLEVEL);
                }
            }
            if(StringUtils.isNotEmpty(waterLevelQueryVO.getGatherTimeSort()) ){
                if(waterLevelQueryVO.getGatherTimeSort().equalsIgnoreCase(ASC)){
                    sortEntity.asc(GATHERTIME);
                }else{
                    sortEntity.desc(GATHERTIME);
                }
            }
            if(sortEntity.size() == 0){
                sortEntity.desc(GATHERTIME);
            }
            page.setSortEntity(sortEntity);
            pageInfo = (entityClass.equals(WaterLevelLatestMongo.class)) ?
                    (Page<T>) levelLatestMongoMapper.findPage(query, page, null, MongoDBType.PRIMARY) :
                    (Page<T>) recordMongoMapper.findPage(query, page, null, MongoDBType.PRIMARY);
            List<T> list = pageInfo.getList();
            if (list != null) {
                list.forEach(vo -> {
                    if (entityClass.equals(WaterLevelLatestMongo.class)) {
                        enrichVoWithDeviceInfo((WaterLevelLatestMongo) vo, waterLevelInfoList);
                    } else {
                        enrichVoWithDeviceInfo((WaterLevelRecordMongo) vo, waterLevelInfoList);
                    }
                });
            }
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    private void enrichVoWithDeviceInfo(WaterLevelMongoBase vo, List<WaterLevelInfo> waterLevelInfoList) {
        List<String> imageList = new ArrayList<>();
        imageList.add(obsFileUrlEncryptUtil.dealObsFileUrl(vo.getPicAddr()));
        vo.setImageList(imageList);
        waterLevelInfoList.stream()
                .filter(info -> vo.getDeviceCode().equals(info.getDeviceCode()))
                .findFirst()
                .ifPresent(info -> {
                    vo.setDeviceName(info.getDeviceName());
                    vo.setDeviceStatus(info.getDeviceStatus());
                    vo.setDeviceAddress(info.getLocation());
                });
    }

}
