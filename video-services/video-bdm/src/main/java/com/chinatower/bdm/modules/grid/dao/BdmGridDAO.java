package com.chinatower.bdm.modules.grid.dao;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.grid.entity.CommonRegionVO;
import com.chinatower.bdm.modules.grid.entity.GridUserVO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BdmGridDAO {

    List<GridListVO> getGridList(GridParam vo);

    List<GridListVO> getGridLayerList(GridLayerParam vo);

    int updateGridInfoSelective(GridVO vo);

    GridVO findOne(GridVO gridVO);

    CustOrganVO getOrganInfo(String orgId);

    List<GridVO> getGridTree(BaseDataScopeEntity vo);

    List<GridVO> getAllGridTree(BaseDataScopeEntity vo);

    List<GridVO> getGridTreeNew(GridVO vo);

    int insertGridInfo(GridVO vo);

    int insertGridInfoList(List<GridVO> list);

    int checkGridRef(GridVO gridVO);

    int deleteGridInfo(Long gridId);

    int checkChildrenGrid(GridVO gridVO);

    List<GridVO> getMessageByLngLat(GridByLngLatParam param);

    List<GridVO> getMessageByLngLatNew(GridByLngLatParam param);
    /**
     * 通过责任网格查询网格id
     * @param gridName
     * @return
     */
    String getGridIdByGridName(String gridName);

    /**
     * 通过责任网格查询网格id(增加客户id，行业编码过滤)
     * @param vo
     * @return
     */
    String getGridIdByGridNameNew(GridNoPageVO vo);

    int checkTwiceGirdName(GridVO vo);

    List<GridVO> getGridListByTenantAll(GridVO gridVO);

    List<GridVO> getGridListByTenantId(GridAllParam vo);

    List<GridVO> getGridListHasChildren(List<GridVO> list);

    List<GridVO> getGridNameByGridId(GridVO gridVO);


//    List<Map> findFirstGrid(List<GridVO> list);

    SysTenantInfo getTenantInfo(GridVO gridVO);

    SysIndustryConf getIndustryCodeInfo(GridVO gridVO);

    int findFirstGrid(GridVO gridVO);

    List<GridVO> getPonitGridList(List<String> list);

    int updateGridAreaInfo(List<GridVO> list);

    GridVO getInfoBygridParentId(Long gridId);

    List<GridUserVO> getUserListByGridList(List<GridByLngLat> list);

    List<GridListVO> getPointGridList(GridNoPageVO gridVO);

    int updateGridBatch(List<GridVO> list);

    List<String> getBmGridList(List<String> list);

    List<CommonRegionVO> getDeviceInfoVOS(int limit,int offset);

    List<CommonRegionVO> getGridkeeperVOS(int limit,int offset);

    void updateAllTable(List<CommonRegionVO> list);

    GridVO screenFindGridInfo(GridInfoParam param);

    List<GridVO> findGridList(GridNoPageVO gridNoPageVO);
}
