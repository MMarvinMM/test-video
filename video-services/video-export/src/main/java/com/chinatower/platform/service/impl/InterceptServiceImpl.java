package com.chinatower.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.cache.MongoTemplateDelegate;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.common.entity.Result;

import com.chinatower.event.constants.Constants;

import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.IntercepiResultVo;
import com.chinatower.event.entity.dto.InterceptMongo;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;

import com.chinatower.event.util.DateUtils;

import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.constant.TableConstant;
import com.chinatower.platform.mapper.IWarningOrderMapper;
import com.chinatower.platform.service.InterceptService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.mongodb.BasicDBList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.chinatower.common.core.mongo.IndexStrategy.INTECEPTER;

@Slf4j
@Service
public class InterceptServiceImpl implements InterceptService {

    @Autowired
    private IWarningOrderMapper warningOrderMapper;

    @Autowired
    private MongoTemplateDelegate mongoTemplate;

    @Autowired
    private SystemFeignClient systemFeignClient;


    @Override
    public IntercepiResultVo getInterceptList(WarningInterceptDTO dto) {
        IntercepiResultVo resultVo = new IntercepiResultVo();

        //判断开关走新老逻辑(1:老逻辑mysql 0:新逻辑mongo)
        String flag = Constants.ONE;
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_INTERCEPT_POWER_FLAG);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            resultVo.setFlag(Constants.ONE);
            Query query = new Query();

