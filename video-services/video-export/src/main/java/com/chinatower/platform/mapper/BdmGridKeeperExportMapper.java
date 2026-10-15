package com.chinatower.platform.mapper;

import com.chinatower.bdm.api.entity.GridKeeperForExportViewVO;
import com.chinatower.bdm.api.entity.GridKeeperParam;
import com.chinatower.bdm.api.entity.GridListVO;
import com.chinatower.platform.dto.KeeperIdGridNameVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BdmGridKeeperExportMapper {


    List<GridKeeperForExportViewVO> getGridKeeperExportList(GridKeeperParam vo);


    /**
     * 查询网格下网格长
     * @param list
     * @return
     */
    List<GridListVO> getKeeperLeader(@Param("list") List<String> list, @Param("keeperId") Long keeperId, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 根据网格员ID查找对应的上级网格
     * @param keeperIds 网格员ID List
     * @return
     */
    List<KeeperIdGridNameVo> findKeeperIdByGridName (@Param("keeperIds") List<String> keeperIds);

}
