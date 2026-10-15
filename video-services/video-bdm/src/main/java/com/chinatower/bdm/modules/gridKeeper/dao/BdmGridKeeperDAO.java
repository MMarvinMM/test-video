package com.chinatower.bdm.modules.gridKeeper.dao;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperParamVO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTTVO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridRefGridKeeperVO;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BdmGridKeeperDAO {

    Integer checkIdCard(GridKeeperVO vo);

    Integer checkPhone(GridKeeperVO vo);

    GridKeeperVO findGridKeeperInfo(GridKeeperVO gridKeeperVO);

    /**
     * 查询详情，不加密手机号和身份证，用于编辑
     * @param gridKeeperVO
     * @return
     */
    GridKeeperVO findGridKeeperInfoNoEncryption(GridKeeperVO gridKeeperVO);

    List<Map> findKeeperRef(GridKeeperVO keeper);

    int insertGridKeeperInfo(GridKeeperVO vo);

    int deleteGridKeeperRefInfo(GridKeeperVO gridKeeperVO);

    int deleteGridKeeperRefInfoBatch(@Param("list") List<Long> list);

    int insertGridKeeperRefInfo(List<GridKeeperRefVO> list);

    int updateGridKeeperInfo(GridKeeperVO vo);

    int deleteGridKeeperInfo(@Param("list") List<Long> list,@Param("userId") String userId);

    int openGridKeeperInfo(@Param("list") List<Long> list,@Param("userId") String userId);

    int stopGridKeeperInfo(@Param("list") List<Long> list,@Param("userId") String userId);

    int stopGridKeeperLocal(@Param("list") List<Long> list,@Param("userId") String userId);

    int openGridKeeperLocal(@Param("list") List<Long> list,@Param("userId") String userId);

    List<GridKeeperListVO> getGridKeeperList(GridKeeperParam vo);

    List<GridKeeperListVO> getGridKeeperListByFloor(GridKeeperLayerParam vo);

    List<GridKeeperListVO> getGridKeeperListByFloorNew(GridKeeperLayerParam vo);

    List<GridKeeperListByGridIdVO> getGridKeeperListByGridId(GridInfoParam gridVO);

    /**
     * 通过用户id、租户id、行业编码获取网格员id
     * @param gridKeeperVO
     * @return
     */
    GridKeeperVO getKeeperIdByUserId(GridKeeperVO gridKeeperVO);

    List<GridVO> getGridByKeeper(GridKeeperVO keeper);

    List<GridKeeperTTVO> getKeeperByPeriphery(KeeperPeripheryParam vo);

    GridKeeperVO getUserByLoginName(String loginName);

    /**
     * 查询网格下网格长
     * @param list
     * @return
     */
    List<GridListVO> getKeeperLeader(@Param("list")List<String> list,@Param("keeperId") Long keeperId,@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);

    /**
     * 查询网格下网格员数量
     * @param gridVO
     * @return
     */
    Integer getKeeperCountByGridId(GridVO gridVO);

    /**
     * 根据网格id查询网格员信息
     * @param gridId
     * @return
     */
    List<GridKeeperBaseVO> getGridKeeperInfoByGridId(@Param("gridId") String gridId);

    List<TreeSelectByBdm> getGridKeeperTree(KeeperTreeSelectParam param);

    List<TreeSelectByBdm> getGridKeeperTreeNew(KeeperTreeSelectParam param);

    List<TreeSelectByBdm> getGridKeeperLine(KeeperTreeSelectParam param);


    /**
     * 通过userId、行业编码查询网络员信息
     * @param userId tenantId
     * @param tenantId
     * @param industryCode
     * @return
     */
    GridKeeperVO findKeeperByUserId(@Param("userId") String userId,@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);

    int closeKeeper(GridKeeperVO vo);


    List<GridKeeperVO> findGridKeeperAll(CommonFeignEntity param);

    List<GridKeeperVO> searchGridKeeper(GridKeeperSearchParam param);

    List<SysDictData> selectDictDataByType(String sysWeekDay);

    List<GridKeeperParamVO> CheckGridKeeperByUserGrid(GridKeeperParamVO gridKeeperParamVO);

    List<InspectionTaskVO> checkTask(List<Long> list);
    List<InspectionTaskVO> checkInspectionConf(List<Long> list);

    List<GridRefGridKeeperVO> getKeeperGridByKeeperIds(List<Long> list);

    List<InspectionTaskVO>  checkTaskByGrid(GridVO gridVO);

    List<GridKeeperParamVO> checkGridUser(GridKeeperParamVO gridKeeperParamVO);

    List<GridVO> getGridByTenantId(GridKeeperVO gridKeeperVO);

    List<GridKeeperListVO> getGridKeeperListByTenantId(List<String> list);

    int updateGridKeeperArea(List<GridKeeperListVO> list);

    GridKeeperVO selectGridKeeperById(@Param("industryCode") String industryCode,@Param("userId")String userId, @Param("tenantId") String tenantId);

    List<GridKeeperListVO> getKeeperListByLonLat(GridKeeperListVO gridKeeperListVO);
}
