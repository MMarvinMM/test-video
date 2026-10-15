package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.device.mapper.CheckpointDeviceInfoMapper;
import com.chinatower.device.service.IVideoCheckpointDeviceMgmtInfoService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.SysAreaBusinessCheckpointVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
@Service
@Slf4j
public class VideoCheckpointDeviceMgmtInfoService extends BaseController implements IVideoCheckpointDeviceMgmtInfoService {

    @Autowired(required = false)
    private CheckpointDeviceInfoMapper checkpointDeviceInfoMapper;

    /**
     * 功能描述：根据条件分页查询卡口设备信息  增加industryCode appVerCode
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    @DataScope(tenantColumn="m.tenant_id",industryCode="m.industry_code",deviceColumn = "d.device_code",alarmSourceVal = "4",acceptColumn="t.accept_node")
    public List<CheckpointDeviceInfoVO> queryCheckpointDeviceInfoPage(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointDeviceInfoVO.setTenantId(user.getTenantId());
        checkpointDeviceInfoVO.setIndustryCode(user.getIndustryCode());
        log.info("查询设备---请求报文------" + JSONObject.toJSONString(checkpointDeviceInfoVO));
        PageHelper.startPage(checkpointDeviceInfoVO.getPageNum(), checkpointDeviceInfoVO.getPageSize());
        return checkpointDeviceInfoMapper.queryCheckpointDeviceInfoList(checkpointDeviceInfoVO);
    }


    /**
     * 功能描述：根据条件不分页查询卡口设备信息
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    @DataScope(tenantColumn="m.tenant_id",industryCode="m.industry_code",deviceColumn = "d.device_code",alarmSourceVal = "4",acceptColumn="t.accept_node")
    public List<CheckpointDeviceInfoVO> queryCheckpointDeviceInfo(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointDeviceInfoVO.setTenantId(user.getTenantId());
        checkpointDeviceInfoVO.setIndustryCode(user.getIndustryCode());
        log.info("查询卡口设备信息---请求报文------" + JSONObject.toJSONString(checkpointDeviceInfoVO));
        return checkpointDeviceInfoMapper.queryCheckpointDeviceInfoList(checkpointDeviceInfoVO);
    }


    /**
     * 功能描述：新增卡口设备信息  增加industryCode appVerCode
     * @Author：zl
     * @param channelCodeList
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public int insertCheckpointDeviceInfo(List<String> channelCodeList) {
        int num = 0;
        List<CheckpointDeviceInfoVO> checkpointDeviceInfoVOList =  checkpointDeviceInfoMapper.selectDeviceInfo(channelCodeList);
        if(!CollectionUtils.isEmpty(checkpointDeviceInfoVOList)){
            for (CheckpointDeviceInfoVO vo : checkpointDeviceInfoVOList) {
                vo.setCreator(SecurityUtils.getOperator());
                vo.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
                vo.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
                vo.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
                vo.setTenantName(SecurityUtils.getLoginUser().getUser().getTenantInfo().getTenantName());
                vo.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            }
            log.info("新增卡口设备信息---请求接口报文------" + JSONObject.toJSONString(checkpointDeviceInfoVOList));
            num = checkpointDeviceInfoMapper.insertCheckpointDeviceInfo(checkpointDeviceInfoVOList);
        }else{
            num = -1;
            log.info("新增卡口设备信息失败,无对应设备通道");
        }
        return num;
    }

    /**
     * 功能描述：删除卡口设备
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public void deleteCheckpointDeviceInfoBatch(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        checkpointDeviceInfoVO.setDeleteFlag(DeviceConstants.DELETE_FLAG_EXCEPTION_INT);
        log.info("删除卡口设备---请求接口报文------" + JSONObject.toJSONString(checkpointDeviceInfoVO));
        checkpointDeviceInfoMapper.deleteCheckpointDeviceInfoBatch(checkpointDeviceInfoVO);
    }

    /**
     * 功能描述：查询租户设备通道树
     * @Author：zl
     * @return Result<List<TreeSelect>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    @DataScope(industryCode = "a.industry_code",tenantColumn = "a.tenant_id",deviceColumn = "d.device_code",alarmSourceVal = "4",acceptColumn="a.accept_node")
    public Result<List<TreeSelect>> getCheckpointDeviceTree(CheckpointDeviceInfoVO checkpointDeviceInfoVO) {
        List<TreeSelect> tree = new ArrayList<>();
        log.info("查询租户设备通道树---请求接口报文------" + JSONObject.toJSONString(checkpointDeviceInfoVO));
        long startMillis = Calendar.getInstance().getTimeInMillis();
        log.info(startMillis + "getCheckpointDeviceTree 开始时间"+ startMillis);
        List<SysAreaBusinessCheckpointVO> chanList = checkpointDeviceInfoMapper.getDeviceList(checkpointDeviceInfoVO);
        long startMillis2 = Calendar.getInstance().getTimeInMillis();
        log.info("getDeviceList 查询sql结束,耗时"+ (startMillis2 - startMillis) +"毫秒！");
        Set<SysAreaBusinessCheckpointVO> areaSet = new HashSet<>();
        List<String> areaCodes = chanList.stream().filter(Objects::nonNull)
                .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                .distinct().collect(Collectors.toList());
        checkpointDeviceInfoVO.setAreaCodeList(areaCodes);
        List<SysAreaBusinessCheckpointVO> arealist = checkpointDeviceInfoMapper.selectSysArea(checkpointDeviceInfoVO);
        long startMillis3 = Calendar.getInstance().getTimeInMillis();
        log.info("selectSysArea 查询sql结束,耗时"+ (startMillis3 - startMillis2) +"毫秒！");
        List<SysAreaBusinessCheckpointVO> removeArealist = new ArrayList<>();
        for (SysAreaBusinessCheckpointVO sysAreaBusinessCheckpointVO : arealist) {
            for (SysAreaBusinessCheckpointVO vo : chanList) {
                if(StringUtils.isEmpty(vo.getProvinceId())||StringUtils.isEmpty(vo.getCityId())||StringUtils.isEmpty(vo.getCountyId())){
                    removeArealist.add(vo);
                } else if (vo.getProvinceId().equals(sysAreaBusinessCheckpointVO.getAreaCode()) || vo.getCityId().equals(sysAreaBusinessCheckpointVO.getAreaCode()) || vo.getCountyId().equals(sysAreaBusinessCheckpointVO.getAreaCode())) {
                    areaSet.add(sysAreaBusinessCheckpointVO);
                }
            }
        }
        long startMillis4 = Calendar.getInstance().getTimeInMillis();
        log.info("遍历arealist结束,耗时"+ (startMillis4 - startMillis3) +"毫秒！");
        chanList.removeAll(removeArealist);
        chanList.addAll(areaSet);
        for (SysAreaBusinessCheckpointVO vo : chanList) {
            tree.add(new TreeSelect(vo.getAreaCode(), vo.getAreaName(), vo.getAreaParentCode(), vo.getAreaLevel()));
        }
        long startMillis5 = Calendar.getInstance().getTimeInMillis();
        log.info("构建新树,耗时"+ (startMillis5 - startMillis4) +"毫秒！");
        log.info("getCheckpointDeviceTree一共耗时"+ (startMillis5 - startMillis) +"毫秒！");
        return Result.data(TreeUtils.listToTreeSelect(tree));
    }




}
