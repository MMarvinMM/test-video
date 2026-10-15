package com.chinatower.video.modules.artags.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.ArTagsInfoCustomAPI;
import com.chinatower.video.api.entity.ArTagsInfoReq;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanRelService;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanService;
import com.chinatower.video.modules.artags.dataobject.*;
import com.chinatower.video.modules.artags.mapper.ArTagsInfoMapper;
import com.chinatower.video.modules.artags.mapper.ArTagsUserRelMapper;
import com.chinatower.video.modules.artags.service.IArTagsInfoService;
import com.chinatower.video.modules.constants.ArConstants;
import com.chinatower.video.modules.query.entity.ArTagFilterVO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * AR标签信息表 前端控制器
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
@RestController
@Slf4j
@Api(value = "AR标签", tags = "AR标签")
@SuppressWarnings("all")
public class ArTagsInfoController implements ArTagsInfoCustomAPI {

    @Autowired
    private IArTagsInfoService arTagsInfoService;

    @Autowired
    private IArCruisePlanService arCruisePlanService;

    @Autowired
    private IArCruisePlanRelService arCruisePlanRelService;
    @Autowired
    private ArTagsInfoMapper arTagsInfoMapper;

    @Autowired
    private ArTagsUserRelMapper arTagsUserRelMapper;

    @ApiOperation(value = "增加/修改AR标签", notes = "增加/修改AR标签")
    @PostMapping("/arTagsInfo/addOrUpdate")
    @Override
    public Result<Long> addOrUpdate(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq) {
        log.info("增加AR标签入参:{}", JSON.toJSONString(arTagsInfoReq));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 新增、查询增加tenantId、industryCode
        ArTagsInfo arTagsInfo = new ArTagsInfo();
        BeanUtils.copyBeanProp(arTagsInfo, arTagsInfoReq);
        arTagsInfo.setTenantId(currentUser.getTenantId());
        arTagsInfo.setIndustryCode(currentUser.getIndustryCode());
        arTagsInfo.setAppVerCode(currentUser.getAppVerCode());
        if(arTagsInfoReq.getVideoFile() != null && arTagsInfoReq.getVideoFile().size() > 0){
            arTagsInfo.setVideoFileString(new Gson().toJson(arTagsInfoReq.getVideoFile()));
        }
        //文本标签device_code设置为空串
        if(Constants.TWO.equals(arTagsInfoReq.getTagsType())){
            arTagsInfo.setDeviceCode("");
        }
        if (!StringUtils.isEmpty(arTagsInfoReq.getId())) {
            arTagsInfo.setId(Long.valueOf(arTagsInfoReq.getId()));
            arTagsInfo.setModifier(currentUser.getUserName());
            arTagsInfo.setUpdateTime(new Date());
            arTagsInfoService.update(arTagsInfo);
            return Result.success("修改AR标签成功!");
        } else {
            arTagsInfo.setCreator(currentUser.getUserName());
            arTagsInfo.setCreateTime(new Date());
            arTagsInfo.setDeleteFlag(0);
            return arTagsInfoService.add(arTagsInfo);
        }

    }

    @ApiOperation(value = "查询AR标签类型树", notes = "查询AR标签类型树")
    @GetMapping("/arTagsInfo/arTagsTypeTree")
    @Override
    public Result<List<BusinessDictType>> arTagsTypeTree() {
        log.info("查询AR标签类型树");
        List<BusinessDictType> list = arTagsInfoService.arTagsTypeTree(1);
        return Result.data(list);
    }

    @ApiOperation(value = "查询事件类型树", notes = "查询AR标签类型树")
    @GetMapping("/arTagsInfo/arTagsTypeTreeForEvent/{dictType}")
    @Override
    public Result<List<BusinessDictType>> arTagsTypeTreeForEvent(@PathVariable("dictType") Integer dictType) {
        log.info("查询事件类型树");
        List<BusinessDictType> list = arTagsInfoService.arTagsTypeTree(dictType);
        return Result.data(list);
    }

