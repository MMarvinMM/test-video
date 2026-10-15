package com.chinatower.file.mapper;

import com.chinatower.file.domain.SysConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysFileInfoMapper {

    /**
     * 查询参数配置信息
     *
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config);

    /**
     * 获取中心节点
     * @return
     */
    String getCenterColonyCode();

    List<String> getAllDownPlatformCode();

    List<String> getSeqByNameList(@Param("name") String name, @Param("list") List<String> list);
}
