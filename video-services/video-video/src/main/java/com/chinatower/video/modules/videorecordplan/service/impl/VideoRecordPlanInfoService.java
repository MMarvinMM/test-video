package com.chinatower.video.modules.videorecordplan.service.impl;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videorecordplan.dto.ChannelVideoPlanDTO;
import com.chinatower.video.modules.videorecordplan.dto.VideoPlanDateVO;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordMergeAsyncService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.dto.VideoOrderResp;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.order.api.IVideoOrderService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.order.VideoOrderReq;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.common.util.TimeMer;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videorecordplan.dataobject.ChannelVideoPlanDO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;
import com.chinatower.video.modules.videorecordplan.dto.TimeBucket;
import com.chinatower.video.modules.videorecordplan.dto.TimeNode;
import com.chinatower.video.modules.videorecordplan.mapper.ChannelVideoPlanMapper;
import com.chinatower.video.modules.videorecordplan.repository.IChannelVideoPlanRepository;
import com.chinatower.video.modules.videorecordplan.repository.IPlanTimeSectionRepository;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import static com.chinatower.video.modules.common.util.TimeMer.*;

/**
 * @ClassName VideoRecordPlanService
 * @Description 视频通道录像计划服务类
 * @Author chenmf
 * @Date 2022/1/18
 */
@Service
@Slf4j
public class VideoRecordPlanInfoService implements IVideoRecordPlanInfoService {

    private static final String NOT_EXIST_ORDER_INFO = "通道录像计划对应的订单信息不存在!";

    private static final String DATE_FORMAT = "2020-01-01 ";

    @Resource
    private IVideoOrderService videoOrderService;

    @Resource
    private IChannelVideoPlanRepository channelVideoPlanRepository;

    @Resource
    private IPlanTimeSectionRepository planTimeSectionRepository;

    @Autowired
    private ApiInfoUtils apiInfoUtils;

    @Resource
    private ChannelVideoPlanMapper channelVideoPlanMapper;

    @Autowired
    VideoQueryDAO videoQueryDAO;

    @Resource
    private VideoControlService videoControlService;

    @Resource
    private IVideoRecordMergeAsyncService videoRecordMergeAsyncService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private NetConnectUtils connectUtils;

    @Resource
    private ICasCadeQueryService iCasCadeQueryService;