            // 查询条件组合List
            List<Criteria> criteriaList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(dto.getCriteriaList())){
                criteriaList = dto.getCriteriaList();
            }
            if(!StringUtils.isEmpty(dto.getWarningSource())){
                criteriaList.add(Criteria.where(Constants.RES_WARNING_SOURCE).is(dto.getWarningSource()));
            }
            if(!StringUtils.isEmpty(dto.getOrderId())){
                criteriaList.add(Criteria.where(Constants.RES_WARNING_ORDER_ID).regex(".*" +dto.getOrderId()+ ".*"));
            }
            if(!CollectionUtils.isEmpty(dto.getDeviceCode())){
                criteriaList.add(Criteria.where(Constants.RES_DEVICE_CODE).in(dto.getDeviceCode()));
            }
            if((!StringUtils.isEmpty(dto.getConfidenceLevelMin()) && !StringUtils.isEmpty(dto.getMinChar())) || (!StringUtils.isEmpty(dto.getConfidenceLevelMax()) && !StringUtils.isEmpty(dto.getMaxChar()))){
                Criteria criteria = Criteria.where(Constants.PARAM_CONFIDENCE_LEVEL_MAX);
                if (!StringUtils.isEmpty(dto.getConfidenceLevelMin()) && !StringUtils.isEmpty(dto.getMinChar())) {
                    if ("100".equals(dto.getMinChar())) {
                        criteria.gt(dto.getConfidenceLevelMin());
                    } else {
                        criteria.gte(dto.getConfidenceLevelMin());
                    }
                }
                if (!StringUtils.isEmpty(dto.getConfidenceLevelMax()) && !StringUtils.isEmpty(dto.getMaxChar())) {
                    if ("100".equals(dto.getMaxChar())) {
                        criteria.lt(dto.getConfidenceLevelMax());
                    } else {
                        criteria.lte(dto.getConfidenceLevelMax());
                    }
                }
                criteriaList.add(criteria);
            }
            Criteria criteria8 = new Criteria();
            if(!StringUtils.isEmpty(dto.getSecondJudgeResult())){
                Criteria criteria1 = new Criteria("second_judge_result").is(dto.getSecondJudgeResult());
                Criteria criteria2 = new Criteria(Constants.RES_WARNING_SOURCE).is(Constants.FOUR);
                criteria8 = criteria8.andOperator(criteria1,criteria2);
                criteriaList.add(criteria8);
            }
            if(!StringUtils.isEmpty(dto.getIsSecondJudge())){
                criteriaList.add(Criteria.where("is_second_judge").is(dto.getIsSecondJudge()));
            }
            // v1.6.40 增加：是否二次识别、二次识别结果、物联设备编号/名称
            if(!StringUtils.isEmpty(dto.getIsSecondIdentify())){
                criteriaList.add(Criteria.where("is_second_identify").is(dto.getIsSecondIdentify()));
            }
            if(!StringUtils.isEmpty(dto.getSecondIdentifyResult())){
                criteriaList.add(Criteria.where("second_identify_result").is(dto.getSecondIdentifyResult()));
            }
            if(!CollectionUtils.isEmpty(dto.getIotDeviceCode())){
                criteriaList.add(Criteria.where(Constants.RES_DEVICE_CODE).in(dto.getIotDeviceCode()));
            }
            if(!StringUtils.isEmpty(dto.getProvinceId())){
                criteriaList.add(Criteria.where("province_code").is(dto.getProvinceId()));
            }
            Criteria criteria = new Criteria();
            if(!StringUtils.isEmpty(dto.getEnlargeFlag())){
                Criteria c1;
                Criteria c2;
                if(Constants.ZERO.equals(dto.getEnlargeFlag())){
                    c1 = new Criteria().orOperator(
                            Criteria.where(Constants.PARAM_ENLARGE_FLAG).is(dto.getEnlargeFlag()),
                            Criteria.where(Constants.PARAM_ENLARGE_FLAG).is(null)
                    );
                } else {
                    c1 = Criteria.where(Constants.PARAM_ENLARGE_FLAG).is(dto.getEnlargeFlag());
                }
                c2 = Criteria.where(Constants.RES_WARNING_SOURCE).in(Arrays.asList("1","4"));
                criteria.andOperator(c1, c2);
            }
            criteriaList.add(criteria);
            if(!StringUtils.isEmpty(dto.getCityId())){
                criteriaList.add(Criteria.where("city_code").is(dto.getCityId()));
            }
            if(!StringUtils.isEmpty(dto.getCountyId())){
                criteriaList.add(Criteria.where(Constants.PARAM_COUNTY_CODE).is(dto.getCountyId()));
            }
        /*if(!CollectionUtils.isEmpty(dto.getWarningTypeIdList())){
            criteriaList.add(Criteria.where("warning_type_id").in(dto.getWarningTypeIdList()));
        }*/
            if(!StringUtils.isEmpty(dto.getAlarmBody())){
                criteriaList.add(Criteria.where("alarm_body").regex(".*" +dto.getAlarmBody()+ ".*"));
            }
            if(!StringUtils.isEmpty(dto.getIntercept())){
                criteriaList.add(Criteria.where("intercept").is(dto.getIntercept()));
            }
            if(!CollectionUtils.isEmpty(dto.getInterceptList())){
                criteriaList.add(Criteria.where("intercept").in(dto.getInterceptList()));
            }
            if(!StringUtils.isEmpty(dto.getImportanceLevel())){
                criteriaList.add(Criteria.where("importance_level").is(dto.getImportanceLevel()));
            }
            if(!CollectionUtils.isEmpty(dto.getAlgorithmCodeList())){
                criteriaList.add(Criteria.where("algorithm_code").in(dto.getAlgorithmCodeList()));
            }
            if(!StringUtils.isEmpty(dto.getAlarmTimeStart()) && !StringUtils.isEmpty(dto.getAlarmTimeEnd())){
                criteriaList.add(Criteria.where("alarm_time").gte(DateUtils.date2Str(DateUtils.string2Date(dto.getAlarmTimeStart()),null)).lte(DateUtils.date2Str(DateUtils.string2Date(dto.getAlarmTimeEnd()),null)));
            }else{
                log.error("getInterceptListNew-开始及结束日期不能为空");
                return new IntercepiResultVo();
            }
            if(!CollUtil.isEmpty(dto.getOrgIdList())){
                Criteria satelliteCriteria = new Criteria();
                Criteria deviceCodeListCriteria = new Criteria();
                Criteria radarListCriteria = new Criteria();
                if(!CollectionUtils.isEmpty(dto.getSatelliteList())){
                    Criteria criteria1 = new Criteria(Constants.PARAM_COUNTY_CODE).in(dto.getSatelliteList().stream().map(SysDeptDeviceVO::getCountyId).filter(com.chinatower.platform.common.utils.StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
                    Criteria criteria2 = new Criteria(Constants.RES_WARNING_SOURCE).is("9");
                    satelliteCriteria=satelliteCriteria.andOperator(criteria1,criteria2);
                }
                if(!CollectionUtils.isEmpty(dto.getRadarList())){
                    Criteria criteria1 = new Criteria(Constants.PARAM_COUNTY_CODE).in(dto.getRadarList().stream().map(SysDeptDeviceVO::getCountyId).filter(com.chinatower.platform.common.utils.StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
                    Criteria criteria2 = new Criteria(Constants.RES_WARNING_SOURCE).is("10");
                    radarListCriteria=radarListCriteria.andOperator(criteria1,criteria2);
                }
                if(!CollectionUtils.isEmpty(dto.getDeviceCodeList())){
                    Criteria criteria1 = new Criteria(Constants.RES_DEVICE_CODE).in(dto.getDeviceCodeList());
                    Criteria criteria2 = new Criteria(Constants.RES_WARNING_SOURCE).in(Arrays.asList("1","3","4","8","12"));
                    deviceCodeListCriteria=deviceCodeListCriteria.andOperator(criteria1,criteria2);
                }
                if(!CollectionUtils.isEmpty(dto.getSatelliteList()) && !CollectionUtils.isEmpty(dto.getDeviceCodeList()) && !CollectionUtils.isEmpty(dto.getRadarList())){
                    Criteria criteria5 = new Criteria().orOperator(satelliteCriteria,deviceCodeListCriteria,radarListCriteria);
                    criteriaList.add(criteria5);
                }else if(!CollectionUtils.isEmpty(dto.getSatelliteList()) && !CollectionUtils.isEmpty(dto.getDeviceCodeList())){
                    Criteria criteria5 = new Criteria().orOperator(satelliteCriteria,deviceCodeListCriteria);
                    criteriaList.add(criteria5);
                }else if(!CollectionUtils.isEmpty(dto.getDeviceCodeList()) && !CollectionUtils.isEmpty(dto.getRadarList())){
                    Criteria criteria5 = new Criteria().orOperator(deviceCodeListCriteria,radarListCriteria);
                    criteriaList.add(criteria5);
                }else if(!CollectionUtils.isEmpty(dto.getSatelliteList()) && !CollectionUtils.isEmpty(dto.getRadarList())){
                    Criteria criteria5 = new Criteria().orOperator(satelliteCriteria,radarListCriteria);
                    criteriaList.add(criteria5);
                }else if(!CollectionUtils.isEmpty(dto.getSatelliteList())){
                    criteriaList.add(satelliteCriteria);
                }else if(!CollectionUtils.isEmpty(dto.getDeviceCodeList())){
                    criteriaList.add(deviceCodeListCriteria);
                }else if(!CollectionUtils.isEmpty(dto.getRadarList())){
                    criteriaList.add(radarListCriteria);
                }
            }
            if(!CollectionUtils.isEmpty(dto.getDevList())){
                criteriaList.add(Criteria.where(Constants.RES_DEVICE_CODE).in(dto.getDevList().stream().map(AlarmOrderByDeviceDTO::getDeviceCode).filter(com.chinatower.platform.common.utils.StringUtils::isNotBlank).distinct().collect(Collectors.toList())));
            }
            if(!CollectionUtils.isEmpty(dto.getDeviceCodes())){
                criteriaList.add(Criteria.where(Constants.RES_DEVICE_CODE).in(dto.getDeviceCodes()));
            }
            if(!CollectionUtils.isEmpty(dto.getWarningTypeIdList())){
                criteriaList.add(Criteria.where("warning_type_id").in(dto.getWarningTypeIdList()));
            }
            if (!CollectionUtils.isEmpty(dto.getLableIdList())) {
                if(!StringUtils.isEmpty(dto.getOrderId())){
                    criteriaList.add(Criteria.where(Constants.RES_WARNING_ORDER_ID).regex(".*" +dto.getOrderId()+ ".*"));
                }else{
                    criteriaList.add(Criteria.where(Constants.RES_WARNING_ORDER_ID).in(dto.getWarningOrderIds()));
                }
            }
            criteriaList.add(Criteria.where("judge_result").ne(Constants.ONE));

            Criteria[] criteriaArray = criteriaList.toArray(new Criteria[criteriaList.size()]);
            // 调用框架原方法
            BasicDBList basicDBList = this.createCriteriaList(criteriaArray);

            // 条件拼接
            Criteria criteriaAll = new Criteria("$and").is(basicDBList);
            query.addCriteria(criteriaAll);
            INTECEPTER.withHint(query);
            List<InterceptVO> resultList = new ArrayList<>();

            Date endDate = DateUtils.string2Date(dto.getAlarmTimeEnd());

            log.info("回收站查询query条件:{}", JSON.toJSONString(query));
            String tableName = TableUtil.getTableName(endDate, TableConstant.WARN_ALARM_INTERCEPT+"_");
            long count = mongoTemplate.count(query,tableName);
            Sort sort = Sort.by(Sort.Direction.DESC,Constants.PARAM_ALARM_TIME);
            if("reciveTime".equals(dto.getSortName())){
                sort = Sort.by(dto.getSort(),"create_time");
            }
            if("alarmTime".equals(dto.getSortName())){
                sort = Sort.by(dto.getSort(),Constants.PARAM_ALARM_TIME);
            }
            if(Constants.PARAM_IMPORTANCE_LEVEL.equals(dto.getSortName())){
                //告警等级一样时，按照告警时间倒序排序
                if (EnumUtil.Order.DESC.getName().equals(dto.getSort())) {
                    sort = Sort.by(Sort.Direction.DESC, Constants.PARAM_IMPORTANCE_LEVEL).and(Sort.by(Sort.Direction.DESC, Constants.PARAM_ALARM_TIME));
                } else {
                    sort = Sort.by(Sort.Direction.ASC, Constants.PARAM_IMPORTANCE_LEVEL).and(Sort.by(Sort.Direction.DESC, Constants.PARAM_ALARM_TIME));
                }
            }
            query.with(sort);

            /*PageRequest pageable =PageRequest.of(dto.getPageNum()-1, dto.getPageSize());
            query.with(pageable);*/
            log.info("回收站查询query条件带排序:{}", JSON.toJSONString(query));
            List<InterceptMongo> interceptVOList = mongoTemplate.find(query,InterceptMongo.class,tableName);
            List<InterceptVO> list = new ArrayList<>();
            for (InterceptMongo mongo:interceptVOList) {
                InterceptVO vo = new InterceptVO();
                BeanUtil.copyProperties(mongo,vo);
                resultList.add(vo);
            }

            resultVo.setTotal(Integer.valueOf(String.valueOf(count)));
            resultVo.setResultList(resultList);
        }else{
            resultVo.setResultList(warningOrderMapper.getInterceptList(dto));
        }
        return resultVo;
    }

    private BasicDBList createCriteriaList(Criteria[] criteria) {
        BasicDBList bsonList = new BasicDBList();
        Criteria[] var3 = criteria;
        int var4 = criteria.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Criteria c = var3[var5];
            bsonList.add(c.getCriteriaObject());
        }

        return bsonList;
    }

}
