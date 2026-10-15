package com.chinatower.video.modules.arcruiseplan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanVo;
import com.chinatower.video.modules.arcruiseplan.mapper.ArCruisePlanMapper;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanService;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.dao.VideoQueryDAO;
import com.chinatower.video.modules.query.entity.*;
import com.chinatower.video.modules.query.service.VideoPtzQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * AR标签信息表 服务实现类
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@Service
@Slf4j
public class ArCruisePlanServiceImpl extends ServiceImpl<ArCruisePlanMapper, ArCruisePlan> implements IArCruisePlanService {

    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    IVideoDeviceService iVideoDeviceService;
    @Autowired
    VideoPtzQueryDAO videoPtzQueryDAO;
    @Autowired
    VideoQueryDAO videoQueryDAO;
    @Resource
    private NetConnectUtils connectUtils;
    @Resource
    ICasCadeQueryService iCasCadeQueryService;

    @Resource
    VideoPtzQueryService videoPtzQueryService;

    @Override
    public Result doLinkage(DoLinkageReq doLinkageReq, Map<String, String> headerMap, boolean b) {
        Result res;
        try {
            NetConnectUtils.RequestParamter<DoLinkageReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(doLinkageReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DOLINKAGE);
            // 接口路径参数
            path.setDeviceCode(doLinkageReq.getDeviceCode());
            path.setChannelCode(doLinkageReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by wangyt 20240129
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(doLinkageReq.getDeviceCode());
            // 调用统一网关接口
            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            // 枪球联动成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.success(VideoConstants.SUCCESS_MSG);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, null));
                log.error("枪球联动失败，失败原因：" + cr.getErrorMsg());
            }
        } catch (Exception e) {
            res = Result.fail(e.getMessage());
        }
        return res;
    }


    @Async("threadPoolTaskExecutor")
    public void doPlanByGateway(ArPlanDTO arPlan, SysUser currentUser) {
        try {
            //根据不同的标签类别，计算其百分比坐标
            Map<String, String> pointMap = this.getPointMap(arPlan);

            if (StringUtil.isNotBlank(pointMap.get(VideoConstants.POINT_X)) && StringUtil.isNotBlank(pointMap.get(VideoConstants.POINT_Y))) {
                DoLinkageReq doLinkageReq = new DoLinkageReq();
                // 根据分辨率和坐标计算坐标参数
                doLinkageReq.setPointX(pointMap.get(VideoConstants.POINT_X));
                doLinkageReq.setPointY(pointMap.get(VideoConstants.POINT_Y));
                doLinkageReq.setMode(1);
                String deviceCode = arPlan.getArViewId();
                String channelCode = arPlan.getChannelCode();
                doLinkageReq.setDeviceCode(deviceCode);
                doLinkageReq.setChannelCode(channelCode);
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(VideoConstants.CUSTOMERCODE, currentUser.getTenantId());
                log.info("调用转动摄像机接口入参:{}", JSON.toJSONString(doLinkageReq));
                Result result = doLinkage(doLinkageReq, headerMap, false);
                log.info("调用枪球联动控制接口返回：" + result.getMsg());
                log.info("执行计划：" + arPlan.getId() + ",调用云台至标签：" + arPlan.getTagId() + "，时间："
                        + DateUtil.now() + "，停留时长：" + arPlan.getStayDuration() + "秒");
            }
        } catch (Exception e) {
            log.error("执行计划异常", e);
        }
    }

    public Map<String,String> getPointMap(ArPlanDTO arPlan) {
        String tagClass = arPlan.getTagClass();
        switch (tagClass)
        {
            case VideoConstants.TAG_CLASS_101:
            case VideoConstants.TAG_CLASS_102:
                return this.getPointMapTag(arPlan);
            case VideoConstants.TAG_CLASS_103:
            case VideoConstants.TAG_CLASS_105:
            case VideoConstants.TAG_CLASS_106:
            case VideoConstants.TAG_CLASS_107:
            case VideoConstants.TAG_CLASS_108:
                return this.getPointMapDevice(arPlan);
            case VideoConstants.TAG_CLASS_104:
                return this.getPointMapLayer(arPlan);
            default:
                break;
        }
        return new HashMap<>();
    }

    public Map<String,String> getPointMap101(ArPlanDTO arPlan) {
        Map<String, String> map = new HashMap<>();
        // 获取标签坐标
        String[] coordinates = arPlan.getCoordinate().split(",");
        if (coordinates.length < 2) {
            // 坐标不正确，跳过
            log.info("标签：" + arPlan.getTagId() + "坐标不正确");
            return map;
        }
        // 获取分辨率
        Integer resolutionx = arPlan.getResolutionX();
        Integer resolutiony = arPlan.getResolutionY();
        if (resolutionx == null || resolutiony == null || resolutionx == 0 || resolutiony == 0) {
            // 分辨率不正确，跳过
            log.info("标签：" + arPlan.getTagId() + "分辨率不正确");
        } else {
            // 根据分辨率和坐标计算坐标参数
            String pointX = String.valueOf((Double.parseDouble(coordinates[0]) / resolutionx) * 255);
            String pointY = String.valueOf((Double.parseDouble(coordinates[1]) / resolutiony) * 255);
            map.put(VideoConstants.POINT_X, pointX);
            map.put(VideoConstants.POINT_Y, pointY);
        }
        return map;
    }