    /**
     * 新增通道录像计划
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {

        int success = 0;
        int fail = 0;
        log.info("保存通道录像计划接口请求报文------" + JSON.toJSONString(channelVideoPlanRequestVO));

        List<ChannelVideoPlanVO> recordPlanList = channelVideoPlanRequestVO.getRecordPlanList();
        if (!CollectionUtils.isEmpty(recordPlanList)) {
            for (int i = 0; i < recordPlanList.size(); i++) {

                ChannelVideoPlanVO channelVideoPlanVO = recordPlanList.get(i);

                VideoOrderReq videoOrderReq = new VideoOrderReq();
                videoOrderReq.setTenantId(channelVideoPlanVO.getTenantId());
                videoOrderReq.setChannelCode(channelVideoPlanVO.getChannelCode());
                videoOrderReq.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                videoOrderReq.setIndustryCode(channelVideoPlanRequestVO.getIndustryCode());

                Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);
                ChannelVideoPlanDO channelVideoPlanDO = new ChannelVideoPlanDO();

                if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                        && !StringUtils.isEmpty(resp.getData().getOrderCode())
                        && !StringUtils.isEmpty(resp.getData().getPackageCode())) {
                    String orderCode = resp.getData().getOrderCode();
                    String packageCode = resp.getData().getPackageCode();
                    channelVideoPlanDO.setOrderCode(orderCode);
                    channelVideoPlanDO.setPackageCode(packageCode);
                    channelVideoPlanVO.setOrderCode(orderCode);
                }
                else
                {
                    fail++;
                    log.error(NOT_EXIST_ORDER_INFO);
                    throw new CustomException(NOT_EXIST_ORDER_INFO);
                }

                ChannelVideoPlanVO tempChannelVideoPlanVO = new ChannelVideoPlanVO();
                tempChannelVideoPlanVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                tempChannelVideoPlanVO.setChannelCode(channelVideoPlanVO.getChannelCode());
                tempChannelVideoPlanVO.setTenantId(channelVideoPlanVO.getTenantId());
                tempChannelVideoPlanVO.setIndustryCode(channelVideoPlanRequestVO.getIndustryCode());
                List<ChannelVideoPlanDO> channelVideoPlanVOList = channelVideoPlanRepository.queryVideoPlanInfo(tempChannelVideoPlanVO);
                //修改
                if (channelVideoPlanVOList.size()> 0) {
                    Long recordPlanId = channelVideoPlanVOList.get(0).getRecordPlanId();
                    Long id = channelVideoPlanVOList.get(0).getId();
                    channelVideoPlanVO.setRecordPlanId(recordPlanId);
                    channelVideoPlanVO.setIndustryCode(channelVideoPlanRequestVO.getIndustryCode());
                    channelVideoPlanVO.setId(id);
                    updateRecordPlan(channelVideoPlanVO);
                }
                //新增
                else {

                        long recordPlanId = System.currentTimeMillis()+ buildRandom(8);

                        channelVideoPlanVO.setRecordPlanId(recordPlanId);

                        BeanUtils.copyProperties(channelVideoPlanVO, channelVideoPlanDO);
                        channelVideoPlanDO.setRecordPlanId(recordPlanId);
                        channelVideoPlanDO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                        channelVideoPlanDO.setChannelCode(channelVideoPlanVO.getChannelCode());
                        channelVideoPlanDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                        channelVideoPlanDO.setCreator(channelVideoPlanRequestVO.getUsername());
//                        channelVideoPlanDO.setModifier(channelVideoPlanRequestVO.getUsername());
                        channelVideoPlanDO.setCreateTime(new Date());
                        channelVideoPlanDO.setRecordPlanName(channelVideoPlanVO.getRecordPlanName());
                        channelVideoPlanDO.setTenantId(channelVideoPlanVO.getTenantId());
                        String industryCode = channelVideoPlanRequestVO.getIndustryCode();
                        String appVerCode = channelVideoPlanRequestVO.getAppVerCode();
                        channelVideoPlanDO.setIndustryCode(industryCode);
                        channelVideoPlanDO.setAppVerCode(appVerCode);


                        channelVideoPlanRepository.save(channelVideoPlanDO);
                        List<TimeSectionVO> timeSectionVOList = channelVideoPlanVO.getTimeSections();
                        if (!CollectionUtils.isEmpty(timeSectionVOList)) {
                            List<PlanTimeSectionDO> list = new ArrayList<>();
                            for (int j = 0; j < timeSectionVOList.size(); j++) {
                                TimeSectionVO timeSectionVO = timeSectionVOList.get(j);
                                PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                                planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                                planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                                planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                                planTimeSectionDO.setRecordPlanId(recordPlanId);
                                planTimeSectionDO.setCreator(channelVideoPlanRequestVO.getUsername());
                                planTimeSectionDO.setModifier(channelVideoPlanRequestVO.getUsername());
                                planTimeSectionDO.setCreateTime(new Date());
                                planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                                planTimeSectionDO.setIndustryCode(industryCode);
                                planTimeSectionDO.setAppVerCode(appVerCode);
                                list.add(planTimeSectionDO);
                            }
                            planTimeSectionRepository.saveBatch(list);
                        }
                        //调用录像计划整合方法
                    ChannelVideoPlanVO planVO = new ChannelVideoPlanVO();
                    planVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    planVO.setChannelCode(channelVideoPlanVO.getChannelCode());
                    planVO.setTenantId(channelVideoPlanVO.getTenantId());
                    mergeRecordPlan(planVO);

                    success++;

                }
            }
            log.info("==========保存通道计划结果:成功"+success+"条，失败"+fail+"条！");
        }
        else {
            throw new CustomException("通道录像计划列表不能为空!", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }
    
    public void recordPlanMergeParam()
    {
    	log.info("#################根据临时表整合开始");
    	List<ChannelVideoPlanVO> recordPlanList = channelVideoPlanMapper.queryRecordPlanMergeParam();
    	if( recordPlanList != null && recordPlanList.size() > DeviceConstants.ZERO_NUM )
    	{
    		log.info("#################recordPlanList列表长度：" + recordPlanList.size());
    		for( ChannelVideoPlanVO planVO : recordPlanList )
    		{
    			log.info("#################整合入参：" + JSON.toJSONString(planVO));
    			try {
    				mergeRecordPlan(planVO);
    			} catch (CustomException e) {
    				log.error("CustomException:" + e.getMessage(), e);
    			} catch (Exception e) {
    				log.error("Exception:" + e.getMessage(), e);
    			} 
    		}
    	}
    	log.info("#################根据临时表整合结束");
    }

    @Override
    public Result<List<String>> queryRecordPlanDate(VideoPlanDateVO vo) {

        YearMonth yearMonth = YearMonth.parse(vo.getYear() + "-" + String.format("%02d",vo.getMonth()), DateTimeFormatter.ofPattern("yyyy-MM"));
        YearMonth nextYearMonth = YearMonth.parse(vo.getNextYear() + "-" + String.format("%02d",vo.getNextMonth()), DateTimeFormatter.ofPattern("yyyy-MM"));
        vo.setFirstDayOfMonth( yearMonth.atDay(1));
        vo.setLastDayOfMonth( yearMonth.atEndOfMonth());
        vo.setNextFirstDayOfMonth(nextYearMonth.atDay(1));
        vo.setNextLastDayOfMonth( nextYearMonth.atEndOfMonth());
        List<ChannelVideoPlanDTO> channelVideoPlanDTOS = channelVideoPlanMapper.queryRecordPlanDate(vo);
        List<String> dateList = new ArrayList<>();
        for (ChannelVideoPlanDTO channelVideoPlanDTO : channelVideoPlanDTOS) {
            // 确定需要生成日期的星期几
            List<Integer> daysOfWeek = channelVideoPlanDTO.getDayOfWeek() == null
                    ? IntStream.rangeClosed(1, 7).boxed().collect(Collectors.toList())
                    : Collections.singletonList(channelVideoPlanDTO.getDayOfWeek());

            // 为每一个星期几生成日期
            for (Integer day : daysOfWeek) {
                List<String> datesByDayOfWeek = findDatesByDayOfWeek(
                        channelVideoPlanDTO.getStartDate(),
                        channelVideoPlanDTO.getEndDate(),
                        day
                );
                dateList.addAll(datesByDayOfWeek);
            }
        }
        // 默认存储7天
        return Result.data(deduplicateAndSortDates(dateList, 7));
    }

    @Override
    public void addRecordPlanSingle(ChannelVideoPlanVO channelVideoPlanVO) {
        VideoOrderReq videoOrderReq = new VideoOrderReq();
        videoOrderReq.setTenantId(channelVideoPlanVO.getTenantId());
        videoOrderReq.setChannelCode(channelVideoPlanVO.getChannelCode());
        videoOrderReq.setDeviceCode(channelVideoPlanVO.getDeviceCode());
        videoOrderReq.setIndustryCode(channelVideoPlanVO.getIndustryCode());
        Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);
        ChannelVideoPlanDO channelVideoPlanDO = new ChannelVideoPlanDO();
        if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                && !StringUtils.isEmpty(resp.getData().getOrderCode())
                && !StringUtils.isEmpty(resp.getData().getPackageCode())) {
            String orderCode = resp.getData().getOrderCode();
            String packageCode = resp.getData().getPackageCode();
            channelVideoPlanDO.setOrderCode(orderCode);
            channelVideoPlanDO.setPackageCode(packageCode);
            channelVideoPlanVO.setOrderCode(orderCode);
        } else {
            log.error(NOT_EXIST_ORDER_INFO);
            throw new CustomException(NOT_EXIST_ORDER_INFO);
        }

        ChannelVideoPlanVO tempChannelVideoPlanVO = new ChannelVideoPlanVO();
        tempChannelVideoPlanVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
        tempChannelVideoPlanVO.setChannelCode(channelVideoPlanVO.getChannelCode());
        tempChannelVideoPlanVO.setTenantId(channelVideoPlanVO.getTenantId());
        tempChannelVideoPlanVO.setIndustryCode(channelVideoPlanVO.getIndustryCode());
        List<ChannelVideoPlanDO> channelVideoPlanVOList = channelVideoPlanRepository.queryVideoPlanInfo(tempChannelVideoPlanVO);
        //修改
        if (channelVideoPlanVOList.size()> 0) {
            Long recordPlanId = channelVideoPlanVOList.get(0).getRecordPlanId();
            Long id = channelVideoPlanVOList.get(0).getId();
            channelVideoPlanVO.setRecordPlanId(recordPlanId);
            channelVideoPlanVO.setId(id);
            updateRecordPlan(channelVideoPlanVO);
        }
        //新增
        else {
            long recordPlanId = System.currentTimeMillis()+ buildRandom(8);
            channelVideoPlanVO.setRecordPlanId(recordPlanId);
            BeanUtils.copyProperties(channelVideoPlanVO, channelVideoPlanDO);
            channelVideoPlanDO.setRecordPlanId(recordPlanId);
            channelVideoPlanDO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
            channelVideoPlanDO.setChannelCode(channelVideoPlanVO.getChannelCode());
            channelVideoPlanDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            channelVideoPlanDO.setCreator(channelVideoPlanVO.getUsername());
            channelVideoPlanDO.setModifier(channelVideoPlanVO.getUsername());
            channelVideoPlanDO.setCreateTime(new Date());
            channelVideoPlanDO.setRecordPlanName(channelVideoPlanVO.getRecordPlanName());
            channelVideoPlanDO.setTenantId(channelVideoPlanVO.getTenantId());
            String industryCode = channelVideoPlanVO.getIndustryCode();
            String appVerCode = channelVideoPlanVO.getAppVerCode();
            channelVideoPlanDO.setIndustryCode(industryCode);
            channelVideoPlanDO.setAppVerCode(appVerCode);
            channelVideoPlanRepository.save(channelVideoPlanDO);
            List<TimeSectionVO> timeSectionVOList = channelVideoPlanVO.getTimeSections();
            if (!CollectionUtils.isEmpty(timeSectionVOList)) {
                List<PlanTimeSectionDO> list = new ArrayList<>();
                for (int j = 0; j < timeSectionVOList.size(); j++) {
                    TimeSectionVO timeSectionVO = timeSectionVOList.get(j);
                    PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                    planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                    planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                    planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                    planTimeSectionDO.setRecordPlanId(recordPlanId);
                    planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                    planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                    planTimeSectionDO.setCreateTime(new Date());
                    planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                    planTimeSectionDO.setIndustryCode(industryCode);
                    planTimeSectionDO.setAppVerCode(appVerCode);
                    list.add(planTimeSectionDO);
                }
                planTimeSectionRepository.saveBatch(list);
            }
            //调用录像计划整合方法
            ChannelVideoPlanVO planVO = new ChannelVideoPlanVO();
            planVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
            planVO.setChannelCode(channelVideoPlanVO.getChannelCode());
            planVO.setTenantId(channelVideoPlanVO.getTenantId());
            mergeRecordPlan(planVO);
        }
    }

    /**
     * 修改通道录像计划
     *
     * @param channelVideoPlanVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecordPlan(ChannelVideoPlanVO channelVideoPlanVO) {

        if (null == channelVideoPlanVO.getRecordPlanId()) {
            throw new CustomException("修改通道录像计划id不能为空!", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        VideoOrderReq videoOrderReq = new VideoOrderReq();

        videoOrderReq.setTenantId(channelVideoPlanVO.getTenantId());
        videoOrderReq.setChannelCode(channelVideoPlanVO.getChannelCode());
        videoOrderReq.setDeviceCode(channelVideoPlanVO.getDeviceCode());
        videoOrderReq.setIndustryCode(channelVideoPlanVO.getIndustryCode());

        Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);
        ChannelVideoPlanDO channelVideoPlanDO = new ChannelVideoPlanDO();
        if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                && !StringUtils.isEmpty(resp.getData().getOrderCode())
                && !StringUtils.isEmpty(resp.getData().getPackageCode())) {
            String orderCode = resp.getData().getOrderCode();
            String packageCode = resp.getData().getPackageCode();
            channelVideoPlanDO.setOrderCode(orderCode);
            //获取修改人
            channelVideoPlanDO.setModifier(SecurityUtils.getOperator());
            channelVideoPlanDO.setPackageCode(packageCode);
            channelVideoPlanVO.setOrderCode(orderCode);
        } else {
            throw new CustomException(NOT_EXIST_ORDER_INFO, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        BeanUtils.copyProperties(channelVideoPlanVO, channelVideoPlanDO);
        String industryCode = channelVideoPlanVO.getIndustryCode();
        String appVerCode = channelVideoPlanVO.getAppVerCode();
        channelVideoPlanDO.setUpdateTime(new Date());
        channelVideoPlanRepository.updateById(channelVideoPlanDO);
        planTimeSectionRepository.deleteByRecordPlanId(channelVideoPlanVO);
        List<TimeSectionVO> timeSectionVOList = channelVideoPlanVO.getTimeSections();
        if (!CollectionUtils.isEmpty(timeSectionVOList)) {
            List<PlanTimeSectionDO> list = new ArrayList<>();
            for (int i = 0; i < timeSectionVOList.size(); i++) {
                TimeSectionVO timeSectionVO = timeSectionVOList.get(i);
                PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                planTimeSectionDO.setRecordPlanId(channelVideoPlanVO.getRecordPlanId());
                planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                planTimeSectionDO.setUpdateTime(new Date());
                planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                planTimeSectionDO.setIndustryCode(industryCode);
                planTimeSectionDO.setAppVerCode(appVerCode);
                list.add(planTimeSectionDO);
            }
            planTimeSectionRepository.saveBatch(list);
        }
        //调用录像计划整合方法
        ChannelVideoPlanVO planVO = new ChannelVideoPlanVO();
        planVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
        planVO.setChannelCode(channelVideoPlanVO.getChannelCode());
        planVO.setTenantId(channelVideoPlanVO.getTenantId());
        mergeRecordPlan(planVO);

    }

    /**
     * 删除通道录像计划
     * @param deleteChannelVideoPlanVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecordPlan(DeleteChannelVideoPlanVO deleteChannelVideoPlanVO) {

        VideoOrderReq videoOrderReq = new VideoOrderReq();
        videoOrderReq.setTenantId(deleteChannelVideoPlanVO.getTenantId());
        videoOrderReq.setChannelCode(deleteChannelVideoPlanVO.getChannelCode());
        videoOrderReq.setDeviceCode(deleteChannelVideoPlanVO.getDeviceCode());
        videoOrderReq.setIndustryCode(deleteChannelVideoPlanVO.getIndustryCode());

        Result<VideoOrderResp> resp = videoOrderService.getVideoOrderInfo(videoOrderReq);
        if (!ObjectUtils.isEmpty(resp) && !ObjectUtils.isEmpty(resp.getData())
                && !StringUtils.isEmpty(resp.getData().getOrderCode())) {
            String orderCode = resp.getData().getOrderCode();
            deleteChannelVideoPlanVO.setOrderCode(orderCode);
        } else {
            throw new CustomException(NOT_EXIST_ORDER_INFO, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        ChannelVideoPlanVO tempChannelVideoPlanVO = new ChannelVideoPlanVO();
        tempChannelVideoPlanVO.setDeviceCode(deleteChannelVideoPlanVO.getDeviceCode());
        tempChannelVideoPlanVO.setChannelCode(deleteChannelVideoPlanVO.getChannelCode());
        tempChannelVideoPlanVO.setTenantId(deleteChannelVideoPlanVO.getTenantId());
        tempChannelVideoPlanVO.setIndustryCode(deleteChannelVideoPlanVO.getIndustryCode());
        List<ChannelVideoPlanDO> channelVideoPlanVOList = channelVideoPlanRepository.queryVideoPlanInfo(tempChannelVideoPlanVO);
        if (channelVideoPlanVOList.size() == 0) {
            throw new CustomException("删除失败,通道录像计划不存在!", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        ChannelVideoPlanDO channelVideoPlanDO = channelVideoPlanVOList.get(0);

        long recordPlanId = channelVideoPlanDO.getRecordPlanId();
        channelVideoPlanRepository.removeById(channelVideoPlanDO.getId());
        ChannelVideoPlanVO channelVideoPlanVO = new ChannelVideoPlanVO();
        channelVideoPlanVO.setRecordPlanId(recordPlanId);
        channelVideoPlanVO.setIndustryCode(deleteChannelVideoPlanVO.getIndustryCode());
        planTimeSectionRepository.deleteByRecordPlanId(channelVideoPlanVO);
        //调用录像计划整合方法
        ChannelVideoPlanVO planVO = new ChannelVideoPlanVO();
        planVO.setDeviceCode(deleteChannelVideoPlanVO.getDeviceCode());
        planVO.setChannelCode(deleteChannelVideoPlanVO.getChannelCode());
        planVO.setTenantId(deleteChannelVideoPlanVO.getTenantId());
        mergeRecordPlan(planVO);

    }


    /**
     * 功能描述: 批量删除通道录像计划
     * @auther: zhaozj
     * @date: 2023/01/07
     * @version: release_v1.5.60
     * @requestID: 视频管理_设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchDeleteRecordPlan(BatchDeleteChannelVideoPlanVO vo) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        vo.setTenantId(sysUser.getTenantId());
        vo.setIndustryCode(sysUser.getIndustryCode());
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            return Result.fail(VideoConstants.TOWER_ACCOUT_CANNOT_DELETE);
        }

        // 录像计划批量删除最大数量取枚举值
        try {
            String num = videoControlService.selectDictDataByType(VideoConstants.BATCH_DELETE_MAX_NUM);
            if (StringUtils.isNotEmpty(num) && vo.getRecordPlanIdList().size() > Integer.valueOf(num)) {
                return Result.fail(VideoConstants.BATCH_NUM_LIMIT + num + VideoConstants.BATCH_NUM_LIMIT_MAX);
            }
        } catch (Exception e) {
            log.error(VideoConstants.EXCEPTION + e.getMessage(), e);
        }

        //获取录像计划信息
        List<ChannelVideoPlanVO> planList = channelVideoPlanMapper.queryVideoPlanList(vo);
        if (!CollectionUtils.isEmpty(planList)) {
            //判断是否存在启用状态得录像计划
            boolean delFlag = planList.stream().filter(item->item.getRecordPlanStatus()==VideoConstants.ONE).findAny().isPresent();
            //只保留停用得录像计划，进行删除
            List<ChannelVideoPlanVO> stopList = planList.stream().filter(algo -> algo.getRecordPlanStatus()==VideoConstants.ZERO).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(stopList)) {
                List<Long> idList = stopList.stream().map(ChannelVideoPlanVO::getId).collect(Collectors.toList());
                List<Long> recordPlanIdList = stopList.stream().map(ChannelVideoPlanVO::getRecordPlanId).collect(Collectors.toList());
                vo.setRecordPlanIdList(recordPlanIdList);
                //批量删除录像计划信息
                channelVideoPlanMapper.batchDelPlan(idList);
                channelVideoPlanMapper.batchDelTimeSec(vo);
            }
            if(delFlag) {
                return Result.fail(VideoConstants.STOP_PLAN_DELETE);
            }
        } else {
            return Result.fail(VideoConstants.PARAM_DATA_ERROR);
        }
        return Result.success(VideoConstants.BATCH_DELETE_SUCCESS);
    }


    /**
     * 功能描述: 批量停用/启用通道录像计划
     * @auther: zhaozj
     * @date: 2023/01/31
     * @version: release_v1.5.70
     * @requestID: 视频管理_设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> batchStartOrEndRecordPlan(BatchStartOrEndRecordPlanVO vo) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        vo.setTenantId(sysUser.getTenantId());
        vo.setIndustryCode(sysUser.getIndustryCode());
        log.info(VideoConstants.BATCH_START_END_RECORD_PLAN + VideoConstants.CONTROLLERREQ + vo);
        //铁塔账号无法进行录像计划停用启用操作
        if (Constants.ZERO.equals(sysUser.getUserType())) {
            return Result.fail(VideoConstants.TOWER_ACCOUT_CANNOT_START_END);
        }

        // 录像计划批量停用启用最大数量取枚举值
        try {
            String num = videoControlService.selectDictDataByType(VideoConstants.BATCH_START_END_MAX_NUM);
            if (StringUtils.isNotEmpty(num) && vo.getRecordPlanIdList().size() > Integer.valueOf(num)) {
                return Result.fail(VideoConstants.BATCH_NUM_LIMIT + num + VideoConstants.BATCH_NUM_LIMIT_MAX);
            }
        } catch (Exception e) {
            log.error(VideoConstants.EXCEPTION + e.getMessage(), e);
        }

        BatchDeleteChannelVideoPlanVO delVO = new BatchDeleteChannelVideoPlanVO();
        delVO.setRecordPlanIdList(vo.getRecordPlanIdList());
        delVO.setTenantId(sysUser.getTenantId());
        delVO.setIndustryCode(sysUser.getIndustryCode());
        log.info(VideoConstants.BATCH_START_END_RECORD_PLAN_MAIN_START + System.currentTimeMillis());
        //批量停用/启用通道录像计划共同方法
        if (recordPlanCommon(delVO, sysUser.getUserName(), vo.getStartEndStatus())) {
            return Result.fail(VideoConstants.PARAM_DATA_ERROR);
        }

        log.info(VideoConstants.BATCH_START_END_RECORD_PLAN_MAIN_END + System.currentTimeMillis());
        if (VideoConstants.OPERATOR_TYPE.equals(vo.getStartEndStatus())) {
            return Result.success(VideoConstants.BATCH_START_SUCCESS);
        } else {
            return Result.success(VideoConstants.BATCH_END_SUCCESS);
        }
    }


    /**
     * 批量停用/启用通道录像计划共同方法
     * @param delVO
     * @return
     */
    public Boolean recordPlanCommon(BatchDeleteChannelVideoPlanVO delVO, String userName, String startEndStatus) {
        log.info(VideoConstants.RECORD_PLAN_COMMON_MAIN_START + System.currentTimeMillis());

        Boolean boolFlag = false;
        //获取录像计划信息
        List<ChannelVideoPlanVO> planList = channelVideoPlanMapper.queryVideoPlanList(delVO);
        if (!CollectionUtils.isEmpty(planList)) {
            List<Long> idList = planList.stream().map(ChannelVideoPlanVO::getId).collect(Collectors.toList());
            //批量更新数据
            BatchStartOrEndRecordPlanVO vo = new BatchStartOrEndRecordPlanVO();
            vo.setIdList(idList);
            vo.setModifier(userName);
            vo.setStartEndStatus(startEndStatus);
            channelVideoPlanMapper.batchUpdatePlan(vo);

            //停用已停用状态的录像或启用已启用状态的录像数据不需要进行整合
            List<ChannelVideoPlanVO> mergeList = planList.stream().filter(algo -> algo.getRecordPlanStatus()!=Integer.valueOf(vo.getStartEndStatus())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(mergeList)) {
                for (ChannelVideoPlanVO channelVideoPlanVO : mergeList) {
                    //调用录像计划整合方法
                    ChannelVideoPlanVO planVO = new ChannelVideoPlanVO();
                    planVO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    planVO.setChannelCode(channelVideoPlanVO.getChannelCode());
                    planVO.setTenantId(vo.getTenantId());
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            videoRecordMergeAsyncService.mergeRecordPlan(planVO);
                        }
                    });
                }
            }
        } else {
            boolFlag = true;
        }
        log.info(VideoConstants.RECORD_PLAN_COMMON_MAIN_END + System.currentTimeMillis());
        return boolFlag;
    }

    /**
     * 查询订单通道录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @Override
    public List<ChannelVideoPlanVO> queryOrderRecordPlanInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {

        List<ChannelVideoPlanVO> planVoList = new ArrayList<>();
        List<ChannelVideoPlanDO> planDOlist = channelVideoPlanRepository.queryOrderRecordPlanInfo(queryOrderDeviceSetDTO);
        Iterator<ChannelVideoPlanDO> it = planDOlist.iterator();
        while (it.hasNext()) {
            ChannelVideoPlanDO channelVideoPlanDO = it.next();
            ChannelVideoPlanVO channelVideoPlanVO = new ChannelVideoPlanVO();
            BeanUtils.copyProperties(channelVideoPlanDO, channelVideoPlanVO);
            VideoTimeSectionVO timeSectionVO = new VideoTimeSectionVO();
            timeSectionVO.setRecordPlanId(String.valueOf(channelVideoPlanVO.getRecordPlanId()));
            timeSectionVO.setDeleteFlag("0");
            timeSectionVO.setIndustryCode(queryOrderDeviceSetDTO.getIndustryCode());
            List<VideoTimeSectionVO> timeSections = videoQueryDAO.queryTimeSectionInfo(timeSectionVO);
            String timeSectionStr = JSON.toJSONString(timeSections);
            List<TimeSectionVO> timeSectionsNew = JSON.parseArray(timeSectionStr, TimeSectionVO.class);
            channelVideoPlanVO.setTimeSections(timeSectionsNew);
            planVoList.add(channelVideoPlanVO);
        }

        return planVoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeRecordPlan(ChannelVideoPlanVO channelVideoPlanVO) {


        //获取录像计划信息
        List<ChannelVideoPlanVO> planList = channelVideoPlanMapper.getChannelVideoPlanList(channelVideoPlanVO);
        //如果没有，那么判断整合表是否存在，如果存在，则删除
        if(CollectionUtils.isEmpty(planList)){
            //判断是否有整合计划
            List<ChannelVideoPlanVO> planMergeList = channelVideoPlanMapper.getChannelVideoPlanMergeList(channelVideoPlanVO);
            if(planMergeList != null && planMergeList.size() >0 ) {
                //如果存在，就做删除处理

                long recordPlanId = planMergeList.get(0).getRecordPlanId();
                channelVideoPlanMapper.deleteChannelVideoPlan(planMergeList.get(0));
                channelVideoPlanMapper.deleteTimeSections(planMergeList.get(0));

                JSONObject inJson = new JSONObject();
                inJson.put("recordPlanId", recordPlanId);
                //属地化数据统一改造 add by wangyt 20240117
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(channelVideoPlanVO.getDeviceCode());
                //南向加字段 add by liuxt 20240117
                inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());


                Result<JSONObject> res = null;
                try {
                    NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
                    // 请求参数
                    paramter.setBody(inJson);
                    NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                    // 获取接口地址
                    path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_RECORD_PLAN);
                    // 接口路径参数
                    path.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    path.setChannelCode(channelVideoPlanVO.getChannelCode());
                    paramter.setPath(path);

                    // 调用统一网关接口
                    log.info(VideoConstants.DELETE_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
                    com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                    log.info(VideoConstants.DELETE_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
                    Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                    res = apiInfoUtils.transResult(cr, respDictData.getData());
                }catch (Exception e){
                    log.error(VideoConstants.DELETE_RECORD_PLAN_ERROR +VideoConstants.EQUAL_INFO, e);
                }
                if (ResultCode.SUCCESS.getCode() != res.getCode() ) {
                    throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
                }

            }

        }else{//多条数据

            //判断是否有整合计划
            List<ChannelVideoPlanVO> planMergeList = channelVideoPlanMapper.getChannelVideoPlanMergeList(channelVideoPlanVO);
            if(planMergeList != null && planMergeList.size() >0 ){
                //如果存在，就做更新处理



                //获取开始时间取最小，终止时间取最大,是否全天录像，是否开启声音，存储最大天数
                ChannelVideoPlanVO planMaxVo = channelVideoPlanMapper.getMaxChannelVideoPlan(channelVideoPlanVO);
                planMaxVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                planMaxVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                planMaxVo.setRecordPlanId(planMergeList.get(0).getRecordPlanId());

                //inJson.put(DeviceNorthIntfConstants.TIMESECTIONS,planVo.getTimeSections());
                JSONObject inJson = new JSONObject();

                //如果存在全体存储，那么通道录像计划时间段表不需要整合，取全天数据即可
                if (planMaxVo.getIsAllDay() == 1) {
                    ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                    queryVo.setIsAllDay(1);
                    queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                    List<ChannelVideoPlanVO> planAllList = channelVideoPlanMapper.getChannelVideoPlanList(queryVo);
                    if (!CollectionUtils.isEmpty(planAllList)) {
                        List<TimeSectionVO> timeSections = channelVideoPlanMapper.getAllTimeSections(planAllList.get(0));
                        planMaxVo.setTimeSections(timeSections);
                        inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, timeSections);
                    }

                } else {//不存在全天存储，那么需要合并时间段

                    List<TimeSectionVO> tmpList = new ArrayList<>();
                    for (int j = 1; j <= 7; ++j) {
                        ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                        queryVo.setDayOfWeek(j);
                        queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                        queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                        List<TimeSectionVO> timeSectionsList = channelVideoPlanMapper.getTimeSectionsList(queryVo);
                        if (!CollectionUtils.isEmpty(timeSectionsList)) {
                            List<TimeBucket> timeBucketList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (TimeSectionVO list : timeSectionsList) {
                                String startTime = DATE_FORMAT + list.getStartTime().substring(0, 2) + ":" + list.getStartTime().substring(2, 4) + ":" + list.getStartTime().substring(4, 6);
                                String endtTime = DATE_FORMAT + list.getEndTime().substring(0, 2) + ":" + list.getEndTime().substring(2, 4) + ":" + list.getEndTime().substring(4, 6);
                                try {
                                    timeBucketList.add(new TimeBucket(sdf.parse(startTime), sdf.parse(endtTime)));


                                } catch (Exception e) {
                                    log.error("时间转换错误：", e);
                                }
                            }

                            List<TimeNode> nodeList = TimeMer.merge(timeBucketList);
                            List<TimeBucket> timeBuckets = TimeMer.convert(nodeList);
                            for (TimeBucket timeBucket : timeBuckets) {
                                TimeSectionVO tmpTS = new TimeSectionVO();
                                tmpTS.setDayOfWeek(j);
                                tmpTS.setStartTime(sdf.format(timeBucket.getStart()).substring(11,13)+sdf.format(timeBucket.getStart()).substring(14,16)+sdf.format(timeBucket.getStart()).substring(17,19));
                                tmpTS.setEndTime(sdf.format(timeBucket.getEnd()).substring(11,13)+sdf.format(timeBucket.getEnd()).substring(14,16)+sdf.format(timeBucket.getEnd()).substring(17,19));
                                tmpList.add(tmpTS);
                            }
                        }
                    }
                    planMaxVo.setTimeSections(tmpList);
                    inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, tmpList);

                }
                    //更新通道录像计划整合表
                    channelVideoPlanMapper.updateChannelVideoPlanMerge(planMaxVo);
                    //先删除通道录像计划时间段整合表
                    channelVideoPlanMapper.deleteTimeSections(planMaxVo);
                    //更新通道录像计划时间段整合表
                    List<TimeSectionVO> timeSectionVOList = planMaxVo.getTimeSections();
                    if (!CollectionUtils.isEmpty(timeSectionVOList)) {
                        List<PlanTimeSectionDO> list = new ArrayList<>();
                        for (int i = 0; i < timeSectionVOList.size(); i++) {
                            TimeSectionVO timeSectionVO = timeSectionVOList.get(i);
                            PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                            planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                            planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                            planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                            planTimeSectionDO.setRecordPlanId(planMergeList.get(0).getRecordPlanId());
                            planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                            planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                            planTimeSectionDO.setCreateTime(new Date());
                            planTimeSectionDO.setUpdateTime(new Date());
                            planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                            planTimeSectionDO.setDeviceCode(planMergeList.get(0).getDeviceCode());
                            list.add(planTimeSectionDO);
                        }
                        channelVideoPlanMapper.saveTimeSectionsMergeBatch(list);
                    }


                    inJson.put(DeviceNorthIntfConstants.IS_ALL_Day, planMaxVo.getIsAllDay());
                    inJson.put(DeviceNorthIntfConstants.STREAM_TYPE, planMaxVo.getStreamType());
                    inJson.put(DeviceNorthIntfConstants.STORAGEDAY, planMaxVo.getStorageDay());
                    inJson.put(DeviceNorthIntfConstants.ISAUDIO, planMaxVo.getIsAudio());
                    inJson.put(DeviceNorthIntfConstants.RECORDPLANNAME, "录像计划整合"+String.valueOf(System.currentTimeMillis()));
                    inJson.put(DeviceNorthIntfConstants.RECORDPLANSTATUS, planMaxVo.getRecordPlanStatus());
                    inJson.put(DeviceNorthIntfConstants.STARTDATE, planMaxVo.getStartDate());
                    inJson.put(DeviceNorthIntfConstants.ENDDATE, planMaxVo.getEndDate());
                    inJson.put(DeviceNorthIntfConstants.DEVICECODE, planMaxVo.getDeviceCode());
                    inJson.put(DeviceNorthIntfConstants.CHANNELCODE, planMaxVo.getChannelCode());
                    inJson.put(DeviceNorthIntfConstants.RECORD_PLAN_ID, planMergeList.get(0).getRecordPlanId());
                    //属地化数据统一改造 add by wangyt 20240117
                    CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(planMaxVo.getDeviceCode());
                    //南向加字段 add by liuxt 20240117
                    inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                    inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());

                    Result<JSONObject> res = null;
                    try {
                        NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
                        // 请求参数
                        paramter.setBody(inJson);
                        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                        // 获取接口地址
                        path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_RECORD_PLAN);
                        // 接口路径参数
                        path.setDeviceCode(planMaxVo.getDeviceCode());
                        path.setChannelCode(planMaxVo.getChannelCode());
                        paramter.setPath(path);

                        // 调用统一网关接口
                        log.info(VideoConstants.UPDATE_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
                        com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                                NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                                NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                        log.info(VideoConstants.UPDATE_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
                        Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                        res = apiInfoUtils.transResult(cr, respDictData.getData());
                    }catch (Exception e){
                        log.error(VideoConstants.UPDATE_RECORD_PLAN_ERROR +VideoConstants.EQUAL_INFO, e);
                    }

                    if (ResultCode.SUCCESS.getCode() != res.getCode()) {
                        throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
                    }



            }else {
                //新增

                //获取开始时间取最小，终止时间取最大,是否全天录像，是否开启声音，存储最大天数
                ChannelVideoPlanVO planMaxVo = channelVideoPlanMapper.getMaxChannelVideoPlan(channelVideoPlanVO);
                planMaxVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                planMaxVo.setChannelCode(channelVideoPlanVO.getChannelCode());

                //inJson.put(DeviceNorthIntfConstants.TIMESECTIONS,planVo.getTimeSections());
                JSONObject inJson = new JSONObject();

                //如果存在全体存储，那么通道录像计划时间段表不需要整合，取全天数据即可
                if (planMaxVo.getIsAllDay() == 1) {
                    ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                    queryVo.setIsAllDay(1);
                    queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                    List<ChannelVideoPlanVO> planAllList = channelVideoPlanMapper.getChannelVideoPlanList(queryVo);
                    if (!CollectionUtils.isEmpty(planAllList)) {
                        List<TimeSectionVO> timeSections = channelVideoPlanMapper.getAllTimeSections(planAllList.get(0));
                        planMaxVo.setTimeSections(timeSections);
                        inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, timeSections);
                    }

                } else {//不存在全天存储，那么需要合并时间段

                    List<TimeSectionVO> tmpList = new ArrayList<>();
                    for (int j = 1; j <= 7; ++j) {
                        ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                        queryVo.setDayOfWeek(j);
                        queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                        queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                        List<TimeSectionVO> timeSectionsList = channelVideoPlanMapper.getTimeSectionsList(queryVo);
                        if(!CollectionUtils.isEmpty(timeSectionsList)){
                            List<TimeBucket> timeBucketList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (TimeSectionVO list : timeSectionsList) {
                                String startTime = DATE_FORMAT + list.getStartTime().substring(0, 2) + ":" + list.getStartTime().substring(2, 4) + ":" + list.getStartTime().substring(4, 6);
                                String endtTime = DATE_FORMAT + list.getEndTime().substring(0, 2) + ":" + list.getEndTime().substring(2, 4) + ":" + list.getEndTime().substring(4, 6);
                                try {
                                    timeBucketList.add(new TimeBucket(sdf.parse(startTime), sdf.parse(endtTime)));


                                } catch (Exception e) {
                                    log.error("时间转换错误：", e);
                                }
                            }

                            List<TimeNode> nodeList = TimeMer.merge(timeBucketList);
                            List<TimeBucket> timeBuckets = TimeMer.convert(nodeList);
                            for (TimeBucket timeBucket : timeBuckets) {
                                TimeSectionVO tmpTS = new TimeSectionVO();
                                tmpTS.setDayOfWeek(j);
                                tmpTS.setStartTime(sdf.format(timeBucket.getStart()).substring(11,13)+sdf.format(timeBucket.getStart()).substring(14,16)+sdf.format(timeBucket.getStart()).substring(17,19));
                                tmpTS.setEndTime(sdf.format(timeBucket.getEnd()).substring(11,13)+sdf.format(timeBucket.getEnd()).substring(14,16)+sdf.format(timeBucket.getEnd()).substring(17,19));
                                tmpList.add(tmpTS);
                            }
                        }
                    }
                    planMaxVo.setTimeSections(tmpList);
                    inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, tmpList);

                }
                String recordPlanName = "录像计划整合-"+String.valueOf(System.currentTimeMillis());
                inJson.put(DeviceNorthIntfConstants.IS_ALL_Day, planMaxVo.getIsAllDay());
                inJson.put(DeviceNorthIntfConstants.STREAM_TYPE, planMaxVo.getStreamType());
                inJson.put(DeviceNorthIntfConstants.STORAGEDAY, planMaxVo.getStorageDay());
                inJson.put(DeviceNorthIntfConstants.ISAUDIO, planMaxVo.getIsAudio());
                inJson.put(DeviceNorthIntfConstants.RECORDPLANNAME, recordPlanName);
                inJson.put(DeviceNorthIntfConstants.RECORDPLANSTATUS, planMaxVo.getRecordPlanStatus());
                inJson.put(DeviceNorthIntfConstants.STARTDATE, planMaxVo.getStartDate());
                inJson.put(DeviceNorthIntfConstants.ENDDATE, planMaxVo.getEndDate());
                inJson.put(DeviceNorthIntfConstants.DEVICECODE, planMaxVo.getDeviceCode());
                inJson.put(DeviceNorthIntfConstants.CHANNELCODE, planMaxVo.getChannelCode());

                Result<JSONObject> res = null;
                try {
                    NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
                    //属地化数据统一改造 add by wangyt 20240117
                    CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(planMaxVo.getDeviceCode());
                    //南向加字段 add by liuxt 20240117
                    inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                    inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());
                    // 请求参数
                    paramter.setBody(inJson);
                    NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                    // 获取接口地址
                    path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ADD_RECORD_PLAN);
                    // 接口路径参数
                    path.setDeviceCode(planMaxVo.getDeviceCode());
                    path.setChannelCode(planMaxVo.getChannelCode());
                    paramter.setPath(path);

                    // 调用统一网关接口
                    log.info(VideoConstants.ADD_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
                    com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                    log.info(VideoConstants.ADD_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
                    Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                    res = apiInfoUtils.transResult(cr, respDictData.getData());
                }catch (Exception e){
                    log.error(VideoConstants.ADD_RECORD_PLAN_ERROR +VideoConstants.EQUAL_INFO, e);
                }

                if (ResultCode.SUCCESS.getCode() == res.getCode() ) {

                    JSONObject resultJson = res.getData();

                    long recordPlanId = resultJson.getJSONObject("data").getLong("recordPlanId");
//                      long recordPlanId = System.currentTimeMillis();
                    planMaxVo.setRecordPlanId(recordPlanId);
                    ChannelVideoPlanDO channelVideoPlanDO = new ChannelVideoPlanDO();

                    BeanUtils.copyProperties(planMaxVo, channelVideoPlanDO);
                    channelVideoPlanDO.setRecordPlanId(recordPlanId);
                    channelVideoPlanDO.setDeviceCode(planMaxVo.getDeviceCode());
                    channelVideoPlanDO.setChannelCode(planMaxVo.getChannelCode());
                    channelVideoPlanDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                    channelVideoPlanDO.setCreator(channelVideoPlanVO.getUsername());
                    channelVideoPlanDO.setModifier(channelVideoPlanVO.getUsername());
                    channelVideoPlanDO.setCreateTime(new Date());
                    channelVideoPlanDO.setUpdateTime(new Date());
                    channelVideoPlanDO.setRecordPlanName(recordPlanName);
                    channelVideoPlanDO.setRecordPlanStatus(planMaxVo.getRecordPlanStatus());
                    //插入通道录像计划整合表
                    channelVideoPlanMapper.saveChannelVideoPlanMerge(channelVideoPlanDO);
                    //先删除通道录像计划时间段整合表
                    channelVideoPlanMapper.deleteTimeSections(planMaxVo);
                    //更新通道录像计划时间段整合表
                    List<TimeSectionVO> timeSectionVOList = planMaxVo.getTimeSections();
                    if (!CollectionUtils.isEmpty(timeSectionVOList)) {
                        List<PlanTimeSectionDO> list = new ArrayList<>();
                        for (int i = 0; i < timeSectionVOList.size(); i++) {
                            TimeSectionVO timeSectionVO = timeSectionVOList.get(i);
                            PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                            planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                            planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                            planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                            planTimeSectionDO.setRecordPlanId(planMaxVo.getRecordPlanId());
                            planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                            planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                            planTimeSectionDO.setCreateTime(new Date());
                            planTimeSectionDO.setUpdateTime(new Date());
                            planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                            planTimeSectionDO.setDeviceCode(planMaxVo.getDeviceCode());
                            list.add(planTimeSectionDO);
                        }
                        channelVideoPlanMapper.saveTimeSectionsMergeBatch(list);
                    }

                }else{
                    throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
                }


            }
        }
    }


    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    private static int buildRandom(int length) {
        int num = 1;
        double random = new SecureRandom().nextDouble();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) (random * num);
    }
}
