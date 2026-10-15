package com.chinatower.platform.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.export.SysConfigDto;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.entity.export.SysFileInfo;
import com.chinatower.platform.enums.TableEnum;
import com.chinatower.platform.mapper.SysAreaMapper;
import com.chinatower.platform.mapper.SysConfigMapper;
import com.chinatower.platform.mapper.SysDictTypeMapper;
import com.chinatower.platform.mapper.SysFileInfoExportMapper;
import com.chinatower.platform.service.AdminExportService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能说明: admin服务导出实现类,所有admin导出查询方法的实现放在该类中
 *
 * @author 李峥
 * @date 2021-12-03 16:44
 * @requestID
 */
@Service
@AllArgsConstructor
public class AdminExportServiceImpl implements AdminExportService {

    private SysAreaMapper sysAreaMapper;

    @Resource
    private SysFileInfoExportMapper sysFileInfoMapper;

    @Resource
    private SysDictTypeMapper dictTypeMapper;

    @Resource
    private SysConfigMapper configMapper;

    @DS(value = TableEnum.BASE)
    @Override
    public List<SysArea> selectSysAreaList(SysArea sysArea) {
        return sysAreaMapper.selectSysAreaList(sysArea);
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 导出参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfigDto> exportConfigList(SysConfigDto config)
    {
        return configMapper.exportConfigList(config);
    }



}




