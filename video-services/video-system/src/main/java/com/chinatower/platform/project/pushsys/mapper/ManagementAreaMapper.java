package com.chinatower.platform.project.pushsys.mapper;


import com.chinatower.system.api.model.ManagementAreaVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName: ManagementAreaDao
 * @Description: 管理区域Dao
 * @Author: ZL
 * @Date: 2022-02-06 16:16
 **/
@Mapper
public interface ManagementAreaMapper {

    /**
     * 管理区域
     * @param managementAreaVO 管理区域信息
     * @return  List<RegionVO> 查询出的管理区域信息
     */
    public List<ManagementAreaVO> getManagementArea(ManagementAreaVO managementAreaVO);

    int uptManagementAreaInfoBatch(List<ManagementAreaVO> managementAreaVOList);

    int addManagementAreaInfoBatch(List<ManagementAreaVO> managementAreaVOList);


}
