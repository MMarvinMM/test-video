package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.entity.vo.AlarmTypeSecondRecogVO;
import com.chinatower.event.entity.vo.CombinationVo;
import com.chinatower.event.entity.vo.SecondRecogConfigVO;
import com.chinatower.event.mapper.AlarmTypeSecondRecogMapper;
import com.chinatower.event.mapper.SecondRecogConfigMapper;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.AlarmTypeSecondRecogService;
import com.chinatower.event.repository.SecondRecogConfigService;
import com.chinatower.event.vo.SecondRecogConfigPageVo;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class SecondRecogConfigServiceImpl implements SecondRecogConfigService {


    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private AlarmTypeSecondRecogService alarmTypeSecondRecogService;

    @Autowired
    private AlarmTypeSecondRecogMapper alarmTypeSecondRecogMapper;

    @Autowired
    private SysMapper sysMapper;

    /**
     * 二次识别配置列表查询
     * @param vo
     * @return
     */
    @Override
    public TableDataInfo findByCondition(SecondRecogConfigPageVo vo) {
        TableDataInfo tableDataInfo = new TableDataInfo();

        Integer pageNum = vo.getPageNum();
        Integer pageSize = vo.getPageSize();

        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }

        List<SecondRecogConfigVO> list = secondRecogConfigMapper.findByCondition(vo);

        // 获取总记录数，这里会触发一次额外的 count 查询
        PageInfo<SecondRecogConfigVO> pageInfo = new PageInfo<>(list);
        //设置数据和消息
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(pageInfo.getTotal());
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setCode(HttpStatus.SUCCESS);

        return tableDataInfo;
    }

    /**
     * 二次识别配置新增
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public Result<Object> saveSecondRecogConfig(SecondRecogConfigVO vo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = sysMapper.selectUserOnlyByUserId(loginUser.getUser().getUserId());
        //获取客户id
        String tenantId = loginUser.getUser().getTenantId();
        //获取行业应用
        String industryCode = loginUser.getUser().getIndustryCode();
        //获取行业应用版本
        String appVerCode = loginUser.getUser().getAppVerCode();
        //节点编码
        String platformCode = loginUser.getUser().getPlatformCode();

        SecondRecogConfigVO secondRecogConfigVO = secondRecogConfigMapper.secondRecogNameIsRepeat(tenantId, industryCode, vo.getSecondRecogName());
        //判断id是否为空 为空就新增 不为空就编辑
        if (StringUtils.isNull(vo.getId())){
            if (ObjectUtils.isNotEmpty(secondRecogConfigVO)){
                //说明二次识别配置名称重复 不可添加
                return Result.fail(ResultCode.ERROR,"二次识别配置名称重复,不可添加！");
            }
            vo.setCreator(user.getUserName());
            vo.setTenantId(tenantId);
            vo.setIndustryCode(industryCode);
            vo.setAppVerCode(appVerCode);
            vo.setPlatformCode(platformCode);
            //二次识别配置新增   表名：vm_second_recog_config_t
            secondRecogConfigMapper.SaveSecondRecogConfig(vo);
        }else{
            if (ObjectUtils.isEmpty(secondRecogConfigVO) || vo.getId().equals(secondRecogConfigVO.getId())){
                vo.setModifier(user.getUserName());
                //二次识别配置修改   表名：vm_second_recog_config_t
                secondRecogConfigMapper.findUpdateById(vo);
                //逻辑删除 表名：vm_alarm_type_second_recog
                alarmTypeSecondRecogMapper.deleteAlarmSecondRecogId(vo.getId(),user.getUserName());
            }else{
                //说明二次识别配置名称重复 不可保存
                return Result.fail(ResultCode.ERROR,"二次识别配置名称重复,不可保存！");
            }

        }

        List<String> strings = new ArrayList<>();

        //循环遍历告警类型多选集合 并赋值
        for (AlarmTypeSecondRecogVO secondRecogVO : vo.getAlarmTypeList()) {
            //赋值
            secondRecogVO.setAlarmSecondRecogId(vo.getId().toString());
            secondRecogVO.setDeleteFlag(0);
            secondRecogVO.setCreator(user.getUserName());
            secondRecogVO.setTenantId(vo.getTenantId());
            secondRecogVO.setIndustryCode(vo.getIndustryCode());
            secondRecogVO.setAppVerCode(vo.getAppVerCode());

            List<String> strings1 = new ArrayList<>();
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmSourceValue())){
                strings1.add(secondRecogVO.getAlarmSourceValue());
            }
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmTypeValue())){
                strings1.add(secondRecogVO.getAlarmTypeValue());
            }
            //拼装数据  告警来源值-告警类型编码   8-1979
            strings.add(String.join("-",strings1));
        }

        //获取前端传来的 （8-1979） 去数据库中查找 并返回一个list
        List<CombinationVo> list = secondRecogConfigMapper.getCombinedResults(strings,tenantId,industryCode);

        //如果list中包含前端传来的数据
        if (StringUtils.isNotEmpty(list)){
            //根据id逻辑删除二次识别配置告警类型  表：vm_alarm_type_second_recog
            alarmTypeSecondRecogMapper.deleteIds(list);
            //根据二次识别配置编码 批量删除       表：vm_second_recog_config_t
            secondRecogConfigMapper.updateDeleteFlag(list);
        }
        //二次识别配置告警类型批量插入 表名：vm_alarm_type_second_recog
        alarmTypeSecondRecogService.insertBatch(vo.getAlarmTypeList());
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 二次识别配置修改
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public Integer updateSecondRecogConfig(SecondRecogConfigVO vo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //获取当前用户名
        String userName = loginUser.getUser().getUserName();

        vo.setModifier(userName);
        //二次识别配置修改   表名：vm_second_recog_config_t
        Integer integer = secondRecogConfigMapper.findUpdateById(vo);
        //逻辑删除 表名：vm_alarm_type_second_recog
//        alarmTypeSecondRecogMapper.deleteAlarmSecondRecogId(vo.getId());

        List<String> strings = new ArrayList<>();

        //循环遍历告警类型多选集合 并赋值
        for (AlarmTypeSecondRecogVO secondRecogVO : vo.getAlarmTypeList()) {
            secondRecogVO.setAlarmSecondRecogId(vo.getId().toString());
            secondRecogVO.setDeleteFlag(0);
            secondRecogVO.setModifier(userName);
            secondRecogVO.setTenantId(vo.getTenantId());
            secondRecogVO.setIndustryCode(vo.getIndustryCode());
            secondRecogVO.setAppVerCode(vo.getAppVerCode());

            List<String> strings1 = new ArrayList<>();
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmSourceValue())){
                strings1.add(secondRecogVO.getAlarmSourceValue());
            }
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmTypeValue())){
                strings1.add(secondRecogVO.getAlarmTypeValue());
            }
            strings.add(String.join("-",strings1));
        }

        List<CombinationVo> list = secondRecogConfigMapper.getCombinedResults(strings,vo.getTenantId(),vo.getIndustryCode());
        if (StringUtils.isNotEmpty(list)){
            alarmTypeSecondRecogMapper.deleteIds(list);
            secondRecogConfigMapper.updateDeleteFlag(list);
        }

        //二次识别配置告警类型批量插入 表名：vm_alarm_type_second_recog
        alarmTypeSecondRecogService.insertBatch(vo.getAlarmTypeList());
        return integer;
    }

    /**
     * 二次识别配置逻辑删除
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public Integer deleteSecondRecogConfig(SecondRecogConfigVO vo) {
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = sysMapper.selectUserOnlyByUserId(loginUser.getUser().getUserId());

        //根据id查找
        SecondRecogConfigVO vo1 = secondRecogConfigMapper.findSecondRecogConfigById(vo.getId());
        //逻辑删除  表名：vm_second_recog_config_t
        if (Objects.isNull(vo1)){
            return 0;
        }
        vo1.setModifier(user.getUserName());
        Integer integer = secondRecogConfigMapper.findUpdateDeleteFlagById(vo1);
        //逻辑删除  表名：vm_alarm_type_second_recog
        alarmTypeSecondRecogMapper.deleteAlarmSecondRecogId(vo1.getId(),user.getUserName());
        return integer;
    }


    /**
     * 默认名称：“二次识别配置名称+数字”（数字累加即可）
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @return
     */
    @Override
    public String getAutoConfigName(String tenantId,String industryCode) {
        return secondRecogConfigMapper.getAutoConfigName(tenantId,industryCode);
    }

    /**
     * 根据id查找对应的二次识别配置
     * @param id
     * @return
     */
    @Override
    public SecondRecogConfigVO findSecondRecogConfigById(Integer id) {
        if (StringUtils.isNull(id)){
            throw new CustomException("id为空");
        }
        SecondRecogConfigVO vo = secondRecogConfigMapper.findSecondRecogConfigById(id);
        List<AlarmTypeSecondRecogVO> list = alarmTypeSecondRecogMapper.findAllListByAlarmSecondRecogId(id);
        vo.setAlarmTypeList(list);
        return vo;
    }

    /**
     * 判断二次识别配置中告警来源X告警类型是否已有配置
     * @param vo
     * @return
     */
    @Override
    public boolean promptInformation(SecondRecogConfigVO vo) {
        List<String> strings = new ArrayList<>();

        //循环遍历告警类型多选集合 并赋值
        for (AlarmTypeSecondRecogVO secondRecogVO : vo.getAlarmTypeList()) {
            List<String> strings1 = new ArrayList<>();
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmSourceValue())){
                strings1.add(secondRecogVO.getAlarmSourceValue());
            }
            if (StringUtils.isNotEmpty(secondRecogVO.getAlarmTypeValue())){
                strings1.add(secondRecogVO.getAlarmTypeValue());
            }
            //拼装数据  告警来源值-告警类型编码   8-1979
            strings.add(String.join("-",strings1));
        }

        //获取前端传来的 （8-1979） 去数据库中查找 并返回一个list
        List<CombinationVo> list = secondRecogConfigMapper.getCombinedResults(strings,vo.getTenantId(),vo.getIndustryCode());
        if (StringUtils.isNotEmpty(list)){
            //当前选中的告警来源下的告警类型已有二次识别配置，是否需要覆盖原有配置？
            return true;
        }
        return false;
    }

    /**
     * 判断二次识别配置名称是否重复
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param secondRecogName 二次识别配置名称
     * @param id id
     * @return
     */
    @Override
    public Result<Object> secondRecogNameIsRepeat(String tenantId, String industryCode, String secondRecogName,Integer id) {
        SecondRecogConfigVO secondRecogConfigVO = secondRecogConfigMapper.secondRecogNameIsRepeat(tenantId, industryCode, secondRecogName);

        if (ObjectUtils.isNotEmpty(secondRecogConfigVO) && (id == null || !id.equals(secondRecogConfigVO.getId()))) {
            //说明二次识别配置名称重复
            return Result.fail(ResultCode.ERROR, "二次识别配置名称重复，请重新设置");
        }

        return Result.success(ResultCode.SUCCESS);
    }
}
