package com.chinatower.bdm.modules.layer.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.modules.inspectionRoute.entity.LayerInfoVO;
import com.chinatower.bdm.modules.layer.mapper.SatelliteLawConfigMapper;
import com.chinatower.bdm.modules.layer.service.SatelliteLawConfigService;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.SatelliteLawConfigVo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SatelliteLawConfigServiceImpl implements SatelliteLawConfigService {


    @Autowired
    private SatelliteLawConfigMapper satelliteLawConfigMapper;

    /**
     * 卫片执法配置列表查询
     * @param vo
     * @return
     */
    @Override
    public List<SatelliteLawConfigVo> getList(SatelliteLawConfigVo vo) {
        if (StringUtils.isNotEmpty(vo.getLayerTemplateName())){
            SatelliteLawConfigVo vos = new SatelliteLawConfigVo();
            vos.setLayerTemplateName(vo.getLayerTemplateName());
            List<String> layerTemplateId = satelliteLawConfigMapper.getLayerTemplateId(vos);
            if (CollectionUtils.isEmpty(layerTemplateId)){
                return new ArrayList<>();
            }
                vo.setLayerTemplateIdList(layerTemplateId);
        }
        Integer pageNum = vo.getPageNum();
        Integer pageSize = vo.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<SatelliteLawConfigVo> list = satelliteLawConfigMapper.getList(vo);
        List<String> typeList = list.stream().map(v -> v.getAlarmTypeId()).collect(Collectors.toList());
        List<String> templateList = list.stream().map(v -> v.getLayerTemplateId()).collect(Collectors.toList());
        //告警类型
        List<Map<String,String>> alarmTypeList = satelliteLawConfigMapper.getAlarmType(typeList);
        //告警模版
        List<Map<String, String>> layerTemplateList = satelliteLawConfigMapper.getLayerTemplate(templateList);
        Map<String, String> typeMap = alarmTypeList.stream().collect(Collectors.toMap(t -> t.get("typeId"), t -> t.get("typeName"), (o, n) -> n, HashMap::new));
        Map<String, String> templateMap = layerTemplateList.stream().collect(Collectors.toMap(t -> String.valueOf(t.get("templateId")), t -> String.valueOf(t.get("templateName")), (o, n) -> n, HashMap::new));
        for (SatelliteLawConfigVo satelliteLawConfigVo : list) {
            satelliteLawConfigVo.setAlarmTypeName(MapUtil.getStr(typeMap,satelliteLawConfigVo.getAlarmTypeId()));
            satelliteLawConfigVo.setLayerTemplateName(MapUtil.getStr(templateMap,satelliteLawConfigVo.getLayerTemplateId()));
        }
        return list;
    }

    /**
     * 查询图斑信息
     * @param vo
     * @return
     */
    @Override
    public List<LayerInfoVO> getLayerInfo(LayerInfoVO vo) {
        List<LayerInfoVO> list = satelliteLawConfigMapper.getLayerInfo(vo);
        return list;
    }

    /**
     * 卫片执法配置修改
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @DS("db1")
    public void updateConfig(SatelliteLawConfigVo vo) {
        if (StringUtils.isEmpty(vo.getId())){
            throw new CustomException("ID不能为空");
        }
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer count = satelliteLawConfigMapper.selectCustomNameCountNotId(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), vo.getCustomName(),vo.getId());
        if (count > 0) {
            throw new com.chinatower.platform.common.exception.CustomException("图层名称已存在，请重新输入！");
        }
        vo.setModifier(loginUser.getUser().getUserName());
        satelliteLawConfigMapper.updateConfig(vo);
    }

    /**
     * 卫片执法配置逻辑删除
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @DS("db1")
    public void deleteConfig(SatelliteLawConfigVo vo) {
        if (StringUtils.isEmpty(vo.getId())){
            throw new CustomException("ID不能为空");
        }
        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        vo.setModifier(loginUser.getUser().getUserName());
        satelliteLawConfigMapper.deleteConfig(vo);
    }

    /**
     * 卫片执法配置判断名称是否重复
     * @param vo
     * @return
     */
    @Override
    @DS("db1")
    public Result checkRepeatName(SatelliteLawConfigVo vo) {
        if (StringUtils.isEmpty(vo.getFileName())){
            throw new CustomException("文件名称不能为空");
        }
        SatelliteLawConfigVo byOne = satelliteLawConfigMapper.getConfigByOne(vo);
        if (ObjectUtils.isNotEmpty(byOne)) {
            return Result.fail(ResultCode.ERROR, "文件名称已存在");
        }
        return Result.success(ResultCode.SUCCESS);
    }
}
