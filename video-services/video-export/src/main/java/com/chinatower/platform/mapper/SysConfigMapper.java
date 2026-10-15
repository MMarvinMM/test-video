package com.chinatower.platform.mapper;

import com.chinatower.platform.entity.export.SysConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: java类作用描述
 * @Author: luo_ji
 * @CreateDate: 2022/12/12 15:08
 * @Version: 1.0
 */
@Mapper
public interface SysConfigMapper {

    /**
     * 导出参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfigDto> exportConfigList(SysConfigDto config);

}
