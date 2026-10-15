package com.chinatower.platform.mapper;


import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.bdm.api.entity.ResourceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能说明：资源管理mapper
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Mapper
public interface ResourceConfExportMapper {

    /**
     * 功能说明: 获取资源顶级列表(顶级结构)
     * @author zhaol
     * @param record 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID
     */
    List<ResourceVO> selectResourceExportList(ResourceConf record);
}