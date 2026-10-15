package com.chinatower.emergency.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.emergency.dto.*;
import com.chinatower.emergency.mapper.EmergencyUserMapper;
import com.chinatower.emergency.mapper.EmergencyUserTrailMapper;
import com.chinatower.emergency.service.EmergencyUserService;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Pois;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateTime.now;

/**
 * @author Liqi
 */
@Service
@Slf4j
public class EmergencyUserServiceImpl extends BaseEventInfoService implements EmergencyUserService {

    @Autowired
    private EmergencyUserMapper emergencyUserMapper;

    @Autowired
    private EmergencyUserTrailMapper emergencyUserTrailMapper;

    @Autowired
    private GisMapApi gisMapApi;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public Result addCommander(EmergencyUserDTO dto) {
        setUserInfo(dto, true);
        //效验是否存在同组织重复用户
        List<EmergencyUserVO> emergencyUser = emergencyUserMapper.getEmergencyUser(dto);
        Map<String, String> userIdToNameMap = emergencyUser.stream()
                .collect(Collectors.toMap(EmergencyUserVO::getEmergencyUserId, EmergencyUserVO::getEmergencyUserName));

        //排除重复id
        List<String> existingIds = dto.getUserIdList().stream()
                .filter(userIdToNameMap::containsKey)
                .collect(Collectors.toList());

        StringBuilder nameBuilder = new StringBuilder();
        dto.getUserIdList().removeAll(existingIds);

        //取出重复用户名称
        for (String id : existingIds) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(",");
            }
            nameBuilder.append(userIdToNameMap.get(id));
        }

        String resultMessage;
        if (StringUtils.isNotBlank(nameBuilder)) {
            if (!dto.getUserIdList().isEmpty()) {
                emergencyUserMapper.addCommander(dto);
            }
            resultMessage = nameBuilder + "已在此组织内";
            return Result.fail(resultMessage);
        } else {
            emergencyUserMapper.addCommander(dto);
            resultMessage = "增加成功";
            return Result.data(resultMessage);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCommander(EmergencyUserDTO dto) {
        setUserInfo(dto, true);
        emergencyUserMapper.updateCommander(dto);
        return "更换指挥官成功";
    }

    @Override
    public List<EmergencyUserVO> getEmergencyUser(EmergencyUserDTO dto) {
        setUserInfo(dto, true);
        //获取组织下所有的用户
        if (null == dto.getPageSize() || null == dto.getPageNum()) {
            dto.setPageNum(1);
            dto.setPageSize(5);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        return emergencyUserMapper.getEmergencyUser(dto);
    }

    @Override
    public String delEmergencyUser(EmergencyUserDTO dto) {
        setUserInfo(dto, true);
        emergencyUserMapper.delEmergencyUserByDept(dto);
        return "清除用户成功";
    }

    @Override
    public EmergencyUserVO checkUser() {
        String userName = SecurityUtils.getLoginUser().getUser().getUserName();
        EmergencyUserVO emergencyUserVO = Optional.ofNullable(emergencyUserMapper.checkUser(userName)).orElse(new EmergencyUserVO());
        emergencyUserVO.setCorrespondenceFlag(StringUtils.isNotBlank(emergencyUserVO.getMobileTerminalNum()));
        return emergencyUserVO;
    }

    @Override
    public Result saveLngLatByUser(SaveLngLatParam dto, Integer flag) {
        log.info("用户打点接口参数 - SaveLngLatParam{}", JSONObject.toJSONString(dto));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //铁塔切换至客户视角：不记录该铁塔用户手机位置信息。
        if (SecurityUtils.isSwitch()) {
            return Result.fail("铁塔切换客户视角不记录用户手机位置信息");
        }
        List<EmergencyUserLngLatDTO> emergencyUserLngLatDTOList = new ArrayList<>();
        EmergencyUserLngLatDTO emergencyUserLngLatDTO = new EmergencyUserLngLatDTO();
        setUserMes(emergencyUserLngLatDTO, user);
        emergencyUserLngLatDTO.setUserId(user.getUserId());
        if (flag == 2) {
            //取最后一条
            Optional<SaveLngLatParam> first = dto.getParamList().stream().max(Comparator.comparing(SaveLngLatParam::getCreateTime));
            if (first.isPresent()) {
                SaveLngLatParam latest = first.get();
                emergencyUserLngLatDTO.setLatitude(String.format("%.6f", Double.parseDouble(latest.getLatitude())));
                emergencyUserLngLatDTO.setLongitude(String.format("%.6f", Double.parseDouble(latest.getLongitude())));
                emergencyUserLngLatDTO.setTakePointTime(latest.getCreateTime());
            } else {
                return Result.fail("批量打点数据为空");
            }
            //数据转移
            for (SaveLngLatParam param : dto.getParamList()) {
                EmergencyUserLngLatDTO userLngLatDTO = new EmergencyUserLngLatDTO();
                setUserMes(userLngLatDTO, user);
                userLngLatDTO.setUserId(user.getUserId());
                userLngLatDTO.setLatitude(String.format("%.6f", Double.parseDouble(param.getLatitude())));
                userLngLatDTO.setLongitude(String.format("%.6f", Double.parseDouble(param.getLongitude())));
                userLngLatDTO.setTakePointTime(param.getCreateTime());
                emergencyUserLngLatDTOList.add(userLngLatDTO);
            }
        } else {
            emergencyUserLngLatDTO.setLatitude(String.format("%.6f", Double.parseDouble(dto.getLatitude())));
            emergencyUserLngLatDTO.setLongitude(String.format("%.6f", Double.parseDouble(dto.getLongitude())));
            emergencyUserLngLatDTO.setTakePointTime(now());
        }
        //判断是否第一次打点
        int i = emergencyUserMapper.checkUserLngLat(emergencyUserLngLatDTO);
        if (i > 0) {
            emergencyUserMapper.updateEmergencyUserLngLat(emergencyUserLngLatDTO);
        } else {
            emergencyUserMapper.addEmergencyUserLngLat(emergencyUserLngLatDTO);
        }
        //用户轨迹存储
        if (flag == 2) {
            return saveTrailBatch(emergencyUserLngLatDTOList);
        } else {
            return saveTrail(emergencyUserLngLatDTO);
        }

    }


    @Override
    public Result getUserTrail(QueryTrailsParam queryTrailsParam) {
        QueryEntity query = new QueryEntity("emergencyUserLngLatDTO", EmergencyUserLngLatDTO.class);
        query.eq("userId", queryTrailsParam.getUserId());
        if (StringUtils.isNotEmpty(queryTrailsParam.getStartTime()) && StringUtils.isNotEmpty(queryTrailsParam.getEndTime())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            try {
                Date start = simpleDateFormat.parse(queryTrailsParam.getStartTime());
                Date end = simpleDateFormat.parse(queryTrailsParam.getEndTime());
                //结束加一秒
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                calendar.add(Calendar.SECOND, 1);
                end = calendar.getTime();
                query.between("takePointTime", start, end);
            } catch (Exception e) {
                log.info(e.toString());
            }
        }
        SortEntity sort = new SortEntity();
        sort.asc("takePointTime");
        List<EmergencyUserLngLatDTO> list = emergencyUserTrailMapper.findList(query, sort, MongoDBType.PRIMARY);
        if (CollectionUtil.isNotEmpty(list) && !list.isEmpty()) {
            for (EmergencyUserLngLatDTO emergencyUserLngLatDTO : list) {
                emergencyUserLngLatDTO.setCreateTime(emergencyUserLngLatDTO.getTakePointTime());
            }
            return Result.data(list);
        } else {
            return Result.data(list, "所选时段暂无用户轨迹信息");
        }

    }

    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "veuli.tenant_id", acceptColumn = "ic.accept_node", industryCode = "d.INDUSTRY_CODE")
    public Result<List<EmergencyUserLngLatDTO>> getUserPosition(SaveLngLatParam dto) {
        List<EmergencyUserLngLatDTO> userPosition = emergencyUserMapper.getUserPosition(dto);
        for (EmergencyUserLngLatDTO emergencyUserLngLatDTO : userPosition) {
            setRegion(emergencyUserLngLatDTO);
        }
        return Result.data(userPosition);
    }

    @Override
    public List<EmergencyUserInfoDTO> getAllUserByEventCode(EmergencyEventInfoVO emergencyEventInfo) {
        if (emergencyEventInfo.getPageNum() == null || emergencyEventInfo.getPageSize() == null) {
            emergencyEventInfo.setPageNum(1);
            emergencyEventInfo.setPageSize(5);
        }
        PageHelper.startPage(emergencyEventInfo.getPageNum(), emergencyEventInfo.getPageSize());
        List<EmergencyUserInfoDTO> allUserByEventCode = emergencyUserMapper.getAllUserByEventCode(emergencyEventInfo.getEventCode());
        allUserByEventCode.forEach(item -> {
            item.setPhoneNumber(setDesTel(item.getPhoneNumber()));
        });
        return allUserByEventCode;
    }


    public String setDesTel(String phone) {
        if (StringUtil.isNotEmpty(phone)) {
            return DesensitizedUtil.mobilePhone(phone);
        }
        return null;
    }

    protected void setRegion(EmergencyUserLngLatDTO regionVO) {
        String lon = lonAndLatFormat(Double.parseDouble(regionVO.getLongitude()), 9);
        String lat = lonAndLatFormat(Double.parseDouble(regionVO.getLatitude()), 9);
        PoiContraryGeocodingQueryRes resMap = getRegion(lon, lat);
        String address = resMap.getAddress();
        if (StringUtils.isNotBlank(address)) {
            regionVO.setAddress(address);
        }
    }


    /**
     * 根据经纬度获取所属省市区
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public PoiContraryGeocodingQueryRes getRegion(String longitude, String latitude) {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        PoiContraryGeocodingQueryRes resMap = null;
        try {
            log.info("-根据经纬度获取所属省市区请求报文=={}", JSON.toJSONString(req));
            Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
            log.info("-根据经纬度获取所属省市区响应报文=={}", JSON.toJSONString(result));
            if (ObjectUtil.isNotEmpty(result) && ResultCode.SUCCESS.getCode() == result.getCode()) {
                List<PoiContraryGeocodingQueryRes> list = result.getData();
                if (!CollectionUtils.isEmpty(list)) {
                    resMap = list.get(0);
                }
            }
        } catch (Exception e) {
            log.error("===========ancientTreeGroupServiceImpl.getRegion error:{}", e);
        }
        return resMap;
    }

    /**
     * 处理经纬度信息，小数位超出9位则四舍五入，不够则补0
     *
     * @param value
     * @param scale
     * @return
     */
    private static String lonAndLatFormat(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value);
        // 如果小数位数不足9位，则补0
        if (bd.scale() < scale) {
            bd = bd.setScale(scale, RoundingMode.HALF_UP);
        }
        // 如果小数位数超出9位，则四舍五入
        return bd.setScale(scale, RoundingMode.HALF_UP).toString();

    }

    public void setUserMes(EmergencyUserLngLatDTO dto, SysUser user) {
        dto.setTenantId(user.getTenantId());
        dto.setIndustryCode(user.getIndustryCode());
        dto.setAppVerCode(user.getAppVerCode());
    }

    /**
     * 轨迹存储
     */
    public Result saveTrail(EmergencyUserLngLatDTO vo) {
        try {
            emergencyUserTrailMapper.insert(vo, MongoDBType.PRIMARY);
        } catch (Exception e) {
            Result.fail("轨迹保存失败" + e.getMessage());
        }
        return Result.data("轨迹保存成功");
    }

    private Result saveTrailBatch(List<EmergencyUserLngLatDTO> emergencyUserLngLatDTO) {
        try {
            emergencyUserTrailMapper.insertAll(emergencyUserLngLatDTO, "emergencyUserLngLatDTO", MongoDBType.PRIMARY);
        } catch (Exception e) {
            Result.fail("轨迹保存失败" + e.getMessage());
        }
        return Result.data("轨迹保存成功");
    }


}
