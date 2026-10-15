package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.project.system.mapper.SysAlarmTypeTTViewMapper;
import com.chinatower.platform.project.system.domain.SysAlarmType;
import com.chinatower.platform.project.system.domain.vo.SysAlarmTypeVO;
import com.chinatower.platform.project.system.service.ISysAlarmTypeTTViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ISysAlarmTypeTTViewServiceImpl implements ISysAlarmTypeTTViewService {

    @Autowired
    private SysAlarmTypeTTViewMapper sysAlarmTypeTTViewMapper;

    /**
     * 查询所有的告警类型
     * @param sysAlarmTypeVO
     * @return
     */
    @Override
    public List<SysAlarmType> getAlarmsTypeList(SysAlarmTypeVO sysAlarmTypeVO) {
        List<SysAlarmType> alarmsTypeList = sysAlarmTypeTTViewMapper.getAlarmsTypeList(sysAlarmTypeVO);
        return alarmsTypeList;
    }

    /**
     * 新增告警类型
     * @param sysAlarmTypeVOList
     * @return
     */
    @Override
    public Result addAlarmsTypeList(List<SysAlarmTypeVO> sysAlarmTypeVOList) {
        SysAlarmTypeVO sysAlarmTypeVO1 = new SysAlarmTypeVO();
        List<SysAlarmType> alarmsTypeList = getAlarmsTypeList(sysAlarmTypeVO1);
        //判断数据库中有没有相同的告警类型名称
        List<SysAlarmTypeVO> repeatList = sysAlarmTypeVOList.stream().filter((sysAlarmTypeVO) -> alarmsTypeList.stream().anyMatch((alarmType) -> {
            return alarmType.getTypeName().equals(sysAlarmTypeVO.getTypeName());
        })).collect(Collectors.toList());
        if (repeatList.size()>0){
            return Result.fail("已存在相同的告警类型名称");
        }
        //新增
        Date date = new Date();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        sysAlarmTypeVOList.forEach((sysAlarmTypeVO)->{
            sysAlarmTypeVO.setCreator(loginUser.getUsername());
            sysAlarmTypeVO.setCreateTime(date);
        });
        sysAlarmTypeTTViewMapper.addAlarmsTypeList(sysAlarmTypeVOList);
        return Result.success("新增告警类型成功");
    }

    /**
     * 修改告警类型
     * @param sysAlarmTypeVO
     * @return
     */
    @Override
    public Result updateAlarmsType(SysAlarmTypeVO sysAlarmTypeVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        sysAlarmTypeVO.setModifier(loginUser.getUsername());
        sysAlarmTypeVO.setUpdateTime(new Date());
        Integer i = sysAlarmTypeTTViewMapper.updateAlarmsType(sysAlarmTypeVO);
        if (i >= 1){
           return Result.success("修改告警类型成功");
        }else {
           return Result.fail("修改告警类型失败");
        }
    }

    /**
     * 删除告警类型   0正常 1删除
     * @param sysAlarmTypeVO
     */
    @Override
    public void removeAlarmType(SysAlarmTypeVO sysAlarmTypeVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        sysAlarmTypeVO.setModifier(loginUser.getUsername());
        sysAlarmTypeVO.setUpdateTime(new Date());
        sysAlarmTypeTTViewMapper.removeAlarmType(sysAlarmTypeVO);
    }

    @Override
    public Result checkAlarmsName(SysAlarmTypeVO sysAlarmTypeVO) {
        int a = sysAlarmTypeTTViewMapper.checkAlarmsName(sysAlarmTypeVO);
        if (a > 0){
            return Result.fail("名称已存在");
        }else {
            return Result.success("名称不存在");
        }
    }
}