    public Map<String,String> getPointMapTag(ArPlanDTO arPlan) {
        Map<String, String> map = new HashMap<>();

        if(StringUtil.isNotEmpty(arPlan.getLongitude()) && StringUtil.isNotEmpty(arPlan.getLatitude())){
            //构造入参
            VideoPtzReq videoPtzReq = this.getVideoPtzReq(arPlan, Double.parseDouble(arPlan.getLongitude()), Double.parseDouble(arPlan.getLatitude()));
            //经纬度转百分比坐标
            map = this.bulidMap(videoPtzReq);
        }
        return map;
    }

    public Map<String,String> getPointMapLayer(ArPlanDTO arPlan) {
        Map<String, String> map = new HashMap<>();

        ArTagFilterVO arTagFilterVO = new ArTagFilterVO();
        arTagFilterVO.setTagIdList(Collections.singletonList(arPlan.getTagId()));
        List<ArTagMoveRes> arTagByDevice = videoPtzQueryDAO.getArTagByLayer(arTagFilterVO);

        if(CollectionUtils.isNotEmpty(arTagByDevice)){
            double minx = arTagByDevice.get(0).getMinx();
            double miny = arTagByDevice.get(0).getMiny();
            double maxx = arTagByDevice.get(0).getMaxx();
            double maxy = arTagByDevice.get(0).getMaxy();
            if(!ObjectUtils.isEmpty(minx) && !ObjectUtils.isEmpty(miny)
                && !ObjectUtils.isEmpty(maxx) && !ObjectUtils.isEmpty(maxy)){
                double longitude = (minx + maxx) / 2;
                double latitude = (miny + maxy) / 2;
                //构造入参
                VideoPtzReq videoPtzReq = this.getVideoPtzReq(arPlan, longitude, latitude);
                //经纬度转百分比坐标
                map = this.bulidMap(videoPtzReq);
            }
        }
        return map;
    }

    public Map<String,String> getPointMapDevice(ArPlanDTO arPlan) {
        Map<String, String> map = new HashMap<>();

        ArTagFilterVO arTagFilterVO = new ArTagFilterVO();
        arTagFilterVO.setTagIdList(Collections.singletonList(arPlan.getTagId()));
        arTagFilterVO.setTenantId(arPlan.getTenantId());
        arTagFilterVO.setIndustryCode(arPlan.getIndustryCode());
        List<ArTagMoveRes> arTagByDevice = null;
        String tagClass = arPlan.getTagClass();
        if(VideoConstants.TAG_CLASS_103.equals(tagClass)){
            arTagByDevice = videoPtzQueryDAO.getArTagByDevice(arTagFilterVO);
        } else if (VideoConstants.TAG_CLASS_105.equals(tagClass))
            arTagByDevice = videoPtzQueryDAO.getArTagByIot(arTagFilterVO);
        else if (VideoConstants.TAG_CLASS_106.equals(tagClass)) {
            arTagByDevice = videoPtzQueryDAO.getArTagByUav(arTagFilterVO);
        } else if (VideoConstants.TAG_CLASS_107.equals(tagClass)) {
            arTagByDevice = videoPtzQueryDAO.getArTagBySensor(arTagFilterVO);
        } else if (VideoConstants.TAG_CLASS_108.equals(tagClass)) {
            arTagByDevice = videoPtzQueryDAO.getArTagByHorn(arTagFilterVO);
        }

        if(CollectionUtils.isNotEmpty(arTagByDevice)){
            //构造入参
            VideoPtzReq videoPtzReq = this.getVideoPtzReq(arPlan, arTagByDevice.get(0).getLongitude(), arTagByDevice.get(0).getLatitude());
            //经纬度转百分比坐标
            map = this.bulidMap(videoPtzReq);
        }
        return map;
    }

    private Map<String,String> bulidMap(VideoPtzReq videoPtzReq){
        Map<String, String> map = new HashMap<>();
        //经纬度转百分比坐标
        VideoPtzRes percentResult = videoPtzQueryService.getPercentPtzByMassPointBatchNew(videoPtzReq);
        List<PercentVO> vertexPercentList = percentResult.getVertexPercentList();
        if(CollectionUtils.isNotEmpty(vertexPercentList)){
            PercentVO percentVO = vertexPercentList.get(0);
            map.put(VideoConstants.POINT_X, String.valueOf(Double.parseDouble(percentVO.getPercentU()) * 255));
            map.put(VideoConstants.POINT_Y, String.valueOf(Double.parseDouble(percentVO.getPercentV()) * 255));
        }
        return map;
    }

    private VideoPtzReq getVideoPtzReq(ArPlanDTO arPlan,double longitude,double latitude) {
        VideoPtzReq videoPtzReq = new VideoPtzReq();
        videoPtzReq.setTenantId(arPlan.getTenantId());
        videoPtzReq.setDeviceCode(arPlan.getArViewId());
        videoPtzReq.setChannelCode(arPlan.getChannelCode());
        List<LongitudeLatitudeVO> vertexList = new ArrayList<>();
        LongitudeLatitudeVO longitudeLatitudeVO = new LongitudeLatitudeVO();
        longitudeLatitudeVO.setPointLon(longitude);
        longitudeLatitudeVO.setPointLat(latitude);
        vertexList.add(longitudeLatitudeVO);
        videoPtzReq.setVertexList(vertexList);
        return videoPtzReq;
    }

    @Override
    public List<ArCruisePlan> ttList(ArCruisePlanVo arCruisePlanVo) {
        return baseMapper.ttList(arCruisePlanVo);
    }

    @Override
    public List<ArCruisePlan> pageList(ArCruisePlanReq arCruisePlanReq) {
        return baseMapper.pageList(arCruisePlanReq);
    }
}
