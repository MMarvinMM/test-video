package com.chinatower.platform.service;

import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.export.SysConfigDto;
import com.chinatower.platform.entity.admin.SysDictType;
import com.chinatower.platform.entity.export.SysFileInfo;

import java.util.List;

/**
 * 功能说明: admin服务导出接口类,所有admin导出查询方法放在该接口中
 *
 * @author 李峥
 * @date 2021-12-03 16:44
 * @requestID
 */
public interface AdminExportService {

    List<SysArea> selectSysAreaList(SysArea sysArea);

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 导出参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfigDto> exportConfigList(SysConfigDto config);

}