    @ApiOperation(value = "查询AR标签列表", notes = "查询AR标签列表")
    @PostMapping("/arTagsInfo/listTree")
    @Override
    public Result<List<ArTagTree>> listTree(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq) {
        log.info("查询AR标签列表入参:{}", arTagsInfoReq);
        if(!StringUtils.isEmpty(arTagsInfoReq.getTagClass()) && "102".equals(arTagsInfoReq.getTagClass())){
            log.info("查询ar经纬度标签");
        }else{
            if (StringUtils.isEmpty(arTagsInfoReq.getArViewId())) {
                return Result.fail("AR实景ID不能为空!");
            }
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (0 == arTagsInfoReq.getIsTowerView()) {
            arTagsInfoReq.setTenantId(currentUser.getTenantId());
            arTagsInfoReq.setIndustryCode(currentUser.getIndustryCode());
        }
        arTagsInfoReq.setUserName(currentUser.getUserName());
        return Result.data(arTagsInfoService.listTree(arTagsInfoReq));
    }

    @ApiOperation(value = "查询AR标签列表", notes = "查询AR标签列表")
    @PostMapping("/arTagsInfo/list")
    @Override
    public Result<List<ArDeviceInfo>> list(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq) {
        log.info("查询AR标签列表入参:{}", arTagsInfoReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();

        if (arTagsInfoReq.getIsTowerView() == 0){
            arTagsInfoReq.setTenantId(currentUser.getTenantId());
            arTagsInfoReq.setIndustryCode(currentUser.getIndustryCode());
        }
        arTagsInfoReq.setUserName(currentUser.getUserName());

        List<ArDeviceInfo> arTagsInfoList = arTagsInfoService.queryAll(arTagsInfoReq);
        return Result.data(arTagsInfoList);
    }


    @ApiOperation(value = "查询AR标签列表", notes = "查询AR标签列表")
    @PostMapping("/arTagsInfo/pageList")
    @Override
    public Result<TableDataInfo> pageList(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq) {
        if (StringUtils.isBlank(arTagsInfoReq.getArViewId())) {
            return Result.fail("AR实景ID不能为空!");
        }

        if (StringUtils.isBlank(arTagsInfoReq.getTagsName())) {
            return Result.fail("请输入查询的关键字!");
        }

        log.info("查询AR标签列表入参:{}", arTagsInfoReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();

        if (arTagsInfoReq.getIsTowerView() == 0){
            arTagsInfoReq.setTenantId(currentUser.getTenantId());
            arTagsInfoReq.setIndustryCode(currentUser.getIndustryCode());
        }

        PageHelper.startPage(arTagsInfoReq.getPageNum(), arTagsInfoReq.getPageSize());
        List<ArDeviceInfo> list = arTagsInfoService.queryAll(arTagsInfoReq);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal((new PageInfo(list)).getTotal());
        return Result.data(rspData);
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "删除AR标签", notes = "删除AR标签")
    @PostMapping("/arTagsInfo/delete")
    @Override
    public Result<String> delete(@Validated @RequestBody ArTagsInfoReq arTagsInfoReq) {
        log.info("删除AR标签入参:{}", arTagsInfoReq);
        QueryWrapper<ArCruisePlanRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArConstants.DELETE_FLAG, 0);
        queryWrapper.eq(ArConstants.TAG_ID, arTagsInfoReq.getId());
        List<ArCruisePlanRel> arCruisePlanRelList = arCruisePlanRelService.list(queryWrapper);
        if (arCruisePlanRelList.size() == 0){
            // 逻辑删除
            ArTagsInfo arTagsInfo = new ArTagsInfo();
            arTagsInfo.setId(Long.valueOf(arTagsInfoReq.getId()));
            arTagsInfo.setDeleteFlag(1);
            arTagsInfoService.updateById(arTagsInfo);
            // 删除收藏标签
            arTagsInfoService.deleteMarks(arTagsInfoReq.getId());
            return Result.success("删除AR标签成功!");
        }else {
            return Result.fail("该AR标签绑定了巡航计划，删除AR标签失败!");
        }
    }

    @ApiOperation(value = "收藏标签", notes = "收藏标签")
    @PostMapping("/arTagsInfo/addMark")
    @Override
    public Result<String> addMark(ArTagsInfoReq arTagsInfoReq) {
        log.info("收藏标签入参:{}", arTagsInfoReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (StringUtils.isEmpty(arTagsInfoReq.getId())) {
            return Result.fail("标签ID不能为空!");
        }
        // 检查是否已经收藏
        int count = arTagsInfoService.countMark(arTagsInfoReq.getId(), currentUser.getUserName());
        if (count > 0) {
            return Result.fail("已经收藏过了!");
        }
        arTagsInfoService.addMark(arTagsInfoReq.getId(), currentUser.getUserName());
        return Result.success("收藏成功!");
    }

    @ApiOperation(value = "批量收藏标签", notes = "批量收藏标签")
    @PostMapping("/arTagsInfo/addMarks")
    @Override
    public Result<String> addMarks(List<ArTagsInfoReq> arTagsInfoReqList) {
        log.info("批量收藏标签入参:{}", arTagsInfoReqList);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (arTagsInfoReqList == null || arTagsInfoReqList.size() <= 0) {
            return Result.fail("标签ID不能为空!");
        }
        // 查询已经收藏的标签
        List<String> arTagsInfoListMark = arTagsInfoService.listMark(currentUser.getUserName());
        // 需要收藏的标签
        List<String> arTagsInfoListMarkNew = new ArrayList<>();
        for (ArTagsInfoReq arTagsInfoReq : arTagsInfoReqList) {
            // 检查是否已经收藏,没收藏的放入arTagsInfoListMarkNew
            boolean flag = false;
            if (arTagsInfoListMark != null && arTagsInfoListMark.size() > 0) {
                for (String tagsId : arTagsInfoListMark) {
                    if (arTagsInfoReq.getId().equals(tagsId)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                arTagsInfoListMarkNew.add(String.valueOf(arTagsInfoReq.getId()));
            }
        }
        if (arTagsInfoListMarkNew.size() > 0) {
            arTagsInfoService.addMarks(arTagsInfoListMarkNew, currentUser.getUserName());
        }
        return Result.success("收藏成功!");
    }

    @ApiOperation(value = "取消收藏标签", notes = "取消收藏标签")
    @PostMapping("/arTagsInfo/delMark")
    @Override
    public Result<String> delMark(ArTagsInfoReq arTagsInfoReq) {
        log.info("取消收藏标签入参:{}", arTagsInfoReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (StringUtils.isEmpty(arTagsInfoReq.getId())) {
            return Result.fail("标签ID不能为空!");
        }
        // 检查是否已经收藏
        int count = arTagsInfoService.countMark(arTagsInfoReq.getId(), currentUser.getUserName());
        if (count <= 0) {
            return Result.fail("还未收藏!");
        }
        arTagsInfoService.delMark(arTagsInfoReq.getId(), currentUser.getUserName());
        return Result.success("取消收藏成功!");
    }

    @ApiOperation(value = "批量取消收藏标签", notes = "批量取消收藏标签")
    @PostMapping("/arTagsInfo/delMarks")
    @Override
    public Result<String> delMarks(List<ArTagsInfoReq> arTagsInfoReqList) {
        log.info("批量取消收藏标签入参:{}", arTagsInfoReqList);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (arTagsInfoReqList == null || arTagsInfoReqList.size() <= 0) {
            return Result.fail("标签ID不能为空!");
        }
        // 要删除的标签ID
        List<String> arTagsInfoListMark = new ArrayList<>();
        for (ArTagsInfoReq arTagsInfoReq : arTagsInfoReqList) {
            arTagsInfoListMark.add(String.valueOf(arTagsInfoReq.getId()));
        }
        arTagsInfoService.delMarks(arTagsInfoListMark, currentUser.getUserName());
        return Result.success("取消收藏成功!");
    }

    @ApiOperation(value = "查询摄像机所在区域天气及空气质量", notes = "查询摄像机所在区域天气及空气质量")
    @PostMapping("/arTagsInfo/weatherInfoList")
    @Override
    public Result weatherInfoList(@RequestBody ArDeviceDTO arDeviceDTO) {
        return arTagsInfoService.weatherInfoList(arDeviceDTO);
    }


    /**
     * 功能描述: 批量增加图标库接口
     *
     * @return: com.chinatower.common.entity.Result
     * @auther: LQ
     * @date: 2025/4/14
     * @version: 1.0
     */
    @PostMapping("/arTagsInfo/batchAddLabelIcon")
    @Override
    public Result<String> batchAddLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery) {
        return arTagsInfoService.batchAddLabelIcon(arLabelIconGallery);
    }

    /**
     * 功能描述: 查询图标库接口
     *
     * @return: com.chinatower.common.entity.Result
     * @auther: LQ
     * @date: 2025/4/14
     * @version: 1.0
     */
    @PostMapping("/arTagsInfo/getLabelIconList")
    @Override
    public Result<List<ArLabelIconGallery>> getLabelIconList(@RequestBody ArLabelIconGallery arLabelIconGallery) {
        return arTagsInfoService.getLabelIconList(arLabelIconGallery);
    }

    /**
     * 功能描述: 删除图标库接口
     *
     * @return: com.chinatower.common.entity.Result
     * @auther: LQ
     * @date: 2025/4/14
     * @version: 1.0
     */
    @PostMapping("/arTagsInfo/delLabelIcon")
    @Override
    public Result<String> delLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery) {
        return arTagsInfoService.delLabelIcon(arLabelIconGallery);
    }

    /**
     * 功能描述: 删除图标库接口
     *
     * @return: com.chinatower.common.entity.Result
     * @auther: LQ
     * @date: 2025/4/14
     * @version: 1.0
     */
    @PostMapping("/arTagsInfo/checkLabelIcon")
    @Override
    public Result<Boolean> checkLabelIcon(@RequestBody ArLabelIconGallery arLabelIconGallery) {
        return arTagsInfoService.checkLabelIcon(arLabelIconGallery);
    }


    /**
     * 小地图设备标签收藏
     * @param arTagsInfoReqList
     * @return
     */
    @PostMapping("/arTagsInfo/addMapTags")
    public Result<String> addMapTags(List<ArTagsInfoReq> arTagsInfoReqList) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 查询已经收藏的标签
        List<String> arTagsInfoListMark = arTagsInfoMapper.listMapTags(currentUser.getUserId());
        // 需要收藏的标签
        List<String> arTagsInfoListMarkNew = new ArrayList<>();
        List<String> arTagsInfoListMarkDef = new ArrayList<>();
        for (ArTagsInfoReq arTagsInfoReq : arTagsInfoReqList) {
            arTagsInfoListMarkDef.add(arTagsInfoReq.getDeviceCode());
            // 检查是否已经收藏,没收藏的放入arTagsInfoListMarkNew
            boolean flag = false;
            if (arTagsInfoListMark != null && arTagsInfoListMark.size() > 0) {
                for (String deviceCode : arTagsInfoListMark) {
                    if (arTagsInfoReq.getDeviceCode().equals(deviceCode)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                arTagsInfoListMarkNew.add(String.valueOf(arTagsInfoReq.getDeviceCode()));
            }
        }
        if (arTagsInfoListMarkNew.size() > 0) {
            arTagsInfoMapper.addMapTags(arTagsInfoListMarkNew, currentUser.getUserId(),arTagsInfoReqList.get(0).getTagsType().toString());
            this.dealCollectDevice(arTagsInfoListMarkDef, currentUser.getUserId(), 1, Constants.FIVE);

        }
        return Result.success("收藏成功!");
    }

    /**
     *
     * @param arTagsInfoReq
     * @return
     */
    @PostMapping("/arTagsInfo/listForCruise")
    @Override
    public Result<List<ArTagMoveRes>> listForCruise(ArTagFilterVO arTagFilterVO) {
        return Result.data(arTagsInfoService.listForCruise(arTagFilterVO));
    }
    /**
     * 小地图根据经纬度查询所有类型标签
     * @param tagFilterVO
     * @return
     */
    @Override
    public Result<List<ArTagMoveRes>> queryMapAllTags(ArTagFilterVO tagFilterVO) {
        return arTagsInfoService.queryMapAllTags(tagFilterVO);
    }

    /**
     * 小地图设备标签取消收藏
     * @param arTagsInfoReqList
     * @return
     */
    @PostMapping("/arTagsInfo/delMapTags")
    @Override
    public Result<String> delMapTags(List<ArTagsInfoReq> arTagsInfoReqList) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 要删除的标签ID
        List<String> arTagsInfoListMark = new ArrayList<>();
        for (ArTagsInfoReq arTagsInfoReq : arTagsInfoReqList) {
            arTagsInfoListMark.add(String.valueOf(arTagsInfoReq.getDeviceCode()));
        }
        arTagsInfoMapper.delMapTags(arTagsInfoListMark, currentUser.getUserId(),arTagsInfoReqList.get(0).getTagsType().toString());
        this.dealCollectDevice(arTagsInfoListMark, currentUser.getUserId(), Constants.ZERO_NUM, Constants.FIVE);
        return Result.success("取消收藏成功!");
    }

    @PostMapping("/arTagsInfo/queryTagLocation")
    @Override
    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public Result<List<TagLocationVO>> queryTagLocation(TagLocationVO arTagsInfoReq) {
        return Result.data(arTagsInfoMapper.queryTagLocation(arTagsInfoReq));
    }


    /**
     * 处理摄像机收藏
     */
    public void dealCollectDevice(List<String> collObjCode, String userId,int optType, String collType) {
        //收藏/取消收藏，单通道场景
        if(collObjCode.size() == Constants.ONE_NUM){
            // 根据通道编码查询设备下的通道
            List<ChannelInfoDO> chanInfoByChanCode = arTagsUserRelMapper.getChanInfoByChanCode(collObjCode.get(Constants.ZERO_NUM));
            if(!CollectionUtils.isEmpty(chanInfoByChanCode)){
                Set<String> channelList = chanInfoByChanCode.stream().map(ChannelInfoDO::getChannelCode).collect(Collectors.toSet());
                String deviceCode = chanInfoByChanCode.get(Constants.ZERO_NUM).getDeviceCode();
                this.addOrCancelCollectDevice(channelList, deviceCode, userId, optType, collType, Constants.SIX);
            }
        }
        //收藏/取消收藏，通道以上级别场景
        else{
            // 根据通道编码查询设备
            List<String> deviceList = arTagsUserRelMapper.getDeviceByChanCode(collObjCode);
            if(!CollectionUtils.isEmpty(deviceList)){
                this.addOrCancelCollectDeviceList(deviceList, userId, optType, collType);
            }
        }
    }

    /**
     * 收藏/取消收藏，通道以上级别场景
     * @param deviceList
     * @param userId
     * @param optType
     */
    public void addOrCancelCollectDeviceList(List<String> deviceList, String userId, int optType, String collType) {
        //add
        if( optType == DeviceConstants.ONE_NUM ){
            //新增设备收藏数据前，先查询insertDeviceList是否在数据库已存在，存在则不新增
            LambdaQueryWrapper<ArTagsTreeUserRel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ArTagsTreeUserRel::getCollObjCode,deviceList).
                    eq(ArTagsTreeUserRel::getCollObjType,collType).
                    eq(ArTagsTreeUserRel::getUserId,userId);
            List<ArTagsTreeUserRel> list = arTagsUserRelMapper.selectList(wrapper);
            Set<String> collDeviceList = list.stream().map(ArTagsTreeUserRel::getCollObjCode).collect(Collectors.toSet());

            List<ArTagsTreeUserRel> insertList = new ArrayList<>();
            for (String s : deviceList) {
                if(!collDeviceList.contains(s)){
                    ArTagsTreeUserRel deviceCollection = new ArTagsTreeUserRel();
                    deviceCollection.setUserId(userId);
                    deviceCollection.setCollObjCode(s);
                    deviceCollection.setCollObjType(collType);
                    insertList.add(deviceCollection);
                }
            }
            arTagsUserRelMapper.batchSave(insertList);
        }
        //remove
        else{
            LambdaQueryWrapper<ArTagsTreeUserRel> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ArTagsTreeUserRel::getCollObjCode,deviceList).
                    eq(ArTagsTreeUserRel::getCollObjType,collType).
                    eq(ArTagsTreeUserRel::getUserId,userId);
            arTagsUserRelMapper.delete(wrapper);
        }
    }

    /**
     * 处理收藏设备信息
     */
    public void addOrCancelCollectDevice(Set<String> channelList, String deviceCode, String userId, int optType, String collType, String queryCollType) {
        //构造通道编码集合

        //查询该用户是否收藏通道集合中的数据
        int chanCollInfo = arTagsUserRelMapper.getChanCollInfo(channelList, userId, queryCollType);

        //add
        if( optType == DeviceConstants.ONE_NUM ){
            //如果收藏数量=通道数量（通道均已收藏），则新增其设备的收藏数据
            if(chanCollInfo == channelList.size()){
                LambdaQueryWrapper<ArTagsTreeUserRel> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ArTagsTreeUserRel::getCollObjCode,deviceCode).
                        eq(ArTagsTreeUserRel::getCollObjType, collType).
                        eq(ArTagsTreeUserRel::getUserId,userId);
                List<ArTagsTreeUserRel> list = arTagsUserRelMapper.selectList(wrapper);
                if(list.size() == Constants.ZERO_NUM){
                    ArTagsTreeUserRel deviceCollection = new ArTagsTreeUserRel();
                    deviceCollection.setUserId(userId);
                    deviceCollection.setCollObjCode(deviceCode);
                    deviceCollection.setCollObjType(collType);
                    arTagsUserRelMapper.insert(deviceCollection);
                }
            }
        }
        //remove
        else{
            LambdaQueryWrapper<ArTagsTreeUserRel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ArTagsTreeUserRel::getCollObjCode,deviceCode).
                    eq(ArTagsTreeUserRel::getCollObjType,collType).
                    eq(ArTagsTreeUserRel::getUserId,userId);
            arTagsUserRelMapper.delete(wrapper);
        }
    }
}


