package com.chinatower.device.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IIndustryDeviceServiceMigrate;
import com.chinatower.device.constants.IndErrorMsgConstants;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.dto.ApiInfoDTO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.dto.RadarTargetDTO;
import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.IndTenantDeviceDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.AnalysisMapper;
import com.chinatower.device.mapper.IndStatisticMapper;
import com.chinatower.device.repository.IChannelInfoRepository;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.repository.UserDeviceCollectionService;
import com.chinatower.device.service.IIndustryDeviceService;
import com.chinatower.device.utils.ApiInfoUtils;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * video-device
 * Created by zheng.yan on 2022/3/22.
 */
@RestController
@Slf4j
@Api(value = "行业应用设备相关接口", tags = "行业应用设备相关接口")
@RequestMapping("/industry/device")
public class IndustryDeviceController {

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;
    @Autowired
    private UserDeviceCollectionService userDeviceCollectionService;
    @Autowired
    IndStatisticMapper indStatisticMapper;
    @Autowired
    AnalysisMapper analysisMapper;
    @Resource
    VideoQueryAPI videoQueryAPI;
    @Autowired
    private IChannelInfoRepository channelInfoRepository;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private ApiInfoUtils apiInfoUtils;

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private IIndustryDeviceService iIndustryDeviceService;

    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    private ICasCadeInfoService iCasCadeInfoService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private SysAreaRootService sysAreaRootService;
    @Autowired
    private IIndustryDeviceServiceMigrate iIndustryDeviceServiceMigrate;

    public static final String VIEW_PITCHANGLE = "查询俯仰角水平角";

    public static final String PITCH_ANGLE = "pitchAngle";
    public static final String VIEW_XANGLE = "viewXAngle";

    public static final String VIEW_YANGLE = "viewYAngle";

    /**
     * 查询摄像头信息
     *
     * @param deviceIndReq 请求参数
     * @return Result<DeviceIndDTO>
     * @author zheng.yan
     * @date 2022/3/22
     * @version
     */
    @ApiOperation(value = "查询摄像头信息", notes = "查询摄像头信息")
    @PostMapping("/getDeviceChannel")
    //@IndustryAuth
    @Deprecated
    public Result<DeviceIndDTO> getDeviceChannel(@Valid @RequestBody DeviceIndReqDTO deviceIndReq) {
        log.info("废弃接口调用");
        DeviceIndDTO device = null;
        deviceIndReq.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<DeviceIndDTO> devices = deviceInfoRepository.listDeviceChannelByCond(deviceIndReq);
        if (!devices.isEmpty()) {
            device = devices.get(0);
        }
        return Result.data(device);
    }

    /**
     * 功能描述 查询行业应用摄像机树
     *
     * @param monitorTreeInfReq 请求参数
     * @return Result<List < DeviceIndDTO>>
     * @author Chensw
     * @date 2022/3/23
     */
    @PostMapping("queryMonitorIndTree")
    @ApiOperation("查询行业应用摄像机树")
    //@IndustryAuth
    public Result<List<IndustryDeviceTree>> queryMonitorIndTree(@RequestBody @Valid MonitorTreeInfReq monitorTreeInfReq) {
        List<IndustryDeviceTree> devices;
        if (Objects.equals(monitorTreeInfReq.getDisplayMode(), IndustryConstants.ORGANIZE)) {
            devices = deviceInfoRepository.queryMonitorIndDeptTree(monitorTreeInfReq);
        } else {
            devices = deviceInfoRepository.queryMonitorIndTree(monitorTreeInfReq);
        }
        return Result.data(devices);
    }

    /**
     * 通过站址信息查询设备
     *
     * @param deviceIndReqDTO 请求参数
     * @return Result<List < DeviceIndDTO>>
     * @author trk
     * @date 2022/3/23
     */
    @ApiOperation(value = "通过站址信息查询设备", notes = "通过站址信息查询设备")
    //@IndustryAuth
    @PostMapping("/queryDevicesBySite")
    public Result<List<DeviceIndDTO>> queryDevicesBySite(@RequestBody DeviceIndReqDTO deviceIndReqDTO) {
        if (StringUtils.isBlank(deviceIndReqDTO.getSiteCode()) && StringUtils.isBlank(deviceIndReqDTO.getSiteName())
                && CollectionUtils.isEmpty(deviceIndReqDTO.getDeviceCodes()) && CollectionUtils.isEmpty(deviceIndReqDTO.getSiteCodes())) {
            return Result.fail("站址信息或设备信息不能为空！");
        }
        List<DeviceIndDTO> list = deviceInfoRepository.queryDevicesBySite(deviceIndReqDTO);
        return Result.data(list);
    }

