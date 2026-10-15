package com.chinatower.bdm.modules.licensePlateLibrary.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.CheckpointVehicleTypeVO;
import com.chinatower.bdm.modules.licensePlateLibrary.mapper.CheckpointVehicleTypeMapper;
import com.chinatower.bdm.modules.licensePlateLibrary.service.CheckpointVehicleTypeService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinatower.common.entity.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.chinatower.common.core.utils.StringUtils.getDataTable;

@Service
@Slf4j
public class CheckpointVehicleTypeServicImpl implements CheckpointVehicleTypeService {


    @Autowired
    CheckpointVehicleTypeMapper checkpointVehicleTypeMapper;

    /**
     * 根据客户+行业查询车辆类型
     *
     * @return List<CheckpointVehicleTypeVO>
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public TableDataInfo getVehicleTypeList(CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleTypeVO.setTenantId(user.getTenantId());
        checkpointVehicleTypeVO.setIndustryCode(user.getIndustryCode());
        log.info("查询车辆类型---请求报文------" + JSONObject.toJSONString(checkpointVehicleTypeVO));
        if (checkpointVehicleTypeVO.getPageNum() != null && checkpointVehicleTypeVO.getPageSize() != null) {
            PageHelper.startPage(checkpointVehicleTypeVO.getPageNum(), checkpointVehicleTypeVO.getPageSize());
        }
        List<CheckpointVehicleTypeVO> vehicleTypeList = checkpointVehicleTypeMapper.getVehicleTypeList(checkpointVehicleTypeVO);
        return getDataTable(vehicleTypeList);
    }

    /**
     * 车辆类型新增接口
     *
     * @return string
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public Result<String> insertOrUpdateVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        log.info("车辆类型新增---请求报文------" + JSONObject.toJSONString(checkpointVehicleTypeVO));
        //判断车辆类型为空
        if (StringUtils.isEmpty(checkpointVehicleTypeVO.getVehicleName())) {
            return Result.fail("车辆类型不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleTypeVO.setTenantId(user.getTenantId());
        checkpointVehicleTypeVO.setIndustryCode(user.getIndustryCode());
        checkpointVehicleTypeVO.setAppVerCode(user.getAppVerCode());
        //效验车辆类型名称在该客户该行业应用中唯一
        int count = checkpointVehicleTypeMapper.checkVehicleType(checkpointVehicleTypeVO);
        if (count > 0) {
            return Result.fail("车辆类型已存在");
        }
        //以id判断是新增还是修改
        if (StringUtils.isNotEmpty(checkpointVehicleTypeVO.getId())) {
            checkpointVehicleTypeVO.setModifier(user.getUserName());
            checkpointVehicleTypeVO.setUpdateTime(LocalDateTime.now().toString());
            checkpointVehicleTypeMapper.updateVehicleType(checkpointVehicleTypeVO);
            return Result.success("车辆类型修改成功");
        } else {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            checkpointVehicleTypeVO.setVehicleType(uuid);
            checkpointVehicleTypeVO.setCreator(user.getUserName());
            checkpointVehicleTypeVO.setCreateTime(LocalDateTime.now().toString());
            checkpointVehicleTypeMapper.insertVehicleType(checkpointVehicleTypeVO);
            return Result.success("新增车辆类型成功");
        }
    }

    /**
     * 车辆类型删除接口
     *
     * @return string
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    @Override
    public Result<String> deleteVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO) {
        log.info("车辆类型删除---请求报文------" + JSONObject.toJSONString(checkpointVehicleTypeVO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        checkpointVehicleTypeVO.setModifier(user.getNickName());
        checkpointVehicleTypeVO.setUpdateTime(LocalDateTime.now().toString());
        checkpointVehicleTypeMapper.deleteVehicleType(checkpointVehicleTypeVO);
        return Result.success("删除车辆类型成功");
    }
}
