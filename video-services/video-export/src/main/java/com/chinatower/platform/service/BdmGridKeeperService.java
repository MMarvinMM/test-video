package com.chinatower.platform.service;

import com.chinatower.bdm.api.entity.GridKeeperForExportViewVO;
import com.chinatower.bdm.api.entity.GridKeeperParam;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.dto.KeeperIdGridNameVo;
import com.chinatower.platform.mapper.BdmGridKeeperExportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BdmGridKeeperService {
    @Resource
    private BdmGridKeeperExportMapper bdmGridKeeperDAO;


    /**
     * 功能描述：导出网格员
     * @Author：zl
     * @param gridKeeperVO
     * @return List<GridKeeperExportVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<GridKeeperForExportViewVO> getGridKeeperExportList(GridKeeperParam gridKeeperVO){
        return bdmGridKeeperDAO.getGridKeeperExportList(gridKeeperVO);
    }

    /**
     * 根据网格员ID查找对应的上级网格
     * @param keeperIds 网格员ID List
     * @return
     */
    public List<KeeperIdGridNameVo> findKeeperIdByGridName (List<String> keeperIds){
        return bdmGridKeeperDAO.findKeeperIdByGridName(keeperIds);
    }

}