    /**
     * 功能描述 查询摄像机图层
     *
     * @param deviceLayerReq 请求参数
     * @return Result
     * @author Chensw
     * @date 2022/3/24 10:53
     */
    @ApiOperation(value = "查询摄像机图层")
    @PostMapping("/queryDeviceLayer")
    //@IndustryAuth
    public Result<List<IndustryDeviceTree>> queryDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq) {
        List<IndustryDeviceTree> devices = deviceInfoRepository.queryDeviceLayer(deviceLayerReq);
        Set<String> fileUrls = new HashSet<>();
        devices.forEach(device -> {
            device.getList().forEach(dev -> {
                fileUrls.add(dev.getImageUrl());
            });
        });
        //加密
        Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
        devices.forEach(device -> {
            device.getList().forEach(dev -> {
                if (!StringUtils.isEmpty(dev.getImageUrl())) {
                    dev.setImageUrl(result.get(dev.getImageUrl()));
                }
            });
        });
        return Result.data(devices);
    }

    @ApiOperation(value = "查询传感器图层")
    @PostMapping("/querySensorLayer")
    //@IndustryAuth
    public Result<List<IndustryDeviceTree>> querySensorLayer(@RequestBody DeviceLayerReq deviceLayerReq) {
        List<IndustryDeviceTree> devices = iotDeviceRepository.querySensorLayer(deviceLayerReq);
        return Result.data(devices);
    }

    /**
     * 新增/取消收藏
     *
     * @param userDeviceCollectionDTO
     * @return Result
     * @author trk
     * @date 2022/3/25
     */
    @ApiOperation(value = "新增/取消收藏")
    @PostMapping("/addOrCancelCollections")
    Result addOrCancelCollections(@RequestBody UserDeviceCollectionDTO userDeviceCollectionDTO) {
        if (userDeviceCollectionDTO.getOptType() == 1) {
            userDeviceCollectionService.addCollections(userDeviceCollectionDTO.getCollObjCode(), userDeviceCollectionDTO.getCollObjType());
            return Result.success("新增收藏成功");
        } else {
            userDeviceCollectionService.cancelCollections(userDeviceCollectionDTO.getCollObjCode(), userDeviceCollectionDTO.getCollObjType());
            return Result.success("取消收藏成功");
        }
    }

    /**
     * 查询用户设备收藏信息
     */
    @ApiOperation(value = "查询收藏信息")
    @PostMapping("/queryCollections")
    public Result<List<UserDeviceCollectionQueryVO>> queryCollections(@RequestBody UserDeviceCollectionQueryDTO param) {
        try {
            log.info("IndustryDeviceController queryCollections param:{}", JSON.toJSONString(param));
            List<UserDeviceCollectionQueryVO> resultList = userDeviceCollectionService.queryCollections(param);
            return Result.data(resultList);
        } catch (Exception e) {
            log.error("IndustryDeviceController queryCollections error, e:", e);
            return Result.fail("查询收藏信息失败");
        }
    }

    /**
     * 功能描述 统计监控设备数量
     *
     * @param indTenantDeviceDTO 请求参数
     * @return Result<IndDeviceVO>
     * @author Chensw
     * @date 2022/4/1 9:27
     */
    @ApiOperation(value = "统计监控设备数量")
    @PostMapping("/queryDeviceStatistics")
    //@IndustryAuth
    Result<IndDeviceVO> queryDeviceStatistics(@RequestBody @Valid IndTenantDeviceDTO indTenantDeviceDTO) {
        log.info("=======行业应用统计监控设备数量入参========={}", JSON.toJSONString(indTenantDeviceDTO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        indTenantDeviceDTO.setTenantId(user.getTenantId());
        IndDeviceVO indDeviceVO;
        // 租户管理员是查询租户id下的数据
        if (DeviceConstants.TENANT_ADMIN.equals(user.getIsTenantAdmin()) && indTenantDeviceDTO.getCategoryCode() == null) {
            indTenantDeviceDTO.setTenantId(user.getTenantId());
            indTenantDeviceDTO.setIndustryCode(user.getIndustryCode());
            indDeviceVO = indStatisticMapper.getStatisticsDevices(indTenantDeviceDTO);
        } else {
            indDeviceVO = new IndDeviceVO();
            List<Map<String, Integer>> devList = analysisMapper.getStatisticsDevicesUserInd(indTenantDeviceDTO);
            int devCountOffline = 0;
            int devCountOnline = 0;
            for (Map<String, Integer> map : devList) {
                //在线
                if (map.get("status") == DeviceConstants.ON_LINE_FLAG) {
                    devCountOnline++;
                } else {
                    devCountOffline++;
                }
            }
            //设备为空，则不查询通道
            if (!CollectionUtils.isEmpty(devList)) {
                List<Map<String, Integer>> channelList = analysisMapper.getStatisticsChannelUserInd(indTenantDeviceDTO);
                int channelCountOffline = 0;
                int channelCountOnline = 0;
                for (Map<String, Integer> map : channelList) {
                    //在线
                    if (map.get("status") == DeviceConstants.ON_LINE_FLAG) {
                        channelCountOnline++;
                    } else {
                        channelCountOffline++;
                    }
                }
                indDeviceVO.setChannelCountOffline(channelCountOffline)
                        .setChannelCountOnline(channelCountOnline)
                        .setChannelCountTotal(channelList.size());
            } else {
                indDeviceVO.setChannelCountOffline(0)
                        .setChannelCountOffline(0)
                        .setChannelCountTotal(0);
            }
            indDeviceVO.setDevCountOffline(devCountOffline)
                    .setDevCountOnline(devCountOnline)
                    .setDeviceCountTotal(devList.size());
        }
        return Result.data(indDeviceVO);
    }

    /**
     * 功能描述 告警类型统计
     *
     * @param indStatisticDTO
     * @return Result<IndDevTypeVO>
     * @author Chensw
     * @date 2022/4/1 11:29
     */
    @ApiOperation("告警类型统计")
    @PostMapping("getDevTypeYearMonth")
    //@IndustryAuth
    @DataScope(deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    Result<List<IndDevTypeVO>> getDevTypeYearMonth(@RequestBody @Valid IndStatisticDTO indStatisticDTO) {
        log.info("=======行业应用告警类型统计入参========={}", JSON.toJSONString(indStatisticDTO));
        if (indStatisticDTO.getDimensionFlag() == null) {
            return Result.fail("操作标识不能为空");
        }
        List<IndDevTypeVO> list = indStatisticMapper.getDevTypeYearMonth(indStatisticDTO);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        } else {
            // 找到对应typeId 的typeName
            List<String> ids = list.stream().map(IndDevTypeVO::getTypeId).collect(Collectors.toList());
            List<Map<String, String>> maps = indStatisticMapper.getTypeName(ids);
            list.forEach(typeVO -> {
                String typeId = typeVO.getTypeId();
                String typeName = "";
                if (StringUtils.isNotEmpty(typeId)) {
                    for (Map<String, String> m : maps) {
                        if (typeId.equals(String.valueOf(m.get(DeviceConstants.TYPE_ID)))) {
                            typeName = m.get(DeviceConstants.TYPE_NAME);
                        }
                    }
                }
                typeVO.setTypeName(typeName);
            });
        }
        List<IndDevTypeVO> resList = new ArrayList<>();
        int count = 0;
        int sum = 0;
        // 列出前5个告警类型，剩下的归到其他中
        for (IndDevTypeVO vo : list) {
            if(StringUtils.isBlank(vo.getTypeName())){
                continue;
            }
            count++;
            if (count <= 5) {
                resList.add(vo);
            } else {
                sum += vo.getNum();
            }
        }
        if (sum > 0) {
            IndDevTypeVO typeVO = new IndDevTypeVO().setNum(sum).setTypeId("aaa666").setTypeName("其他");
            resList.add(typeVO);
        }
        return Result.data(resList);
    }

    /**
     * 功能描述 告警来源统计
     *
     * @param indStatisticDTO
     * @return Result<IndEventAlarmNumVO>
     * @author Chensw
     * @date 2022/4/1 11:29
     */
    @ApiOperation("告警来源统计")
    @PostMapping("getEventAlarmNumYearMonth")
    //@IndustryAuth
    @DataScope(deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    Result<IndEventAlarmNumVO> getEventAlarmNumYearMonth(@RequestBody @Valid IndStatisticDTO indStatisticDTO) {
        log.info("行业应用告警来源统计入参========={}", JSON.toJSONString(indStatisticDTO));
        IndEventAlarmNumVO alarmNumYearMonth = indStatisticMapper.getEventAlarmNumYearMonth(indStatisticDTO);
        List<Map> statisticsList = indStatisticMapper.getAllEventAlarmNumYearMonth(indStatisticDTO);
        if (!CollectionUtils.isEmpty(statisticsList)) {
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(IndustryConstants.WARNING_SOURCE_COMMON).getData();
            for (Map<String,Object> map : statisticsList) {
                warningSourceEnum.forEach(item -> {
                    if (item.getDictValue().equals(MapUtils.getString(map,"warningSource"))) {
                        map.put("warningSourceName",item.getDictLabel());
                    }
                });
            }
            alarmNumYearMonth.setSourceList(statisticsList);
        }
        return Result.data(alarmNumYearMonth);
    }

    /**
     * 功能描述 告警高频区域统计
     *
     * @param indStatisticDTO
     * @return Result<IndEventNumAreaVO>Result<IndEventNumAreaVO>
     * @author Chensw
     * @date 2022/4/1 11:29
     */
    @ApiOperation("告警高频区域统计")
    @PostMapping("getEventNumByArea")
    //@IndustryAuth
    @DataScope(deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    Result<List<IndEventNumVO>> getEventNumByArea(@RequestBody IndStatisticDTO indStatisticDTO) {
        if (StringUtils.isBlank(indStatisticDTO.getQueryStartTime()) && StringUtils.isBlank(indStatisticDTO.getQueryEndTime())) {
            if (indStatisticDTO.getDimensionFlag() == null) {
                return Result.fail("操作标识不能为空");
            }
        }
        log.info("=======行业应用告警来源统计入参========={}", JSON.toJSONString(indStatisticDTO));

        List<IndEventNumVO> eventVOList = indStatisticMapper.getEventNumByArea(indStatisticDTO);
        if (CollectionUtils.isEmpty(eventVOList)) {
            return  Result.data(new ArrayList<>());
        }
        ArrayList<String> areaCodes = new ArrayList<>(5);
        eventVOList.forEach(vo -> {
            areaCodes.add(vo.getProvinceId());
            areaCodes.add(vo.getCityId());
            areaCodes.add(vo.getCountyId());
        });
        // 查询用到的省市区乡镇信息， 后面直接组装
        List<SysArea> areas = analysisMapper.getSysAreas(areaCodes, 0);
        Map<String, String> resultMap = areas.stream().collect(Collectors.toMap(SysArea::getAreaCode, SysArea::getAreaName));

        // 替换掉原有的省市县名字
        StringBuilder regionName;
        Integer other = 0;
        ArrayList<IndEventNumVO> eventNumVOS = new ArrayList<>();
        for (IndEventNumVO eventVO : eventVOList) {
            regionName = new StringBuilder();
            if (StringUtils.isEmpty(eventVO.getProvinceId()) || StringUtils.isEmpty(eventVO.getCityId()) || StringUtils.isEmpty(eventVO.getCountyId())){

                other = other + eventVO.getNum();
            }else {
                regionName.append(MapUtils.getString(resultMap, eventVO.getProvinceId(), ""))
                        .append(MapUtils.getString(resultMap, eventVO.getCityId(), ""))
                        .append(MapUtils.getString(resultMap, eventVO.getCountyId(), ""));
                eventVO.setRegionName(regionName.toString());
                eventNumVOS.add(eventVO);
            }
        }
        if (other != 0){
            IndEventNumVO NumOther = new IndEventNumVO();
            NumOther.setNum(other);
            NumOther.setRegionName("其它");
            eventNumVOS.add(NumOther);
        }
        return Result.data(eventNumVOS);
    }

    /**
     * 功能描述 告警趋势统计
     *
     * @param indEventTrendDTO
     * @return Result<IndEventTrendVO>
     * @author Chensw
     * @date 2022/4/1 11:29
     */
    @ApiOperation("告警趋势统计")
    @PostMapping("getEventTrend")
    //@IndustryAuth
    @DataScope(deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    Result<List<IndEventTrendVO>> getEventTrend(@RequestBody @Valid IndEventTrendDTO indEventTrendDTO) {
        log.info("=======行业应用告警来源统计入参========={}", JSON.toJSONString(indEventTrendDTO));
        List<IndEventTrendVO> list;
        if (DeviceConstants.YEAR.equals(indEventTrendDTO.getTrendFlag())) {
            list = indStatisticMapper.getEventTrendYear(indEventTrendDTO);
        } else if (DeviceConstants.MONTH.equals(indEventTrendDTO.getTrendFlag())) {
            list = indStatisticMapper.getEventTrendMonth(indEventTrendDTO);
        } else if (DeviceConstants.DAY.equals(indEventTrendDTO.getTrendFlag())) {
            list = indStatisticMapper.getEventTrendDay(indEventTrendDTO);
        } else if (StringUtils.isNotBlank(indEventTrendDTO.getQueryStartTime()) && StringUtils.isNotBlank(indEventTrendDTO.getQueryEndTime())) {
            list = indStatisticMapper.getEventTrendQuery(indEventTrendDTO);
        } else {
            return Result.fail("传入参数错误");
        }
        return Result.data(list);
    }

    /**
     * 遍历数据
     *
     * @param list
     * @param labelString
     * @param searchMap
     */
    public void structureRangeDate(List<IndEventTrendVO> list, List<String> labelString, Map<String, Map> searchMap) {
        IndEventTrendVO vo;
        for (String s : labelString) {
            vo = new IndEventTrendVO();
            vo.setLabel(s);
            Integer num = 0;
            if (null != searchMap) {
                Map map = searchMap.get(s);
                if(null != map){
                    num = ((BigDecimal) map.get("sum")).intValue();
                }
            }
            vo.setNum(num == null ? 0 : num);
            list.add(vo);
        }
    }

    /**
     * 获取时间集合
     *
     * @param startTime
     * @param endTime
     * @param trendFlag
     * @return
     */
    public List<String> getRangeDate(String startTime, String endTime, String trendFlag) {
        //定义时间格式
        SimpleDateFormat simpleDateFormat = null;
        if (DeviceConstants.YEAR.equals(trendFlag)) {
            startTime = startTime.substring(0, 4);
            endTime = endTime.substring(0, 4);
            simpleDateFormat = new SimpleDateFormat("yyyy");
        } else if (DeviceConstants.MONTH.equals(trendFlag)) {
            startTime = startTime.substring(0, 7);
            endTime = endTime.substring(0, 7);
            simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        } else if (DeviceConstants.DAY.equals(trendFlag)) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        List<String> list = new ArrayList<>();
        try {
            // 转化成日期类型
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime() <= endDate.getTime()) {
                // 把日期添加到集合
                list.add(simpleDateFormat.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                if (DeviceConstants.YEAR.equals(trendFlag)) {
                    //把日期增加一年
                    calendar.add(Calendar.YEAR, 1);
                } else if (DeviceConstants.MONTH.equals(trendFlag)) {
                    //把日期增加一月
                    calendar.add(Calendar.MONTH, 1);
                } else if (DeviceConstants.DAY.equals(trendFlag)) {
                    //把日期增加一天
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // 获取增加后的日期
                startDate = calendar.getTime();
            }
        } catch (Exception e) {
            log.error("getRangeDate异常", e);
        }
        return list;
    }


    /**
     * 功能描述 告警高频时段统计
     *
     * @param indAnalysisVO
     * @return Result<IndEventNumVO>
     * @author Chensw
     * @date 2022/4/1 11:29
     */
    @ApiOperation("告警高频时段统计")
    @PostMapping("getEventNumByHour")
    //@IndustryAuth
    @DataScope(deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    Result<List<Map>> getEventNumByHour(@RequestBody @Valid IndAnalysisVO indAnalysisVO) {
        log.info("=======行业应用告警高频时段统计入参========={}", JSON.toJSONString(indAnalysisVO));
        return Result.data(indStatisticMapper.getEventNumByHour(indAnalysisVO));
    }

    @ApiOperation(value = "查询摄像机型号信息", notes = "查询摄像机型号信息")
    @PostMapping("/getDeviceModelInfo")
        //@IndustryAuth
    Result<DeviceModelDTO> getDeviceModelInfo(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        if (StringUtils.isBlank(deviceInfoReq.getChannelCode())) {
            LambdaQueryWrapper<ChannelInfoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChannelInfoDO::getDeviceCode, deviceInfoReq.getDeviceCode());
            queryWrapper.eq(ChannelInfoDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL);
            List<ChannelInfoDO> channelList = channelInfoRepository.list(queryWrapper);
            for (ChannelInfoDO channelInfo : channelList) {
                if (StringUtils.isNotBlank(channelInfo.getChannelName()) && channelInfo.getChannelName().endsWith("_0") && channelInfo.getFacade() != null && channelInfo.getFacade() == 1) {
                    deviceInfoReq.setChannelCode(channelInfo.getChannelCode());
                    break;
                }
            }
            // 对于多通道的 先获取第一个通道，但是通道的外形facade，如果是3，就不行，换为1球机
            if (StringUtils.isBlank(deviceInfoReq.getChannelCode()) && !channelList.isEmpty()) {
                if(channelList.size() > 1){
                    ChannelInfoDO channelInfoDO = channelList.get(0);
                    if(channelInfoDO.getFacade() != null && channelInfoDO.getFacade() == 1){
                        deviceInfoReq.setChannelCode(channelInfoDO.getChannelCode());
                    }else{
                        deviceInfoReq.setChannelCode(channelList.get(1).getChannelCode());
                    }
                }else{
                    deviceInfoReq.setChannelCode(channelList.get(0).getChannelCode());
                }
            }
        }
        // 获取摄像机型号信息
        List<DeviceModelDTO> deviceModels = deviceInfoRepository.getDeviceModel(deviceInfoReq);
        if (deviceModels == null || deviceModels.isEmpty()) {
            return Result.data(null);
        }
        DeviceModelDTO deviceModel;
        try {
            deviceModel = calDeviceVisible(deviceInfoReq, deviceModels.get(0),1);
        } catch (Exception e) {
            deviceModel = deviceModels.get(0);
            deviceModel.setHorizViewRange(null);
            deviceModel.setVertiViewRange(null);
            deviceModel.setHorizRange(null);
            deviceModel.setVertiRange(null);
        }
        return Result.data(deviceModel);
    }

    /**
     * 计算可视域
     *
     * @param: deviceInfoReq
     * @param: deviceModel
     * @return: DeviceModelDTO
     * @author: zheng.yan
     * @date: 2022/5/13
     * @version:
     */
    private DeviceModelDTO calDeviceVisible(DeviceIndReqDTO deviceInfoReq, DeviceModelDTO deviceModel,int flag) {
        long time = new Date().getTime();
        log.info(deviceInfoReq.getDeviceCode()+"  calDeviceVisible  start " + (time) +"ms");
        DeviceModelDTO result = new DeviceModelDTO();
        if (StringUtils.isBlank(deviceModel.getMinFocus()) || deviceModel.getRefPoints() == null || deviceModel.getRefPoints().isEmpty()) {
            log.error(IndErrorMsgConstants.DEVICE_VISUAL_NOT_FOUND, HttpStatus.HTTP_NOT_FOUND);
            result.setDeviceCode(deviceModel.getDeviceCode());
            result.setDeviceName(deviceModel.getDeviceName());
            result.setVisualRange(deviceModel.getVisualRange());
            result.setMonitorAngle(deviceInfoReq.getMonitorAngle());
            result.setMonitorDirection(deviceInfoReq.getMonitorDirection());
            result.setDeviceSubType(deviceInfoReq.getDeviceSubType());
            return result;
        }

        // 调用接口获取ptz
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.QUERY_PTZ_POSITION);
        apiInfoDTO.setDeviceCode(deviceInfoReq.getDeviceCode());
        apiInfoDTO.setChannelCode(deviceInfoReq.getChannelCode());
        apiInfoDTO.setTenantId(deviceInfoReq.getTenantId());
        log.info("start call {}",DeviceNorthIntfConstants.QUERY_PTZ_POSITION);
        Result<JSONObject> ptzResult = null;
        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PTZ_POSITION);
            // 接口路径参数
            path.setDeviceCode(deviceInfoReq.getDeviceCode());
            path.setChannelCode(deviceInfoReq.getChannelCode());
            paramter.setPath(path);
            // 属地化数据统一改造 add by wangyt 20240129
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(deviceInfoReq.getDeviceCode());
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(deviceInfoReq.getDeviceCode()+"  calDeviceVisible请求时间" + (new Date().getTime() - time) +"ms");
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(deviceInfoReq.getDeviceCode()+"  calDeviceVisible返回时间" + (new Date().getTime() - time) +"ms");
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            ptzResult = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.CAL_DEVICE_VISIBLE_ERROR,e);
        }

        String transPan = null;
        String transTilt = null;
        String transZoom = null;
        if (ResultCode.SUCCESS.getCode() == ptzResult.getCode()) {
            // ptz 转换
            DeviceModelVO model = new DeviceModelVO();
            BeanUtils.copyProperties(deviceModel, model);
            JSONObject ptzData = ptzResult.getData().getJSONObject(DeviceNorthIntfConstants.DATA);
            transPan = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_PAN_STR), model, LongLatAngleUtils.TYPE_P);
            transTilt = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_TILT_STR), model, LongLatAngleUtils.TYPE_T);
            transZoom = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_ZOOM_STR), model, LongLatAngleUtils.TYPE_Z);
            //返回原始ptz值，方便核查问题
            deviceModel.setOriginalPan(ptzData.getString(DeviceConstants.PTZ_PAN_STR));
            deviceModel.setOriginalTilt(ptzData.getString(DeviceConstants.PTZ_TILT_STR));
            deviceModel.setOriginalZoom(ptzData.getString(DeviceConstants.PTZ_ZOOM_STR));
        } else {
            if(flag == 2){
                log.error(ptzResult.getMsg(), ptzResult.getCode());
                return result;
            }else{
                throw new CustomException(ptzResult.getMsg(), ptzResult.getCode());
            }
        }
        log.info("trans p:{}t:{}z:{}", transPan, transTilt, transZoom);
        // 获取p0 t0
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(transPan);
        List<SysDictData> enumListNew = new ArrayList<>();
        deviceModelVO = LongLatAngleUtils.getValidOffset(deviceModel.getRefPoints(), deviceModelVO,enumListNew);
        deviceModel.setPan(deviceModelVO.getP0());
        deviceModel.setTilt(deviceModelVO.getT0());
        BigDecimal zoom = new BigDecimal(transZoom);
        // z为0判断，避免计算报错
        if (BigDecimal.ZERO.compareTo(zoom) == 0) {
            if(flag == 2){
                log.error(IndErrorMsgConstants.DEVICE_ZOOM_ZERO, HttpStatus.HTTP_INTERNAL_ERROR);
                return result;
            }else{
                throw new CustomException(IndErrorMsgConstants.DEVICE_ZOOM_ZERO, HttpStatus.HTTP_INTERNAL_ERROR);
            }
        }
        try {
            //查询是否开启调用基础俯仰角水平角
            String configValue = deviceInfoRepository.getConfigByType("visible_ptz_config");
            // 俯仰角
            String pitchAngle = "";
            // 水平视场角
            String viewXAngle = "";
            // 垂直视场角
            String viewYAngle = "";
            if(StringUtils.isNotEmpty(configValue) && Constants.ONE.equals(configValue) ){
                DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
                deviceIndReqDTO.setDeviceCode(deviceInfoReq.getDeviceCode());
                deviceIndReqDTO.setChannelCode(deviceInfoReq.getChannelCode());
                Result<JSONObject> result1 = getViewPitchAngle(deviceIndReqDTO);
                if(ResultCode.SUCCESS.getCode() == result1.getCode()){
                    JSONObject json = result1.getData();
                    pitchAngle = json.get(PITCH_ANGLE) == null ? "": json.get(PITCH_ANGLE).toString();
                    viewXAngle = json.get(VIEW_XANGLE) == null ? "": json.get(VIEW_XANGLE).toString();
                    viewYAngle = json.get(VIEW_YANGLE) == null ? "": json.get(VIEW_YANGLE).toString();
                }
            }
            // f= f0*z'
            BigDecimal realFocal = new BigDecimal(deviceModel.getMinFocus()).multiply(zoom).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigDecimalTwo = new BigDecimal(2);
            BigDecimal doubleRealFocal = bigDecimalTwo.multiply(realFocal);
            // arctan(c/2f)
            double c2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorLength()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(c/2f)
            // 取得摄像机基础平台水平视场角值
            if(StringUtils.isNotEmpty(viewXAngle)){
                deviceModel.setHorizRange(viewXAngle);
            }else{
                deviceModel.setHorizRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(c2f)).toPlainString());
            }
            // arctan(d/2f)
            double d2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorWidth()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(d/2f)
            deviceModel.setVertiRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(d2f)).toPlainString());
            // p'-p0'
            BigDecimal pTemp = new BigDecimal(transPan).subtract(deviceModel.getPan());
            if(pTemp.compareTo(BigDecimal.ZERO) < 0){
                pTemp = pTemp.add(new BigDecimal(360));
            }
            log.info("换算P值：{}",pTemp);
            deviceModel.setHorizViewRange(pTemp.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            // t'-t0'
            // 垂直视场角
            if(StringUtils.isNotEmpty(viewYAngle)){
                deviceModel.setVertiViewRange(viewYAngle);
            }else{
                deviceModel.setVertiViewRange(new BigDecimal(transTilt).subtract(deviceModel.getTilt()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            }
            //俯仰角
            if(StringUtils.isNotEmpty(pitchAngle)){
                deviceModel.setPitchAngle(pitchAngle);
            }
            // 监控角度
            if(StringUtils.isNotEmpty(deviceInfoReq.getMonitorAngle())){
                deviceModel.setMonitorAngle(deviceInfoReq.getMonitorAngle());
            }else{
                deviceModel.setMonitorAngle(deviceModel.getHorizRange());
            }
            //监控方向
            if(StringUtils.isNotEmpty(deviceInfoReq.getMonitorDirection())){
                deviceModel.setMonitorDirection(deviceInfoReq.getMonitorDirection());
            }else{
                deviceModel.setMonitorDirection(deviceModel.getHorizViewRange());
            }
            // 设备子类型
            deviceModel.setDeviceSubType(deviceInfoReq.getDeviceSubType());
        } catch (Exception e) {
            log.error("calDevice", e);
            if(flag == 2){
                log.error(IndErrorMsgConstants.DEVICE_CAL_VISIBLE_ERROR, HttpStatus.HTTP_INTERNAL_ERROR);
            }else{
                throw new CustomException(IndErrorMsgConstants.DEVICE_CAL_VISIBLE_ERROR, HttpStatus.HTTP_INTERNAL_ERROR);
            }
        }
        log.info(deviceInfoReq.getDeviceCode()+"calDeviceVisible耗时" + (new Date().getTime() - time) +"ms");
        return deviceModel;
    }

    @ApiOperation(value = "根据站址里列表查询摄像机可视域", notes = "根据站址里列表查询摄像机可视域")
    @PostMapping("/getDeviceVisibleBySite")
    //@IndustryAuth
    public Result<List<DeviceModelDTO>> getDeviceVisibleBySite(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        log.info("getDeviceVisibleBySite入参" + JSONObject.toJSONString(deviceInfoReq));
        if (CollectionUtils.isEmpty(deviceInfoReq.getSiteCodes()) && CollectionUtils.isEmpty(deviceInfoReq.getDeviceCodes())) {
            return Result.fail("站址编码/设备编码不能为空");
        }
        List<DeviceIndDTO> devices = deviceInfoRepository.queryDevicesBySite(deviceInfoReq);
        if (devices.isEmpty()) {
            return Result.data(null);
        }
        deviceInfoReq.setDeviceCodes(devices.stream().map(DeviceIndDTO::getDeviceCode).collect(Collectors.toList()));
        Map<String, List<DeviceIndDTO>> deviceMap = devices.stream().collect(Collectors.groupingBy(DeviceIndDTO::getDeviceCode));
        List<DeviceModelDTO> deviceModes = deviceInfoRepository.getDeviceModel(deviceInfoReq);
        List<CompletableFuture<DeviceModelDTO>> allFutureList = new ArrayList<>(deviceModes.size());
//        List<DeviceModelDTO> deviceInfoList = new ArrayList<>();
        List<DeviceThreadDTO> allDeviceInfoList = new ArrayList<>();
        for (DeviceModelDTO deviceModel : deviceModes) {
            DeviceThreadDTO deviceThreadDTO = new DeviceThreadDTO();
            DeviceIndReqDTO deviceInfo = new DeviceIndReqDTO();
            deviceInfo.setTenantId(deviceInfoReq.getTenantId());
            deviceInfo.setDeviceCode(deviceModel.getDeviceCode());
            deviceInfo.setMonitorAngle(deviceModel.getMonitorAngle());
            deviceInfo.setMonitorDirection(deviceModel.getMonitorDirection());
            deviceInfo.setDeviceSubType(deviceModel.getDeviceSubType());
            List<DeviceIndDTO> deviceIndDTOS = deviceMap.get(deviceModel.getDeviceCode());
            if(!CollectionUtils.isEmpty(deviceIndDTOS)){
                DeviceIndDTO device = deviceIndDTOS.get(Constants.ZERO_NUM);
                List<ChannelIndDTO> channels = device.getChannels();
                for (ChannelIndDTO channelInfo : channels) {
                    if (StringUtils.isNotBlank(channelInfo.getChannelName()) && channelInfo.getChannelName().endsWith("_0") && channelInfo.getFacade() != null && channelInfo.getFacade() == 1) {
                        deviceInfo.setChannelCode(channelInfo.getChannelCode());
                        break;
                    }
                }
                if (StringUtils.isBlank(deviceInfoReq.getChannelCode())
                        && !CollectionUtils.isEmpty(channels)
                        && StringUtils.isEmpty(deviceInfo.getChannelCode())) {
                    if(channels.size() > 1){
                        ChannelIndDTO channelInfoDO =channels.get(0);
                        if(channelInfoDO.getFacade() != null && channelInfoDO.getFacade() == 1){
                            deviceInfo.setChannelCode(channelInfoDO.getChannelCode());
                        }else{
                            deviceInfo.setChannelCode(channels.get(1).getChannelCode());
                        }
                    }else{
                        deviceInfo.setChannelCode(channels.get(0).getChannelCode());
                    }
                }

                if (StringUtils.isNotBlank(deviceInfoReq.getChannelCode()) && !CollectionUtils.isEmpty(channels)) {
                    boolean booChan = channels.stream().filter(algo ->
                            deviceInfoReq.getChannelCode().equals(algo.getChannelCode())
                    ).findAny().isPresent();
                    if(booChan){
                        deviceInfo.setChannelCode(deviceInfoReq.getChannelCode());
                    }else{
                        return Result.fail("通道编码与站址编码/设备编码不匹配！");
                    }
                }
            }
            //新修改的组装逻辑
            deviceThreadDTO.setQueryType(deviceInfoReq.getQueryType());
            deviceThreadDTO.setDeviceModelDTO(deviceModel);
            deviceThreadDTO.setDeviceIndReqDTO(deviceInfo);
            allDeviceInfoList.add(deviceThreadDTO);
        }
        List<DeviceModelDTO> deviceInfoList = ThreadParallelUtil.run(allDeviceInfoList, it -> {
            try {
                return calDeviceVisible(it.getDeviceIndReqDTO(), it.getDeviceModelDTO(), it.getQueryType());
            } catch (Exception e) {
                log.error("calDeviceVisible orTimeout deviceCode{}", it.getDeviceIndReqDTO().getDeviceCode(), e);
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.VIDEO_REQUEST_JOB);
        deviceInfoList = deviceInfoList.stream()
                .filter(Objects::nonNull) // 过滤掉null元素
                .collect(Collectors.toList());
        return Result.data(deviceInfoList);
    }

    @ApiOperation(value = "查询摄像机标定信息", notes = "查询摄像机标定信息")
    @PostMapping("/getDeviceCalibration")
    //@IndustryAuth
    public Result<DeviceModelDTO> getDeviceCalibration(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        DeviceModelDTO result = deviceInfoRepository.getDeviceCalibration(deviceInfoReq);
        if (result == null || result.getPan() == null || result.getTilt() == null) {
            return Result.fail(HttpStatus.HTTP_NOT_FOUND, "未找到摄像机标定信息");
        }
        return Result.data(result);
    }


    /**
     * 查询传感器设备信息
     */
    @ApiOperation("查询传感器设备信息")
    @PostMapping(value = "/pageSensorDevice")
    //@IndustryAuth
    public TableDataInfo pageSensorDevice(@Valid @RequestBody PageDeviceVO pageDeviceVO) {
        IotDeviceVO qryCond = new IotDeviceVO();
        BeanUtils.copyProperties(pageDeviceVO, qryCond);
        List<IotDeviceVO> list = iotDeviceRepository.queryIotDeviceListSimple(qryCond);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo<>(list)).getTotal());
        return rspData;
    }

    /**
     * 查询物联设备信息列表
     */
    @ApiOperation("查询物联设备信息")
    @PostMapping(value = "/pageSensorDeviceInfo")
    public TableDataInfo pageSensorDeviceInfo(@Valid @RequestBody PageSensorDeviceVO param) {
        log.info("IndustryDeviceController pageSensorDeviceInfo param: {}", JSON.toJSONString(param));
        IotDeviceVO qryCond = new IotDeviceVO();
        BeanUtils.copyProperties(param, qryCond);
        log.info("IndustryDeviceController pageSensorDeviceInfo qryCond: {}", JSON.toJSONString(qryCond));
        if (qryCond.getTenantId() == null || qryCond.getTenantId().equals("")){
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            qryCond.setTenantId(tenantId);
        }
        List<IotDeviceVO> list = iotDeviceRepository.querySensorList(qryCond);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo<>(list)).getTotal());
        return rspData;
    }

    @ApiOperation("根据设备类型查询物联设备信息")
    @PostMapping("/pageIotDeviceByDeviceType")
    public TableDataInfo pageIotDeviceByDeviceType(@Valid @RequestBody PageIotDeviceDTO dto){
        log.info("IndustryDeviceController pageIotDeviceByDeviceType param: {}", JSON.toJSONString(dto));
        if (null == dto.getTenantId()  || "".equals(dto.getTenantId())){
            dto.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        }
        if (null == dto.getIndustryCode() || "".equals(dto.getIndustryCode())){
            dto.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        }
        List<PageIotDeviceVO> list = iotDeviceRepository.queryIotDeviceInfo(dto);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo<>(list)).getTotal());
        return rspData;
    }

    @Deprecated
    @ApiOperation("根据设备类型查询物联设备所对应的告警类型编码")
    @PostMapping("/pageAlarmTypeCodeByDeviceType")
    public TableDataInfo pageAlarmTypeCodeByDeviceType(@Valid @RequestBody PageIotDeviceDTO dto){
        log.info("IndustryDeviceController pageAlarmTypeCodeByDeviceType param: {}", JSON.toJSONString(dto));
        if (null == dto.getTenantId()  || "".equals(dto.getTenantId())){
            dto.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        }
        if (null == dto.getIndustryCode() || "".equals(dto.getIndustryCode())){
            dto.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        }
        List<PageAlarmTypeVO> list = iotDeviceRepository.queryAlarmType(dto);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo<>(list)).getTotal());
        return rspData;
    }

    /**
     * 根据租户ID查询雷达设备信息
     */
    @ApiOperation("根据租户ID查询雷达设备信息")
    @PostMapping(value = "/pageRadarDevice")
    public TableDataInfo pageRadarDevice(@Valid @RequestBody PageRadarDeviceVO pageRadarDeviceVO) {
        IotDeviceVO qryCond = new IotDeviceVO();
        BeanUtils.copyProperties(pageRadarDeviceVO, qryCond);
        if (qryCond.getTenantId() == null || qryCond.getTenantId().equals("")){
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            qryCond.setTenantId(tenantId);
        }
        List<IotDeviceVO> list = iotDeviceRepository.queryRadarList(qryCond);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo<>(list)).getTotal());
        return rspData;
    }

    /**
     * 查询雷达设备树
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @ApiOperation("查询雷达设备树")
    @PostMapping(value = "/getRadarDeviceTree")
    public Result<List<DeviceTreeVO>> getRadarDeviceTree(@RequestBody IotDeviceVO iotDeviceVO){
        try {
            iotDeviceVO.setCategoryCode(1);
            iotDeviceVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            List<DeviceTreeVO> treeList = iotDeviceRepository.getIotOrRadarDeviceTree(iotDeviceVO);
            List<DeviceTreeVO> list = buildIotMonitorTree(treeList, Boolean.TRUE);
            List<DeviceTreeVO> rootTree = new ArrayList<>(1);
            if (!treeList.isEmpty()) {
                // 补充中国根节点
                DeviceTreeVO root = new DeviceTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOnlineNum(list.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                root.setNum(list.stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常省份
                list = list.stream().filter(dto -> StringUtils.isNotEmpty(dto.getCode())).collect(Collectors.toList());
                root.setList(list);
                rootTree.add(root);
            }
            return Result.data(rootTree);
        }catch (Exception e){
            log.error("查询雷达设备行政区域树形失败, e:", e);
            return Result.fail("查询雷达设备行政区域树形失败");
        }
    }

    /**
     * 根据客户信息获取物联设备数量
     * @return
     */
    @ApiOperation("根据客户信息获取物联设备数量")
    @PostMapping(value = "/getSensorDeviceCount")
    public Result<SensorDeviceCountVO> getSensorDeviceCount(@RequestBody SensorDeviceCountReqDTO request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            throw new CustomException("获取用户信息失败", HttpStatus.HTTP_BAD_REQUEST);
        }
        if (request == null) {
            throw new CustomException("请求参数不能为空", HttpStatus.HTTP_BAD_REQUEST);
        }
        try {
            if (StringUtils.isEmpty(request.getTenantId())) {
                request.setTenantId(loginUser.getUser().getTenantId());
            }
            if (StringUtils.isEmpty(request.getIndustryCode())) {
                request.setIndustryCode(loginUser.getUser().getIndustryCode());
            }
            SensorDeviceCountVO result = iotDeviceRepository.getSensorDeviceCount(request);
            return Result.data(result);
        } catch (Exception e){
            log.error("查询物联设备数量失败, e:", e);
            return Result.fail("查询物联设备数量失败");
        }
    }

    /**
     * 查询物联设备树
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @ApiOperation("查询物联设备树")
    @PostMapping(value = "/getIotDeviceTree")
    public Result<List<DeviceTreeVO>> getIotDeviceTree(@RequestBody IotDeviceVO iotDeviceVO){
        try {
            iotDeviceVO.setCategoryCode(2);
            iotDeviceVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            iotDeviceVO.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
            List<DeviceTreeVO> treeList = iotDeviceRepository.getIotOrRadarDeviceTree(iotDeviceVO);
            List<DeviceTreeVO> list = buildIotMonitorTree(treeList, Boolean.TRUE);
            List<DeviceTreeVO> rootTree = new ArrayList<>(1);
            if (!treeList.isEmpty()) {
                // 补充中国根节点
                DeviceTreeVO root = new DeviceTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOnlineNum(list.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                root.setNum(list.stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常省份
                list = list.stream().filter(dto -> StringUtils.isNotEmpty(dto.getCode())).collect(Collectors.toList());
                root.setList(list);
                rootTree.add(root);
            }
            return Result.data(rootTree);
        }catch (Exception e){
            log.error("查询物联设备行政区域树形失败, e:", e);
            return Result.fail("查询物联设备行政区域树形失败");
        }
    }

    private List<DeviceTreeVO> buildIotMonitorTree(List<DeviceTreeVO> treeList, boolean backDevFlag) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.getList().forEach(townCode -> {
                            townCode.setOnlineNum((int) townCode.getList().stream().filter(townCodeVo -> Objects.equals(Constants.ZERO, townCodeVo.getStatus())).count());
                            townCode.setNum(townCode.getList().size());
                            if (!backDevFlag) {
                                townCode.getList().clear();
                            }
                        });
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        if (!backDevFlag) {
                            //去掉异常乡镇
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode()) && com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getName())).collect(Collectors.toList()));
                        } else {
                            //将有异常的乡镇数据移动到区县目录下
                            List<DeviceTreeVO> list = country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isEmpty(townDto.getCode())).collect(Collectors.toList());
                            list.forEach(deviceTreeVO -> {
                                country.getList().addAll(deviceTreeVO.getList());
                            });
                            country.setList(country.getList().stream().filter(townDto ->
                                    com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townDto.getCode())).collect(Collectors.toList()));
                        }
                    });

                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            com.chinatower.platform.common.utils.StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(prioDto ->
                        com.chinatower.platform.common.utils.StringUtils.isNotEmpty(prioDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
            }
        });
        return treeList;
    }

    @ApiOperation("查询行业传感器设备信息")
    @PostMapping(value = "/pageIndSensorDevice")
    public TableDataInfo pageIndSensorDevice(@RequestBody PageDeviceVO pageDeviceVO) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        if (StringUtils.isBlank(pageDeviceVO.getAppVerCode())) {
            rspData.setRows(null);
        }
        IotDeviceVO qryCond = new IotDeviceVO();
        BeanUtils.copyProperties(pageDeviceVO, qryCond);
        List<IotDeviceVO> list = iotDeviceRepository.queryIndIotDeviceListSimple(qryCond);
        rspData.setRows(list);
        rspData.setTotal((new PageInfo(list)).getTotal());
        return rspData;
    }

    /**
     * 查询行业物联因子及告警信息
     */
    @Deprecated
    @ApiOperation("查询行业物联因子及告警信息")
    @PostMapping(value = "/pageIotMonitorInfo")
    public Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(@RequestBody PageIotMonitorDTO param) {
        return iotDeviceRepository.pageIotMonitorInfo(param);
    }

    /**
     * 获取雷达对应的摄像机列表
     */
    @ApiOperation(value = "获取雷达对应的摄像机列表", notes = "获取雷达对应的摄像机列表")
    @PostMapping(value = "/getRadarDeviceConfigInfo")
    public Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(@RequestBody RadarDeviceReqDTO radarDeviceReqDTO) {
        try {
            return iotDeviceRepository.getRadarDeviceConfigInfo(radarDeviceReqDTO);
        } catch (Exception e) {
            log.error("IndustryDeviceController getRadarDeviceConfigInfo error, e:", e);
            return Result.fail("获取失败");
        }
    }

    @ApiOperation(value = "根据组织机构id查询物联设备", notes = "根据组织机构id查询物联设备")
    @PostMapping(value = "/getIotDeviceByDept")
    public Result<List<IotDeviceEntity>> getIotDeviceByDept(@RequestBody IotDeviceEntity iotDeviceEntity) {
        iotDeviceEntity.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        iotDeviceEntity.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return iotDeviceRepository.getIotDeviceByDept(iotDeviceEntity);
    }

    @ApiOperation(value = "根据地域或传感器类型查询传感器设备列表（以类型分组返回）", notes = "根据地域或传感器类型查询传感器设备列表（以类型分组返回）")
    @PostMapping(value = "/getIotGroupByDeviceType")
    public Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(@RequestBody IotDeviceAreaEntity iotDeviceAreaEntity) {
        iotDeviceAreaEntity.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        iotDeviceAreaEntity.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        return iotDeviceRepository.getIotGroupByDeviceType(iotDeviceAreaEntity);
    }

    /**
     * 功能描述 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     *
     * @param deviceLayerReq
     * @return IndustryDeviceTree
     * @author wanglei
     * @date 2022/12/15 10:33
     * @version: release_v1.5
     */
    @ApiOperation(value = "查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序")
    @PostMapping("/queryDevice3km")
    public Result<List<IndustryDeviceTree>> queryDevice3km(@RequestBody DeviceLayerReq deviceLayerReq) {
        List<IndustryDeviceTree> device = deviceInfoRepository.queryDevice3km(deviceLayerReq);
        return Result.data(device);
    }

    /**
     * 根据关键字查询设备的基本信息接口
     * 入参：搜索的关键信息（keyword（摄像机名称、物联设备名称））、租户id（tenantId）
     *
     * @param deviceInfoVO name
     * @return IndustryDeviceTree
     * @author yiyao
     * @date 2023/03/16
     * @version: release_v1.5.80
     */
    @ApiOperation(value = "根据关键字查询设备的基本信息接口")
    @PostMapping("/queryDeviceList")
    public TableDataInfo queryDeviceList(@RequestBody DeviceInfoVO deviceInfoVO) {
        List<DeviceAndIotDeviceDTO> list = iIndustryDeviceService.queryDeviceList(deviceInfoVO);
        return new TableDataInfo(list, (int)(new PageInfo(list)).getTotal());
    }


    /**
     * 根据设备编码和类型，获取设备详细信息
     * 入参：设备编码(deviceCode)、设备类型（deviceType）、租户id（tenantId）
     *
     * @param deviceInfoVO name
     * @return IndustryDeviceTree/
     * @author yiyao
     * @date 2023/03/16
     * @version: release_v1.5.80
     */
    @ApiOperation(value = "根据设备编码和类型，获取设备详细信息")
    @PostMapping("/queryDeviceDetail")
    public Result queryDeviceDetail(@RequestBody @Valid DeviceQueryVO deviceInfoVO) {
        Object object = iIndustryDeviceService.queryDeviceDetail(deviceInfoVO);
        return Result.data(object);
    }


    /**
     * 根据设备编码，获取附近指定范围的所有设备"
     * 入参：设备编码(deviceCode)、设备类型（deviceType）、范围（distance）
     *
     * @param deviceQueryVO deviceCode  deviceType distance
     * @return DeviceByDistanceDTO
     * @author yiyao
     * @date 2023/03/20
     * @version: release_v1.5.80
     */
    @ApiOperation(value = "根据设备编码，获取附近指定范围的所有设备")
    @PostMapping("/queryDeviceByDistance")
    public Result<List<DeviceByDistanceDTO>> queryDeviceByDistance(@RequestBody @Valid DeviceQueryVO deviceQueryVO) {
        List<DeviceByDistanceDTO> list = iIndustryDeviceService.queryDeviceByDistance(deviceQueryVO);
        return null == list ? Result.fail("获取设备失败，请确认查询信息") : Result.data(list);
    }

    /**
     * 根据摄像机编码，获取附近指定范围的摄像机，每个站址取一个设备
     * @param param 请求参数
     * @return 摄像机信息
     */
    @ApiOperation(value = "根据摄像机编码，获取附近指定范围的摄像机，每个站址取一个设备")
    @PostMapping("/queryDeviceInfoByDistance")
    public Result<List<DeviceInfoByDistanceDTO>> queryDeviceInfoByDistance(@RequestBody DeviceInfoQueryVO param) {
        return Result.data(iIndustryDeviceService.queryDeviceInfoByDistance(param));
    }

    /**
     * 需求：新增接口，按照摄像机的标签编码list或者组织机构编码list或者算法list或者摄像机类型list 返回对应的摄像机信息（四者可以组合）
     * 入参：行业应用版本编码、租户编码，用户ID、摄像机标签编码list、算法list、组织机构编码
     *
     * @param devicesByConditionVO devicesByConditionVO
     * @return DeviceModelDTO
     * @author yiyao
     */
    @ApiOperation(value = "根据摄像机对应的标签，部门编码、算法，展示该标签下或者组织机构下（含本组织机构下所有的子节点）或者算法下或者摄像机类型下，所对应的的摄像机信息。")
    @PostMapping("/queryDevicesByCondition")
    public TableDataInfo queryDevicesByCondition(@RequestBody DevicesByConditionVO devicesByConditionVO) {
        List<DeviceByConditionDTO> list = iIndustryDeviceService.queryDevicesByCondition(devicesByConditionVO);
        return new TableDataInfo(list, (int)(new PageInfo(list)).getTotal());
    }

    /**
     * 新增摄像机查询总数接口，根据租户ID或者组织列表，查询该租户下或者所属组织列表下，对应组织的摄像机总数
     * 出参：对应租户下或者组织机构下（含本组织机构）的摄像机总数。
     * 如果是按照租户查，出参格式：
     * "totalCnt":摄像机总数
     * “onlineCnt”:在线摄像机数
     *
     * 如果是按照组织列表查询，查询所传组织机构下（只查本组织机构的摄像机，不含其子节点的摄像机）的摄像机，出参格式：{
     *     "totalCnt":0,   // 摄像机总数
     *     "onlineCnt":0, //摄像机总在线数
     *      "deviceList":[
     * "组织机构编码":[{
     *              "deviceCode":xxxxx, //摄像机编码
     *               "isOnline":1        //是否在线状态
     *         }]]
     * }
     * @param devicesStatisticsReqVO
     * @return
     */
    @ApiOperation(value = "新增摄像机查询总数接口，根据租户ID或者组织列表，查询该租户下或者所属组织列表下，对应组织的摄像机总数")
    @PostMapping("/queryDevicesStatistics")
    public Result<DevicesStatisticsResVO> queryDevicesStatistics(@RequestBody DevicesStatisticsReqVO devicesStatisticsReqVO) {
        return iIndustryDeviceService.queryDevicesStatistics(devicesStatisticsReqVO);
    }

    /**
     * 雷达联动接力/取消接力接口
     */
    @ApiOperation(value = "雷达联动接力/取消接力接口")
    @PostMapping("/radarTargetFollowRelay")
    public Result<RadarTargetFollowResultVO> radarTargetFollowRelay(@RequestBody RadarTargetFollowRelayVO param) {
        try {
            Result<RadarTargetFollowResultVO> result = Result.data(iIndustryDeviceService.radarTargetFollowRelay(param));
            log.info("IndustryDeviceController radarTargetFollowRelay param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("IndustryDeviceController radarTargetFollowRelay error:", e);
            return Result.fail(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }

    /**
     * 雷达联动同时随动/取消同时随动接口
     */
    @ApiOperation(value = "雷达联动同时随动/取消同时随动接口")
    @PostMapping("/radarTargetFollowLookTo")
    public Result<RadarTargetFollowResultVO> radarTargetFollowLookTo(@RequestBody RadarTargetFollowLookToVO param) {
        try {
            Result<RadarTargetFollowResultVO> result = Result.data(iIndustryDeviceService.radarTargetFollowLookTo(param));
            log.info("IndustryDeviceController radarTargetFollowLookTo param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("IndustryDeviceController radarTargetFollowLookTo error:", e);
            return Result.fail(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }


    /**
     * 获取雷达目标设备列表接口
     */
    @ApiOperation(value = "获取目标设备列表接口")
    @PostMapping("/queryRadarTargetList")
    public Result<List<RadarDeviceStatusVO>> queryRadarTargetList(@RequestBody RadarTargetDTO radarTargetDTO){
        return iIndustryDeviceService.queryRadarTargetList(radarTargetDTO);
    }

    /**
     * 根据站址里列表查询摄像机可视域-全域感知大屏
     */
    @ApiOperation(value = "根据站址里列表查询摄像机可视域-全域感知大屏", notes = "根据站址里列表查询摄像机可视域--全域感知大屏")
    @PostMapping("/getDeviceVisibleBySiteNew")
    //@IndustryAuth
    @Deprecated
    public Result<List<DeviceModelDTO>> getDeviceVisibleBySiteNew(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        log.info("废弃接口调用");
        log.info("getDeviceVisibleBySiteNew入参" + JSONObject.toJSONString(deviceInfoReq));
        if (CollectionUtils.isEmpty(deviceInfoReq.getSiteCodes()) && CollectionUtils.isEmpty(deviceInfoReq.getDeviceCodes())) {
            return Result.fail("站址编码/设备编码不能为空");
        }
        List<DeviceIndDTO> devices = deviceInfoRepository.queryDevicesBySite(deviceInfoReq);
        if (devices.isEmpty()) {
            return Result.data(null);
        }
        deviceInfoReq.setDeviceCodes(devices.stream().map(DeviceIndDTO::getDeviceCode).collect(Collectors.toList()));
        Map<String, List<DeviceIndDTO>> deviceMap = devices.stream().collect(Collectors.groupingBy(DeviceIndDTO::getDeviceCode));
        List<DeviceModelDTO> deviceModes = deviceInfoRepository.getDeviceModel(deviceInfoReq);
        List<CompletableFuture<DeviceModelDTO>> allFutureList = new ArrayList<>(deviceModes.size());
        for (DeviceModelDTO deviceModel : deviceModes) {
            DeviceIndReqDTO deviceInfo = new DeviceIndReqDTO();
            deviceInfo.setTenantId(deviceInfoReq.getTenantId());
            deviceInfo.setDeviceCode(deviceModel.getDeviceCode());

            List<DeviceIndDTO> deviceIndDTOS = deviceMap.get(deviceModel.getDeviceCode());
            if(!CollectionUtils.isEmpty(deviceIndDTOS)){
                DeviceIndDTO device = deviceIndDTOS.get(Constants.ZERO_NUM);
                for (ChannelIndDTO channelInfo : device.getChannels()) {
                    if (StringUtils.isNotBlank(channelInfo.getChannelName()) && channelInfo.getChannelName().endsWith("_0")) {
                        deviceInfo.setChannelCode(channelInfo.getChannelCode());
                        break;
                    }
                }
                if (StringUtils.isBlank(deviceInfoReq.getChannelCode())
                        && !CollectionUtils.isEmpty(device.getChannels())
                        && StringUtils.isEmpty(deviceInfo.getChannelCode())) {
                    deviceInfo.setChannelCode(device.getChannels().get(0).getChannelCode());
                }

                if (StringUtils.isNotBlank(deviceInfoReq.getChannelCode()) && !CollectionUtils.isEmpty(device.getChannels())) {
                    boolean booChan = device.getChannels().stream().filter(algo ->
                            deviceInfoReq.getChannelCode().equals(algo.getChannelCode())
                    ).findAny().isPresent();
                    if(booChan){
                        deviceInfo.setChannelCode(deviceInfoReq.getChannelCode());
                    }else{
                        return Result.fail("通道编码与站址编码/设备编码不匹配！");
                    }
                }
            }

            allFutureList.add(CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> calDeviceVisibleNew(deviceInfo, deviceModel), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error("calDeviceVisible error", e);
                                return null;
                            }), 5, TimeUnit.SECONDS).exceptionally(e -> {
                log.error("calDeviceVisible orTimeout deviceCode{}",deviceInfo.getDeviceCode(), e);
                return null;
            }));
        }
        CompletableFuture<Void> doneFuture = CompletableFuture.allOf(allFutureList.toArray(new CompletableFuture[allFutureList.size()]));
        return Result.data(doneFuture.thenApply(v ->
                allFutureList.stream()
                        .map(CompletableFuture::join).filter(Objects::nonNull)
                        .collect(Collectors.toList())).join());
    }

    private DeviceModelDTO calDeviceVisibleNew(DeviceIndReqDTO deviceInfoReq, DeviceModelDTO deviceModel) {
        long time = new Date().getTime();
        DeviceModelDTO result = new DeviceModelDTO();
        result.setDeviceCode(deviceModel.getDeviceCode());
        result.setDeviceName(deviceModel.getDeviceName());
        result.setVisualRange(deviceModel.getVisualRange());
        if (StringUtils.isBlank(deviceModel.getMinFocus()) || deviceModel.getRefPoints() == null || deviceModel.getRefPoints().isEmpty()) {
            log.error(IndErrorMsgConstants.DEVICE_VISUAL_NOT_FOUND, HttpStatus.HTTP_NOT_FOUND);
            return result;
        }

        // 调用接口获取ptz
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setServiceName(DeviceNorthIntfConstants.QUERY_PTZ_POSITION);
        apiInfoDTO.setDeviceCode(deviceInfoReq.getDeviceCode());
        apiInfoDTO.setChannelCode(deviceInfoReq.getChannelCode());
        apiInfoDTO.setTenantId(deviceInfoReq.getTenantId());
        log.info("start call {}",DeviceNorthIntfConstants.QUERY_PTZ_POSITION);
        Result<JSONObject> ptzResult = null;
        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_PTZ_POSITION);
            // 接口路径参数
            path.setDeviceCode(deviceInfoReq.getDeviceCode());
            path.setChannelCode(deviceInfoReq.getChannelCode());
            paramter.setPath(path);
            // 属地化数据统一改造 add by wangyt 20240129
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(deviceInfoReq.getDeviceCode());
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            ptzResult = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.CAL_DEVICE_VISIBLE_ERROR,e);
        }

        String transPan = null;
        String transTilt = null;
        String transZoom = null;
        if (ResultCode.SUCCESS.getCode() == ptzResult.getCode()) {
            // ptz 转换
            DeviceModelVO model = new DeviceModelVO();
            BeanUtils.copyProperties(deviceModel, model);
            JSONObject ptzData = ptzResult.getData().getJSONObject(DeviceNorthIntfConstants.DATA);
            transPan = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_PAN_STR), model, LongLatAngleUtils.TYPE_P);
            transTilt = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_TILT_STR), model, LongLatAngleUtils.TYPE_T);
            transZoom = LongLatAngleUtils.paramTrans(ptzData.getString(DeviceConstants.PTZ_ZOOM_STR), model, LongLatAngleUtils.TYPE_Z);
            //返回原始ptz值，方便核查问题
            deviceModel.setOriginalPan(ptzData.getString(DeviceConstants.PTZ_PAN_STR));
            deviceModel.setOriginalTilt(ptzData.getString(DeviceConstants.PTZ_TILT_STR));
            deviceModel.setOriginalZoom(ptzData.getString(DeviceConstants.PTZ_ZOOM_STR));
        } else {
            log.error(ptzResult.getMsg(), ptzResult.getCode());
            return result;
        }
        log.info("trans p:{}t:{}z:{}", transPan, transTilt, transZoom);
        // 获取p0 t0
        DeviceModelVO deviceModelVO = new DeviceModelVO();
        deviceModelVO.setRealPan(transPan);
        List<SysDictData> enumListNew = new ArrayList<>();
        deviceModelVO = LongLatAngleUtils.getValidOffset(deviceModel.getRefPoints(), deviceModelVO,enumListNew);
        deviceModel.setPan(deviceModelVO.getP0());
        deviceModel.setTilt(deviceModelVO.getT0());
        BigDecimal zoom = new BigDecimal(transZoom);
        // z为0判断，避免计算报错
        if (BigDecimal.ZERO.compareTo(zoom) == 0) {
            log.error(IndErrorMsgConstants.DEVICE_ZOOM_ZERO, HttpStatus.HTTP_INTERNAL_ERROR);
            return result;
        }
        try {
            // f= f0*z'
            BigDecimal realFocal = new BigDecimal(deviceModel.getMinFocus()).multiply(zoom).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigDecimalTwo = new BigDecimal(2);
            BigDecimal doubleRealFocal = bigDecimalTwo.multiply(realFocal);
            // arctan(c/2f)
            double c2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorLength()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(c/2f)
            deviceModel.setHorizRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(c2f)).toPlainString());
            // arctan(d/2f)
            double d2f = Math.toDegrees(Math.atan(new BigDecimal(deviceModel.getPicSensorWidth()).divide(doubleRealFocal, 2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            // 2*arctan(d/2f)
            deviceModel.setVertiRange(bigDecimalTwo.multiply(NumberUtils.toScaledBigDecimal(d2f)).toPlainString());
            // p'-p0'
            BigDecimal pTemp = new BigDecimal(transPan).subtract(deviceModel.getPan());
            if(pTemp.compareTo(BigDecimal.ZERO) < 0){
                pTemp = pTemp.add(new BigDecimal(360));
            }
            log.info("换算P值：{}",pTemp);
            deviceModel.setHorizViewRange(pTemp.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            // t'-t0'
            deviceModel.setVertiViewRange(new BigDecimal(transTilt).subtract(deviceModel.getTilt()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } catch (Exception e) {
            log.error("calDevice", e);
            log.error(IndErrorMsgConstants.DEVICE_CAL_VISIBLE_ERROR, HttpStatus.HTTP_INTERNAL_ERROR);
            return result;
        }
        log.info(deviceInfoReq.getDeviceCode()+"calDeviceVisible耗时" + (new Date().getTime() - time) +"ms");
        return deviceModel;
    }

    @ApiOperation(value = "查询摄像机水平角俯仰角", notes = "查询摄像机水平角俯仰角")
    @PostMapping("/getViewPitchAngle")
    Result<JSONObject> getViewPitchAngle(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        Result<JSONObject> result = null;
        if(StringUtils.isEmpty(deviceInfoReq.getDeviceCode()) ||  StringUtils.isEmpty(deviceInfoReq.getChannelCode())){
            return Result.fail("摄像机编码和通道编码不能为空");
        }
        ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
        apiInfoDTO.setDeviceCode(deviceInfoReq.getDeviceCode());
        apiInfoDTO.setChannelCode(deviceInfoReq.getChannelCode());
        try {
            NetConnectUtils.RequestParamter<ApiInfoDTO> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(apiInfoDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_VIEW_PITCHANGLE);
            // 接口路径参数
            path.setDeviceCode(deviceInfoReq.getDeviceCode());
            path.setChannelCode(deviceInfoReq.getChannelCode());
            paramter.setPath(path);
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(deviceInfoReq.getDeviceCode());
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.CAL_DEVICE_VISIBLE_OP + VideoConstants.REQUEST_LOG + paramter);
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VIEW_PITCHANGLE + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            result = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VIEW_PITCHANGLE, e);
        }
        JSONObject resultJson = new JSONObject();
        if (ResultCode.SUCCESS.getCode() == result.getCode()) {
            JSONObject json = result.getData().getJSONObject(DeviceNorthIntfConstants.DATA);
            resultJson.put(PITCH_ANGLE,json.get(PITCH_ANGLE) == null ? "": json.get(PITCH_ANGLE) );
            resultJson.put(VIEW_XANGLE,json.get(VIEW_XANGLE) == null ? "": json.get(VIEW_XANGLE));
            resultJson.put(VIEW_YANGLE,json.get(VIEW_YANGLE) == null ? "": json.get(VIEW_YANGLE));
        } else {
            log.error(result.getMsg(), result.getCode());
            return result;
        }
        return Result.data(resultJson);
    }
    @PostMapping("/getViewPitchAngleTest")
    Result<JSONObject> getViewPitchAngleTest(@RequestBody DeviceIndReqDTO deviceInfoReq) {
        Result<JSONObject> result1= iIndustryDeviceServiceMigrate.getViewPitchAngle(deviceInfoReq);
        // 俯仰角
        String pitchAngle = "";
        // 水平视场角
        String viewXAngle = "";
        // 垂直视场角
        String viewYAngle = "";
        if(ResultCode.SUCCESS.getCode() == result1.getCode()){
            JSONObject json = result1.getData();
            pitchAngle = json.get(PITCH_ANGLE) == null ? "": json.get(PITCH_ANGLE).toString();
            viewXAngle = json.get(VIEW_XANGLE) == null ? "": json.get(VIEW_XANGLE).toString();
            viewYAngle = json.get(VIEW_YANGLE) == null ? "": json.get(VIEW_YANGLE).toString();
        }
        return Result.data(new JSONObject().fluentPut(PITCH_ANGLE, pitchAngle).fluentPut(VIEW_XANGLE, viewXAngle).fluentPut(VIEW_YANGLE, viewYAngle));
    }

    @ApiOperation(value = "查询客户订购设备数量")
    @GetMapping("/queryIndustryDeviceStatistics")
    public Result<IndustryDeviceStatisticsVO> queryIndustryDeviceStatistics() {
        return Result.data(iIndustryDeviceService.queryIndustryDeviceStatistics());
    }

    /**
     * 分页查询摄像机图层
     * @param deviceLayerReq
     * @return
     */
    @ApiOperation(value = "分页查询摄像机图层")
    @PostMapping("/pageDeviceLayer")
    public Result<PageInfo<IndustryDeviceTree>> pageDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq) {
        PageInfo<IndustryDeviceTree> pageInfo = deviceInfoRepository.pageDeviceLayer(deviceLayerReq);
        Set<String> fileUrls = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(device -> {
                device.getList().forEach(dev -> {
                    fileUrls.add(dev.getImageUrl());
                });
            });
            //加密
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
            pageInfo.getList().forEach(device -> {
                device.getList().forEach(dev -> {
                    if (!StringUtils.isEmpty(dev.getImageUrl())) {
                        dev.setImageUrl(result.get(dev.getImageUrl()));
                    }
                });
            });
        }
        return Result.data(pageInfo);
    }
}
