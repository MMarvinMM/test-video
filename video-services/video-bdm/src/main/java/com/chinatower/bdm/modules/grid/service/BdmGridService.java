package com.chinatower.bdm.modules.grid.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.CoordinateUtil;
import com.chinatower.bdm.common.NonceUtil;
import com.chinatower.bdm.modules.grid.dao.BdmGridDAO;
import com.chinatower.bdm.modules.grid.entity.CommonRegionVO;
import com.chinatower.bdm.modules.grid.entity.GridKeyWordVO;
import com.chinatower.bdm.modules.grid.entity.GridUserVO;
import com.chinatower.bdm.modules.gridKeeper.dao.BdmGridKeeperDAO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.TradeAlarmOrderDTO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.gis.api.GisAPI;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.DistrictsVO;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.constant.ImportConstants;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.system.api.content.SysConstants.ONE;
import static java.util.stream.Collectors.toMap;

@RefreshScope
@Service
@Slf4j
public class BdmGridService {
    @Resource
    private BdmGridDAO bdmGridDAO;
    @Resource
    private BdmGridKeeperDAO bdmGridKeeperDAO;
    @Autowired
    private GisAPI gisAPI;
    @Autowired
    private GisMapApi gisMapApi;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private IVideoManageRemoteService videoManageRemoteService;


    /**
     * 删除网格
     *
     * @param gridVO
     * @return Result
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public Result deleteGridInfo(GridVO gridVO) {
        if (StringUtils.isEmpty(gridVO.getGridId().toString())) {
            return Result.fail(Constants.GRID_NOT_NULL);
        }
        // 校验网格下是否有网格员
        if (bdmGridDAO.checkGridRef(gridVO) > 0) {
            return Result.fail(Constants.GRID_NOT_DEL);
        }

        //校验网格下是否有子网格
        if (bdmGridDAO.checkChildrenGrid(gridVO) > 0) {
            return Result.fail(Constants.GRID_HAS_CHILDREN);
        }

        return Result.condition(bdmGridDAO.deleteGridInfo(gridVO.getGridId()) > 0);
    }

    /**
     * 修改网格
     *
     * @param gridVO
     * @return Result
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public Result updateGridInfo(GridVO gridVO) {
        log.info("updateGridInfo入参:" + JSONObject.toJSON(gridVO));
        if (ObjectUtil.isEmpty(gridVO.getGridId())) {
            return Result.fail(Constants.GRID_NOT_NULL);
        }
        if (Constants.ONE.equals(gridVO.getPointStatus()) && !StringUtils.isEmpty(gridVO.getGridCenter())) {
            String gridCenter = gridVO.getGridCenter();
            if (!gridCenter.contains(Constants.LEFT_BRACKETS)) {
                gridCenter = Constants.LEFT_BRACKETS + gridVO.getGridCenter() + Constants.RIGHT_BRACKETS;
            }
            JSONArray gridCenterJsonArray = JSON.parseArray(gridCenter);
            PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
            req.setLocation(NullProcessUtil.nvlToString(gridCenterJsonArray.getJSONArray(0).get(0), "")+ Constants.COMMA +
                    NullProcessUtil.nvlToString(gridCenterJsonArray.getJSONArray(0).get(1), ""));
            log.info("逆地理编码查询接口入参："+JSONObject.toJSONString(req));
            Result<List<PoiContraryGeocodingQueryRes>> resArea = gisMapApi.poiContraryGeocodingQuery(req);
            log.info("逆地理编码查询接口返回："+JSONObject.toJSONString(resArea));
            if (ObjectUtil.isNotEmpty(resArea) && ResultCode.SUCCESS.getCode() == resArea.getCode()) {
                List<PoiContraryGeocodingQueryRes> list = resArea.getData();
                if(!CollectionUtils.isEmpty(list)){
                    PoiContraryGeocodingQueryRes resMap = list.get(0);
                    Region region = resMap.getRegion();
                    gridVO.setProvinceCode(region.getProvinceCode());
                    gridVO.setCityCode(region.getCityCode());
                    gridVO.setProvinceName(region.getProvince());
                    gridVO.setCityName(region.getCity());
                }
            }
            log.info("gridVO处理行政地址后:" + gridVO);
            //压缩网格
            String finalRange = gridCompress(gridVO);
            if(StringUtils.isNotEmpty(finalRange)){
                gridVO.setGridRange(finalRange);
            }
        }
        return Result.condition(bdmGridDAO.updateGridInfoSelective(gridVO) > 0);
    }

    /**
     * 保存网格
     *
     * @param gridVO
     * @return Result
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public Result addGridInfo(GridVO gridVO) {
        if (bdmGridDAO.checkTwiceGirdName(gridVO) > 0) {
            return Result.fail(Constants.CHECKTWICEGRIDNAME);
        } else {
            if (Constants.ZERO.equals(gridVO.getParentId().toString())) {
                return Result.fail(Constants.NO_ALLOW_ADD);
            }
            //根据父级gridId查询要添加的租户id和行业编码是否为父级的租户id和网格编码
            GridVO gridInfo = bdmGridDAO.getInfoBygridParentId(gridVO.getParentId());
            if (!ObjectUtil.isEmpty(gridInfo)) {
                if (!gridVO.getTenantId().equals(gridInfo.getTenantId()) || !gridVO.getIndustryCode().equals(gridInfo.getIndustryCode())) {
                    return Result.fail(Constants.NOT_EQUALS_TENANT);
                }
            } else {
                return Result.fail(Constants.NOT_PARENT_GRID);
            }
            if (!StringUtils.isEmpty(gridVO.getGridBeginTime())) {
                gridVO.setGridBeginTime(gridVO.getGridBeginTime() + DeviceConstants.START_TIME_SUFFIX);
            } else {
                gridVO.setGridBeginTime(null);
            }

            if (!StringUtils.isEmpty(gridVO.getGridEndTime())) {
                gridVO.setGridEndTime(gridVO.getGridEndTime() + DeviceConstants.END_TIME_SUFFIX);
            } else {
                gridVO.setGridEndTime(null);
            }

            String gridCode = gridVO.getGridCode();
            if (StringUtils.isEmpty(gridCode)) {
                gridCode = NonceUtil.getNonceStr();
            }
            gridVO.setGridCode(gridCode);
            gridVO.setGridId(Long.parseLong(gridCode));
            gridVO.setGridLevelCode(gridInfo.getGridLevelCode() + Constants.COMMA + gridVO.getGridCode());
            gridVO.setGridLevel(String.valueOf(Integer.valueOf(gridInfo.getGridLevel()) + 1));
//            getLevel(gridVO);
            gridVO.setGridPicture(StringUtils.isEmpty(gridVO.getGridPicture()) ? null : StringUtils.substringBefore(gridVO.getGridPicture(), "?"));
            gridVO.setPointStatus(ObjectUtil.isEmpty(gridVO.getPointStatus()) ? "0" : gridVO.getPointStatus());
            int result = bdmGridDAO.insertGridInfo(gridVO);

            // 同步大屏配置
            syncScreenDataConfig(gridVO);
            return Result.condition(result > 0);
        }
    }

    private void syncScreenDataConfig(GridVO gridVO){
        log.info("新增网格同步大屏配置入参："+JSONObject.toJSONString(gridVO));
        List<ScreenDataConfigVO> screenDataConfigVOList = new ArrayList<>();
        ScreenDataConfigVO configVO = new ScreenDataConfigVO();
        List<String> gridCodeList = new ArrayList<>();
        configVO.setTenantId(gridVO.getTenantId());
        configVO.setIndustryCode(gridVO.getIndustryCode());
        gridCodeList.add(gridVO.getGridId().toString());
        configVO.setGridCodeList(gridCodeList);
        screenDataConfigVOList.add(configVO);
        videoManageRemoteService.addConfig(screenDataConfigVOList);
    }

    /**
     * 新增最高级网格
     *
     * @param gridVOList
     * @return Result
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @Transactional(rollbackFor = Exception.class)
    public Result addFirstGrid(List<GridVO> gridVOList) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        // 根据tenantId和industryCode查询是否存在最高级网格 如果不存在，向数据库插入一条最高级网格数据
        for (GridVO gridVO : gridVOList) {
            //tenantId判空
            if (ObjectUtil.isEmpty(gridVO.getTenantId())) {
                return Result.fail(Constants.CHECK_IN_ARG_1);
            }
            //industryCode判空
            if (ObjectUtil.isEmpty(gridVO.getIndustryCode())) {
                return Result.fail(Constants.CHECK_IN_ARG_3);
            }
            //industryName判空
            if (ObjectUtil.isEmpty(gridVO.getIndustryName())) {
                return Result.fail(Constants.CHECK_IN_ARG_4);
            }
            //appVerCode判空
            if (ObjectUtil.isEmpty(gridVO.getAppVerCode())) {
                return Result.fail(Constants.CHECK_IN_ARG_5);
            }
            //gridEmCenter判空
//            if (ObjectUtil.isEmpty(gridVO.getGridEmCenter())) {
//                return Result.fail(Constants.CHECK_IN_ARG_6);
//            }
            //校验tenant_id
            SysTenantInfo sysTenantInfo = bdmGridDAO.getTenantInfo(gridVO);
            if (ObjectUtil.isEmpty(sysTenantInfo)) {
                return Result.fail(Constants.NOT_EXIST_TENANT_ID);
            }
            //校验industry_code
            gridVO.setPlatformCode(currentUser.getUser().getPlatformCode());
            SysIndustryConf industryCodeInfo = bdmGridDAO.getIndustryCodeInfo(gridVO);
            if (ObjectUtil.isEmpty(industryCodeInfo)) {
                return Result.fail(Constants.NOT_EXIST_INDUSTRY);
            }
            int i = bdmGridDAO.findFirstGrid(gridVO);
            if (i > 0) {
                return Result.fail(Constants.EXIST_FIRST_GRID);
            }
            gridVO.setCreator(currentUser.getUser().getUserId().toString());
            gridVO.setGridBeginTime(!StringUtils.isEmpty(gridVO.getGridBeginTime()) ? gridVO.getGridBeginTime() + DeviceConstants.START_TIME_SUFFIX : null);
            gridVO.setGridEndTime(!StringUtils.isEmpty(gridVO.getGridEndTime()) ? gridVO.getGridEndTime() + DeviceConstants.END_TIME_SUFFIX : null);
            String gridCode = gridVO.getGridCode();
            gridVO.setGridCode(StringUtils.isEmpty(gridCode) ? NonceUtil.getNonceStr() : gridCode);
            gridVO.setGridId(Long.parseLong(gridVO.getGridCode()));
            gridVO.setParentId(0L);
            gridVO.setParentCode(Constants.ZERO);
            //租户名-行业编码名-行业版本编码
            gridVO.setGridName(sysTenantInfo.getTenantName() + Constants.LINE_CENTER + gridVO.getIndustryName() + Constants.LINE_CENTER + gridVO.getAppVerCode());
            gridVO.setGridLevelCode(Constants.ONE_ZERO_ONE + Constants.COMMA + gridVO.getGridCode());
            gridVO.setGridLevel(Constants.ONE);
        }
        int result = bdmGridDAO.insertGridInfoList(gridVOList);
        return Result.condition(result > 0);
//        }
    }

    /**
     * 获取网络层级和网络层级编码
     *
     * @param gridVO
     * @return GridVO
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    private GridVO getLevel(GridVO gridVO) {
        //层级编码
        if (Constants.ZERO.equals(gridVO.getParentId().toString())) {
            // 父级是0，当前层级是第一层级
            gridVO.setGridLevelCode(Constants.ONE_ZERO_ONE + Constants.COMMA + gridVO.getGridCode());
            gridVO.setGridLevel(Constants.ONE);
        } else {
            //父级不是0，根据父级ID查询父级数据
            GridVO parentGridVo = new GridVO();
            parentGridVo.setGridId(gridVO.getParentId());
            GridVO parentVO = bdmGridDAO.findOne(parentGridVo);
            if (ObjectUtil.isEmpty(parentVO)) {
                //没有查询到父级数据，当成第一层级处理
                gridVO.setGridLevelCode(Constants.ONE_ZERO_ONE + Constants.COMMA + gridVO.getGridCode());
                gridVO.setGridLevel(Constants.ONE);
            } else {
                gridVO.setGridLevelCode(parentVO.getGridLevelCode() + Constants.COMMA + gridVO.getGridCode());
                gridVO.setGridLevel(String.valueOf(Integer.valueOf(parentVO.getGridLevel()) + 1));
            }
        }
        return gridVO;
    }

    /**
     * 查询网格树getGridTree
     */
    /**
     * 查询网格树getGridTree
     *
     * @return List<TreeSelect>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", gridColumn = "a.grid_id")
    public List<TreeSelect> getGridTree(BaseDataScopeEntity vo) {
        List<TreeSelect> tree = new ArrayList<>();
//        vo.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
//        vo.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        //根据组织编码查询组织树
        List<GridVO> list = new ArrayList<>();
        if (!Objects.isNull(vo.getDeleteFlag()) && vo.getDeleteFlag() == ONE) {
            list = bdmGridDAO.getAllGridTree(vo);
        } else {
            list = bdmGridDAO.getGridTree(vo);
        }
        for (GridVO item : list) {
            tree.add(new TreeSelect(item.getGridCode(), item.getGridName(), item.getParentCode(), item.getIsEnd()));
        }
        return TreeUtils.listToTreeSelect(tree);
    }


    /**
     * 查询当前用户网格树
     */
    /**
     * 查询当前用户网格树
     *
     * @return List<TreeSelect>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", gridColumn = "a.grid_id")
    public List<TreeSelect> getGridTreeCurUser(GridVO vo) {
        List<TreeSelect> tree = new ArrayList<>();
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){//当前登录用户不是网格员返回空
            return tree;
        }
        List<GridVO> gridVOList = bdmGridKeeperDAO.getGridByKeeper(keeper);
        if(!CollectionUtils.isEmpty(gridVOList)){
            List<Long> gridIdList = gridVOList.stream().map(GridVO::getGridId).collect(Collectors.toList());
            //根据组织编码查询组织树
            vo.setGridIdList(gridIdList);
            List<GridVO> list = bdmGridDAO.getGridTreeNew(vo);
            for (GridVO item : list) {
                tree.add(new TreeSelect(item.getGridCode(), item.getGridName(), item.getParentCode(), item.getIsEnd()));
            }
            return TreeUtils.listToTreeSelect(tree);
        }else{
            return tree;
        }
    }

    /**
     * 单条数据获取findGridInfo 根据一期功能开发
     *
     * @param gridId
     * @return Result<GridVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public Result<GridVO> findGridInfo(Long gridId) {
        GridVO gridVO = new GridVO();
        gridVO.setGridId(gridId);
        GridVO vo = bdmGridDAO.findOne(gridVO);
        if (ObjectUtil.isEmpty(vo)) {
            return Result.fail(Constants.NO_DATA);
        }
        //obs加密图片
        //add by liuxt video-file改造
        vo.setGridPicture(fileUrlEncryptUtil.dealObsFileUrl(vo.getGridPicture()));
        // 查询父级网格名称
        GridVO parentGridVO = new GridVO();
        parentGridVO.setGridId(vo.getParentId());
        GridVO parentVo = bdmGridDAO.findOne(parentGridVO);
        if (ObjectUtil.isNotEmpty(parentVo)) {
            vo.setParentName(parentVo.getGridName());
        }
        // 获取组织名称
        CustOrganVO dept = bdmGridDAO.getOrganInfo(vo.getGridEmCenter());
        if (ObjectUtil.isNotEmpty(dept)) {
            vo.setGridEmCenterName(dept.getDeptName());
        }
        List<String> gridList = new ArrayList<>();
        gridList.add(String.valueOf(vo.getGridId()));
        log.info("登录用户信息:{}", JSON.toJSONString(SecurityUtils.getLoginUser().getUser()));
        String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        List<GridListVO> keepers = bdmGridKeeperDAO.getKeeperLeader(gridList, null, tenantId, industryCode);
        if (!CollectionUtils.isEmpty(keepers)) {
            vo.setKeeperId(keepers.get(Constants.NUMBER_ZERO).getKeeperId());
            vo.setKeeperName(keepers.get(Constants.NUMBER_ZERO).getKeeperName());
            vo.setKeeperPhoneNumber(keepers.get(Constants.NUMBER_ZERO).getKeeperPhoneNumber());
            //add by liuxt video-file改造
            vo.setKeeperPhotoUrl(fileUrlEncryptUtil.dealObsFileUrl(keepers.get(Constants.NUMBER_ZERO).getKeeperPhotoUrl()));
            vo.setIsLine(keepers.get(Constants.NUMBER_ZERO).getIsLine());
            vo.setPhonePlainText(keepers.get(Constants.NUMBER_ZERO).getPhonePlainText());
        }
        //获取网格员名称，网格员电话
        List<GridKeeperBaseVO> gridKeeperlist = bdmGridKeeperDAO.getGridKeeperInfoByGridId(gridId.toString());
        vo.setGridKeeperlist(gridKeeperlist);
        //add by liuxt 获取网格员数量
        int keeperCount = gridKeeperlist.size();
        vo.setKeeperCount(keeperCount);
        //add by liuxt 获取今日告警数
        TradeAlarmOrderDTO dto = new TradeAlarmOrderDTO();
        dto.setGridId(String.valueOf(gridId));
        //获取当天日期
        Date date = new Date();//获取当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMATE_DAY);//定义日期格式
        String dateString = dateFormat.format(date);//将时间格式化为字符串
        dto.setAlarmTimeStart(DateUtils.string2Date(dateString + Constants.START_TIME_SUFFIX));
        dto.setAlarmTimeEnd(DateUtils.string2Date(dateString + Constants.END_TIME_SUFFIX));
        String todayAlarmNum = iTradeWarningOrderService.getOrderListCount(dto);
        vo.setTodayAlarmNum(todayAlarmNum);
        return Result.data(vo);
    }

    /**
     * 大屏单条数据获取
     *
     * @param param
     * @return List<GridListVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(gridColumn = "a.grid_id")
    public Result<GridVO> screenFindGridInfo(GridInfoParam param) {
        GridVO vo = bdmGridDAO.screenFindGridInfo(param);
        if (ObjectUtil.isEmpty(vo)) {
            return Result.fail(Constants.NO_DATA);
        }else{
            return findGridInfo(vo.getGridId());
        }
    }

    /**
     * 根据父级网格获取子集网格列表数据getGridList  根据一期功能开发
     *
     * @param gridVO
     * @return List<GridListVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public List<GridListVO> getGridList(GridParam gridVO) {
        List<GridListVO> list = bdmGridDAO.getGridList(gridVO);
        if (!CollectionUtils.isEmpty(list)) {
            List<GridListVO> keepers = bdmGridKeeperDAO.getKeeperLeader(getGridIds(JSONArray.parseArray(JSONArray.toJSONString(list))), null, gridVO.getTenantId(), gridVO.getIndustryCode());
            if (!CollectionUtils.isEmpty(keepers)) {
                for (GridListVO vo : list) {
                    keepers.forEach(item -> {
                        if (item.getGridId().equals(vo.getGridId())) {
                            vo.setKeeperId(item.getKeeperId());
                            vo.setKeeperName(item.getKeeperName());
                            vo.setKeeperPhoneNumber(item.getKeeperPhoneNumber());
                        }
                    });
                }
            }
        }
        return list;
    }


    /**
     * 获取和传参中gridId和哪层gridId相等的网格数据
     *
     * @param sonAll
     * @param gridId
     * @return Result<GridVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    private List<GridListVO> gridIdAll(List<GridListVO> sonAll, String gridId) {
        List<GridListVO> sonAll2 = sonAll.stream().filter(gridListVO -> gridId.equals(gridListVO.getParentId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonAll2)) {
            return null;
        }
        List<GridListVO> ret1 = new ArrayList<>();
        ret1.addAll(sonAll2);
        for (GridListVO son : sonAll2) {
            List<GridListVO> ret12 = gridIdAll(sonAll, son.getGridId());
            if (!CollectionUtils.isEmpty(ret12)) {
                ret1.addAll(ret12);
            }
        }
        return ret1;
    }

    /**
     * 网格图层数据获取
     *
     * @param gridLayerParam
     * @return Result<GridVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", gridColumn = "a.grid_id")
    public List<GridListVO> getGridLayerList(GridLayerParam gridLayerParam) {
        List<GridListVO> gridListVOList = bdmGridDAO.getGridLayerList(gridLayerParam);
        if (StringUtils.isNotEmpty(gridLayerParam.getCurrId())) {
            List<GridListVO> currList = gridListVOList.stream().filter(gridListVO -> gridListVO.getGridId().
                    equals(gridLayerParam.getCurrId())).collect(Collectors.toList());
            List<GridListVO> listVOList = gridIdAll(gridListVOList, gridLayerParam.getCurrId());
            if (!CollectionUtils.isEmpty(listVOList)) {
                currList.addAll(listVOList);
            }
            gridListVOList = currList;
        }

        if (!CollectionUtils.isEmpty(gridListVOList)) {
            List<GridListVO> keepers = bdmGridKeeperDAO.getKeeperLeader(
                    getGridIds(JSONArray.parseArray(JSONArray.toJSONString(gridListVOList))), null, SecurityUtils.getLoginUser().getUser().getTenantId(), SecurityUtils.getLoginUser().getUser().getIndustryCode());
            if (!CollectionUtils.isEmpty(keepers)) {
                for (GridListVO vo : gridListVOList) {
                    keepers.forEach(item -> {
                        if (item.getGridId().equals(vo.getGridId())) {
                            vo.setKeeperId(item.getKeeperId());
                            vo.setKeeperName(item.getKeeperName());
                            vo.setKeeperPhoneNumber(item.getKeeperPhoneNumber());
                        }
                    });
                }
            }
        }
        return gridListVOList;
    }


    /**
     * 转换数据类型
     *
     * @param json
     * @return List<String>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    private List<String> getGridIds(JSONArray json) {
        List<String> list = new ArrayList<>();
        for (Object object : json) {
            JSONObject obj = (JSONObject) object;
            String gridId = obj.getString(Constants.GRID_ID);
            list.add(gridId);
        }
        return list;
    }

    /**
     * 导入
     *
     * @param params
     * @return List<SysImportDetail>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public List<SysImportDetail> importData(ImportParams<GridExportVO> params) {

        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        SysUser loginUser = MapUtil.get(params.getExtraParams(), Constants.LOGIN_USER, SysUser.class);
        // 优化for循环查库操作
        List<String> gridVOList = new ArrayList<>();
        List<String> gridCodeList = params.getList().stream().filter(vo -> ObjectUtil.isNotEmpty(vo.getGridCode())).map(GridExportVO::getGridCode).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(gridCodeList)){
            gridVOList = bdmGridDAO.getBmGridList(gridCodeList);
        }
        BaseDataScopeEntity baseDataScopeEntity = new BaseDataScopeEntity();
        baseDataScopeEntity.setTenantId(loginUser.getTenantId());
        baseDataScopeEntity.setIndustryCode(loginUser.getIndustryCode());
        List<GridVO> list = bdmGridDAO.getGridTree(baseDataScopeEntity);
        for (GridExportVO vo : params.getList()) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(), rowNum);
            try {
                // 校验是否存在
                if (ObjectUtil.isNotEmpty(vo.getGridCode())) {
//                    GridVO gridVO1 = new GridVO();
//                    gridVO1.setGridId(Long.parseLong(vo.getGridCode()));
//                    GridVO gridVO = bdmGridDAO.findOne(gridVO1);
                    if (gridVOList.contains(vo.getGridCode())) {
                        detail.setFailReason(Constants.NET_CODE + vo.getGridCode() + ImportConstants.STR_E);
                        continue;
                    }
                }
                if (StringUtils.isEmpty(vo.getGridName())) {
                    detail.setFailReason(Constants.NET_NAME_NULL);
                    continue;
                }

                if (ObjectUtil.isEmpty(vo.getParentId())) {
                    detail.setFailReason(Constants.PARENT_NET_CODE_NULL);
                    continue;
                }

                boolean flag = true;
                String parentCode = null;
                for (GridVO x : list) {
                    if (vo.getParentId().equals(x.getGridId())) {
                        flag = false;
                        parentCode = x.getGridCode();
                        break;
                    }
                }
                if (flag) {
                    detail.setFailReason(Constants.PARENT_NET_NOT_FROM_OWN);
                    continue;
                }
                GridVO param = new GridVO();
                BeanUtils.copyProperties(vo, param);
                param.setParentCode(parentCode);
                param.setTenantId(loginUser.getTenantId());
                param.setIndustryCode(loginUser.getIndustryCode());
                param.setAppVerCode(loginUser.getAppVerCode());
                param.setCreator(loginUser.getUserId().toString());
                param.setIsEnd(Constants.ZERO);
                if (!StringUtils.isEmpty(vo.getGridSort())) {
                    param.setGridSort(Integer.valueOf(vo.getGridSort()));
                }
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMATE_DAY);
                if (!ObjectUtil.isEmpty(vo.getGridBeginTime())) {
                    param.setGridBeginTime(sdf.format(vo.getGridBeginTime()));
                }
                if (!ObjectUtil.isEmpty(vo.getGridEndTime())) {
                    param.setGridEndTime(sdf.format(vo.getGridEndTime()));
                }
                Result result = addGridInfo(param);
                if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                    detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);
                } else {
                    detail.setFailReason(result.getMsg());
                }
            } catch (Exception e) {
                detail.setFailReason("处理异常：" + ExceptionUtil.stacktraceToString(e, 1000));
                log.error(e.getMessage(), e);
            } finally {
                rowNum++;
                details.add(detail);
            }
        }
        return details;
    }

    /**
     * 根据经纬度获取网格
     *
     * @param param
     * @return List<GridByLngLat>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(gridColumn = "a.grid_id")
    public List<GridByLngLat> getMessageByLngLat(GridByLngLatParam param) {
        log.info("getMessageByLngLat入参:" + JSONObject.toJSON(param));
        String longitude = param.getLongitude();
        String latitude = param.getLatitude();
        List<String> tenantIdList = param.getTenantIdList();

        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            return null;
        }

        List<GridVO> list;
        //tenantIdList为空，说明是调用方是event，则取tenantList
        if (CollectionUtils.isEmpty(tenantIdList)) {
            List<BaseIndustryEntity> tenantList = param.getTenantList();
            if (!CollectionUtils.isEmpty(tenantList)) {
                tenantIdList = tenantList.stream().map(BaseIndustryEntity::getTenantId).collect(Collectors.toList());
                list = bdmGridDAO.getMessageByLngLatNew(param);
            } else {
                return null;
            }
        }
        //兼容旧逻辑
        else {
            list = bdmGridDAO.getMessageByLngLat(param);
        }

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<GridByLngLat> data = new ArrayList<>();
        for (String tenantId : tenantIdList) {
            for (GridVO gridVO : list) {
                if (tenantId.equals(gridVO.getTenantId())) {
                    if (!StringUtils.isEmpty(gridVO.getGridRange())) {
                        String gridRange = gridVO.getGridRange();
                        //处理一个网格标绘多个区域场景
                        gridRange = gridRange.replace(",[[", "-[");
                        String[] gridRangeArry = gridRange.split(Constants.LINE_CENTER);
//                        for (int i = 0; i < gridRangeArry.length; i++) {
//                            String range = gridRangeArry[i].replaceAll(Constants.CURLY_BRACES, "").replaceAll(Constants.ENCLOSED_CHECK, "");
//                            if (com.chinatower.common.core.utils.MapUtil.rayCasting(Double.valueOf(longitude), Double.valueOf(latitude), range)) {
//                                GridByLngLat gridByLngLat = new GridByLngLat();
//                                gridByLngLat.setGridId(gridVO.getGridId().toString());
//                                gridByLngLat.setGridName(gridVO.getGridName());
//                                gridByLngLat.setGridLevel(gridVO.getGridLevel());
//                                gridByLngLat.setIndustryCode(gridVO.getIndustryCode());
//                                gridByLngLat.setTenantId(tenantId);
//                                data.add(gridByLngLat);
//                                break;
//                            }
//                        }
                        //替换java JTS方法
                        for (int i = 0; i < gridRangeArry.length; i++) {
                            String range = gridRangeArry[i].replace(Constants.TWO_SQUARE_BRACKETS, Constants.VERTICAL_LINE).replace(Constants.LEFT_SQUARE_BRACKET, "").replace(Constants.RIGHT_SQUARE_BRACKET, "");                            Coordinate[] coordinateArray = CoordinateUtil.getCoordinateArray(range);
                            Coordinate coordinate = new Coordinate(Double.valueOf(longitude),  Double.valueOf(latitude));
                            if (CoordinateUtil.withinAndIntersects(coordinateArray, coordinate)) {
                                GridByLngLat gridByLngLat = new GridByLngLat();
                                gridByLngLat.setGridId(gridVO.getGridId().toString());
                                gridByLngLat.setGridName(gridVO.getGridName());
                                gridByLngLat.setGridLevel(gridVO.getGridLevel());
                                gridByLngLat.setIndustryCode(gridVO.getIndustryCode());
                                gridByLngLat.setTenantId(tenantId);
                                data.add(gridByLngLat);
                                break;
                            }
                        }
                    }
                }
            }
        }

        //查询网格下用户集合
        if (!CollectionUtils.isEmpty(data)) {
            List<GridUserVO> userListByGridList = bdmGridDAO.getUserListByGridList(data);
            data.stream().map(item -> {
                List<String> userList = new ArrayList<>();
                userListByGridList.stream().filter(area -> Objects.equals(item.getGridId(), area.getGridId())
                ).forEach(area -> {
                    userList.add(area.getUserId());
                });
                item.setUserList(userList);
                return item;
            }).collect(Collectors.toList());
        }
        log.info("getMessageByLngLat返回:" + JSONObject.toJSON(data));
        return data;
    }
    @DataScope(gridColumn = "a.grid_id")
    public List<GridByLngLat> getMessageByLngLatNew(GridByLngLatParam param) {
        log.info("getMessageByLngLatNew入参:" + JSONObject.toJSON(param));
        String longitude = param.getLongitude();
        String latitude = param.getLatitude();
        List<String> tenantIdList = param.getTenantIdList();

        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            return null;
        }

        List<GridVO> list = new ArrayList<>();
        //tenantIdList为空，说明是调用方是event，则取tenantList
        if (CollectionUtils.isEmpty(tenantIdList)) {
            List<BaseIndustryEntity> tenantList = param.getTenantList();
            if (!CollectionUtils.isEmpty(tenantList)) {
                list = bdmGridDAO.getMessageByLngLatNew(param);
            } else {
                return null;
            }
        }

        List<GridByLngLat> data = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            //将结果集根据tenantId分组
            Map<Object, List<GridVO>> gridMap = list.stream().filter(s->!StringUtils.isEmpty(s.getGridRange()))
                    .collect(Collectors.groupingBy(GridVO::getTenantId));

            gridMap.entrySet().forEach(entry->{
                //循环单个租户下的网格数据，按照gridLevel倒序排列，所以，如果匹配上了，就break外层循环
                List<GridVO> mapValue = entry.getValue();
                int matchCout = 0;
                int gridCout = 0;
                out:for (GridVO gridVO : mapValue) {
                    gridCout ++ ;
                    String gridRange = gridVO.getGridRange();
                    //处理一个网格标绘多个区域场景
                    String[] gridRangeArry = gridRange.replaceAll(",\\[\\[", "-\\[").split(Constants.LINE_CENTER);
//                    for (int i = 0; i < gridRangeArry.length; i++) {
//                        matchCout ++ ;
//                        String range = gridRangeArry[i].replaceAll(Constants.CURLY_BRACES, "").replaceAll(Constants.ENCLOSED_CHECK, "");
//                        if (com.chinatower.common.core.utils.MapUtil.rayCasting(Double.valueOf(longitude), Double.valueOf(latitude), range)) {
//                            GridByLngLat gridByLngLat = new GridByLngLat();
//                            gridByLngLat.setGridId(gridVO.getGridId().toString());
//                            gridByLngLat.setGridName(gridVO.getGridName());
//                            gridByLngLat.setGridLevel(gridVO.getGridLevel());
//                            gridByLngLat.setIndustryCode(gridVO.getIndustryCode());
//                            gridByLngLat.setTenantId(gridVO.getTenantId());
//                            data.add(gridByLngLat);
//                            break out;
//                        }
//                    }
                    //替换java JTS方法
                    for (int i = 0; i < gridRangeArry.length; i++) {
                        matchCout ++ ;
                        String range = gridRangeArry[i].replace(Constants.TWO_SQUARE_BRACKETS, Constants.VERTICAL_LINE).replace(Constants.LEFT_SQUARE_BRACKET, "").replace(Constants.RIGHT_SQUARE_BRACKET, "");
                        Coordinate[] coordinateArray = CoordinateUtil.getCoordinateArray(range);
                        Coordinate coordinate = new Coordinate(Double.valueOf(longitude),  Double.valueOf(latitude));
                        if (CoordinateUtil.withinAndIntersects(coordinateArray, coordinate)) {
                            GridByLngLat gridByLngLat = new GridByLngLat();
                            gridByLngLat.setGridId(gridVO.getGridId().toString());
                            gridByLngLat.setGridName(gridVO.getGridName());
                            gridByLngLat.setGridLevel(gridVO.getGridLevel());
                            gridByLngLat.setIndustryCode(gridVO.getIndustryCode());
                            gridByLngLat.setTenantId(gridVO.getTenantId());
                            data.add(gridByLngLat);
                            break;
                        }
                    }
                }
                log.info(entry.getKey() + "租户匹配网格次数："+gridCout);
                log.info(entry.getKey() + "租户匹配网格区域次数："+matchCout);
            });

            //查询网格下用户集合
            if (!CollectionUtils.isEmpty(data)) {
                List<GridUserVO> userListByGridList = bdmGridDAO.getUserListByGridList(data);
                if(!CollectionUtils.isEmpty(userListByGridList)){
                    data.stream().map(item -> {
                        List<String> userList = new ArrayList<>();
                        userListByGridList.stream().filter(area -> Objects.equals(item.getGridId(), area.getGridId())
                        ).forEach(area -> {
                            userList.add(area.getUserId());
                        });
                        item.setUserList(userList);
                        return item;
                    }).collect(Collectors.toList());
                }
            }
        }
        log.info("getMessageByLngLatNew返回:" + JSONObject.toJSON(data));
        return data;
    }

    /**
     * 网格全部数据获取
     *
     * @param gridAllParam
     * @return List<GridVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public List<GridVO> getGridListByTenantId(GridAllParam gridAllParam) {
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String isTenantAdmin = sysUser.getIsTenantAdmin();

        List<GridVO> list = new ArrayList<>();
        // 如果租户管理员
        if (VideoSystemEnums.IsOrNotType.IS.getIndex().equals(isTenantAdmin)) {
            GridVO gridVO = new GridVO();
            gridVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            gridVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
            list = bdmGridDAO.getGridListByTenantAll(gridVO);
        } else {
            if (StringUtils.isNotEmpty(gridAllParam.getDeptIds())) {
                list = bdmGridDAO.getGridListByTenantId(gridAllParam);
                if (gridAllParam.getIsChild() && !CollectionUtils.isEmpty(list)) {
                    return bdmGridDAO.getGridListHasChildren(list);
                }

            }
        }
        return list;
    }

    /**
     * 智慧乡镇根据网格id查网格名称
     *
     * @param list
     * @return List<GridVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public List<GridVO> getGridNameByGridId(List<Long> list) {
        List<GridVO> listResult;
        GridVO gridVO = new GridVO();
        gridVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        gridVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        if (list.isEmpty()) {
            listResult = bdmGridDAO.getGridListByTenantAll(gridVO);
        } else {
            gridVO.setGridIdList(list);
            listResult = bdmGridDAO.getGridNameByGridId(gridVO);
        }
        return listResult;
    }

    /**
     * 同步区域信息
     *
     * @return Result<String>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public Result syncAreaInfo(List<String> tenantIdList) {
        //查出已标绘的网格数据
        List<GridVO> gridVOList = bdmGridDAO.getPonitGridList(tenantIdList);
        if (!CollectionUtils.isEmpty(gridVOList)) {
            for (GridVO gridVO : gridVOList) {
                String gridCenter = gridVO.getGridCenter();
                if (!StringUtils.isEmpty(gridCenter) && !gridCenter.contains(Constants.LEFT_BRACKETS)) {
                    gridCenter = Constants.LEFT_BRACKETS + gridCenter + Constants.RIGHT_BRACKETS;
                }
                gridVO.setGridCenter(gridCenter);
                JSONArray gridCenterJsonArray = JSON.parseArray(gridCenter);
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(NullProcessUtil.nvlToString(gridCenterJsonArray.getJSONArray(0).get(0), "")+ Constants.COMMA +
                        NullProcessUtil.nvlToString(gridCenterJsonArray.getJSONArray(0).get(1), ""));
                log.info("逆地理编码查询接口入参："+JSONObject.toJSONString(req));
                Result<List<PoiContraryGeocodingQueryRes>> resArea = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("逆地理编码查询接口返回："+JSONObject.toJSONString(resArea));
                if (ObjectUtil.isNotEmpty(resArea) && ResultCode.SUCCESS.getCode() == resArea.getCode()) {
                    List<PoiContraryGeocodingQueryRes> list = resArea.getData();
                    if(!CollectionUtils.isEmpty(list)){
                        PoiContraryGeocodingQueryRes resMap = list.get(0);
                        Region region = resMap.getRegion();
                        gridVO.setProvinceCode(region.getProvinceCode());
                        gridVO.setCityCode(region.getCityCode());
                        gridVO.setProvinceName(region.getProvince());
                        gridVO.setCityName(region.getCity());
                    }
                }
            }
            bdmGridDAO.updateGridAreaInfo(gridVOList);
        }
        return Result.success(Constants.GOOD);
    }

    /**
     * @param gisKeyWordQueryReq 入参
     * @return com.chinatower.common.entity.Result
     * @Description: 地图检索
     * @Author: TangJinBo
     * @Date: 2022/12/13 17:48
     */
    public Result<List<GridKeyWordVO>> keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        // 地图检索
        Result result = gisAPI.keyWordQuery(gisKeyWordQueryReq);
        if (ResultCode.SUCCESS.getCode() != result.getCode()) {
            return Result.success(Constants.FAIL);
        }
        Object data = result.getData();
        if (Objects.isNull(data)) {
            return Result.success(Constants.NO_DATA);
        }
        try {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(data);
            List<GridKeyWordVO> list = JSONArray.parseArray(jsonObject.getString("result"), GridKeyWordVO.class);
            // 封装查询参数
            List<String> queryList = this.getQueryAreaParam(list);
            if (CollectionUtils.isEmpty(queryList)) {
                return Result.success(Constants.NO_DATA);
            }
            // 根据区划名称查询区划code
            Result<List<SysArea>> areaResult = systemFeignClient.selectByAreaName(queryList);
            if (ResultCode.SUCCESS.getCode() != areaResult.getCode()) {
                return Result.success(Constants.FAIL);
            }
            List<SysArea> sysAreaList = areaResult.getData();
            if (CollectionUtils.isEmpty(sysAreaList)) {
                return Result.success(Constants.NO_DATA);
            }
            Map<String, SysArea> areaMap = sysAreaList.stream().collect(toMap(e -> e.getAreaName() + e.getAreaLevel(), Function.identity(), (v1, v2) -> v1));
            for (GridKeyWordVO gridKeyWordVO : list) {
                SysArea province = areaMap.get(gridKeyWordVO.getPname() + VideoSystemEnums.AreaLevel.PROVINCE.getIndex());
                gridKeyWordVO.setPcode(Objects.isNull(province) ? null : province.getAreaCode());
                SysArea city = areaMap.get(gridKeyWordVO.getCityname() + VideoSystemEnums.AreaLevel.CITY.getIndex());
                gridKeyWordVO.setCityCode(Objects.isNull(city) ? null : city.getAreaCode());
                SysArea county = areaMap.get(gridKeyWordVO.getAdname() + VideoSystemEnums.AreaLevel.COUNTY.getIndex());
                gridKeyWordVO.setAdCode(Objects.isNull(county) ? null : county.getAreaCode());
            }
            return Result.data(list);
        } catch (Exception e) {
            log.error("地图检索，解析json数据失败", e);
            return Result.success(Constants.NO_DATA);
        }
    }

    private List<String> getQueryAreaParam(List<GridKeyWordVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<String> provinceNames = list.stream().map(GridKeyWordVO::getPname).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        Set<String> cityNames = list.stream().map(GridKeyWordVO::getCityname).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        Set<String> adNames = list.stream().map(GridKeyWordVO::getAdname).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        List<String> result = Lists.newArrayList();
        result.addAll(provinceNames);
        result.addAll(cityNames);
        result.addAll(adNames);
        return result;
    }


    /**
     * @param gisKeyWordQueryReq 入参
     * @return com.chinatower.common.entity.Result
     * @Description: 行政区域查询
     * @Author: TangJinBo
     * @Date: 2022/12/13 17:48
     */
    public Result<List<DistrictsVO>> districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        List<DistrictsVO> list = new ArrayList<>();
        PoiAreaQueryReq req = new PoiAreaQueryReq();
        req.setAdCode(gisKeyWordQueryReq.getAdCode());
        req.setKeyWord(gisKeyWordQueryReq.getKeyWord());
        log.info("POI-行政查询接口入参："+JSONObject.toJSONString(req));
        Result<PoiAreaQueryRes> resArea = gisMapApi.poiAreaQuery(req);
        log.info("POI-行政查询接口返回："+JSONObject.toJSONString(resArea));
        if (ObjectUtil.isNotEmpty(resArea) && ResultCode.SUCCESS.getCode() == resArea.getCode()) {
            PoiAreaQueryRes poiAreaQueryRes = resArea.getData();
            if(!ObjectUtils.isEmpty(poiAreaQueryRes)){
                DistrictsVO districtsVO = new DistrictsVO();
                districtsVO.setCenter(poiAreaQueryRes.getLocation());
                districtsVO.setPolyline(poiAreaQueryRes.getPolygon());
                districtsVO.setProvince(poiAreaQueryRes.getProvince());
                districtsVO.setProvinceCode(poiAreaQueryRes.getProvinceCode());
                districtsVO.setCity(poiAreaQueryRes.getCity());
                districtsVO.setCitycode(poiAreaQueryRes.getCityCode());
                districtsVO.setCounty(poiAreaQueryRes.getCounty());
                districtsVO.setCountyCode(poiAreaQueryRes.getCountyCode());
                districtsVO.setTown(poiAreaQueryRes.getTown());
                districtsVO.setTownCode(poiAreaQueryRes.getTownCode());
                list.add(districtsVO);
            }
        }
        return Result.data(list);
    }

    /**
     * 网格压缩
     * @param gridVO
     * @return Result<GridVO>
     * @Author：lxt
     * @version release_v1.6.60
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public String gridCompress(GridVO gridVO) {
        String finalRange = "";
        try{
            String rangeStr = gridVO.getGridRange();//网格坐标
            JSONArray rangeJson = (JSONArray) JSONArray.parse(rangeStr);
            JSONArray newRangeArray = new JSONArray();
            for (int i = Constants.ZERO_NUM; i < rangeJson.size(); i++) {
                int pointSize = ((JSONArray) rangeJson.get(i)).size();
                if (pointSize >= Constants.THREE_NUM) {
                    JSONArray newPolygonJsonArray = new JSONArray();
                    newRangeArray.add(newPolygonJsonArray);
                    JSONArray newLineRingJsonArray = new JSONArray();
                    newPolygonJsonArray.add(newLineRingJsonArray);

                    JSONArray firstPoint = (JSONArray) ((JSONArray) rangeJson.get(i)).get(Constants.ZERO_NUM);
                    JSONArray lastPoint = (JSONArray) ((JSONArray) rangeJson.get(i)).get(pointSize - Constants.ONE_NUM);

                    if (firstPoint.get(Constants.ZERO_NUM).equals(lastPoint.get(Constants.ZERO_NUM)) && firstPoint.get(1).equals(lastPoint.get(1))) {
                        if (pointSize == Constants.THREE_NUM) {
                            // 因只有2个有效点，无法构成一个polygon故而舍弃该polygon
                            continue;
                        }
                    } else {
                        // 此处已处理数据首尾不一致的错误，数据标识改为true
                        ((JSONArray) rangeJson.get(i)).add(firstPoint.clone());
                    }
                    for (int j = Constants.ZERO_NUM; j < ((JSONArray) rangeJson.get(i)).size(); j++) {
                        JSONArray originPoint = (JSONArray) ((JSONArray) rangeJson.get(i)).get(j);
                        JSONArray newPoint = new JSONArray();
                        if (originPoint.size() > Constants.ONE_NUM) {
                            newPoint.add(originPoint.get(Constants.ZERO_NUM));
                            newPoint.add(originPoint.get(Constants.ONE_NUM));
                        } else {
                            continue;
                        }
                        newLineRingJsonArray.add(newPoint);
                    }
                } else {
                    // 此处已处理数据内部Polygon点数不足的错误，数据标识改为true
                    continue;
                }
            }
            log.info("newRangeArray:"+JSONObject.toJSONString(newRangeArray));
            //调pg数据库 对数据进行抽稀
            String newRangeStr =  "{ \"type\": \"MultiPolygon\", \"coordinates\": " + JSONObject.toJSONString(newRangeArray)  + "}";
            Result<String> result = gisMapApi.gridCompress(newRangeStr);
            log.info("压缩结果为:"+JSONObject.toJSONString(result));
            if(result.getCode() == Constants.CODE_SUCCESS ){
                String compressRangeStr = result.getData();
                cn.hutool.json.JSONObject a = new cn.hutool.json.JSONObject(compressRangeStr);
                cn.hutool.json.JSONArray returnRangeArray = a.getJSONArray("coordinates");
                cn.hutool.json.JSONArray finalRangeArray = new cn.hutool.json.JSONArray();
                for (int i = Constants.ZERO_NUM; i < returnRangeArray.size(); i++) {
                    cn.hutool.json.JSONArray newPolygonJsonArray = new cn.hutool.json.JSONArray();
                    finalRangeArray.add(newPolygonJsonArray);
                    cn.hutool.json.JSONArray newRangeArray1 = (cn.hutool.json.JSONArray) returnRangeArray.get(i);
                    cn.hutool.json.JSONArray newRangeArray2 = (cn.hutool.json.JSONArray) newRangeArray1.get(Constants.ZERO_NUM);
                    for (int j = Constants.ZERO_NUM; j < newRangeArray2.size(); j++) {
                        cn.hutool.json.JSONArray originPoint = (cn.hutool.json.JSONArray) newRangeArray2.get(j);
                        cn.hutool.json.JSONArray newPoint = new cn.hutool.json.JSONArray();
                        newPoint.add(originPoint.get(Constants.ZERO_NUM));
                        newPoint.add(originPoint.get(Constants.ONE_NUM));
                        newPolygonJsonArray.add(newPoint);
                    }
                }
                finalRange = JSONObject.toJSONString(finalRangeArray);
                log.info("finalRange:"+finalRange);
            }

        }catch(Exception e){
            log.error("压缩网格失败",e);
        }
        return finalRange;
    }



    /**
     * 获取要标绘的网格列表信息
     *
     * @param
     * @return List<GridListVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public List<GridListVO> getPointGridList(GridNoPageVO gridVO) {

        List<GridListVO> list = bdmGridDAO.getPointGridList(gridVO);
        return list;
    }


    /**
     * 更新网格范围
     *
     * @param
     * @return List<GridListVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public int updateGridRange(GridVO vo) {
        int i = bdmGridDAO.updateGridInfoSelective(vo);
        return i;
    }


    /**
     * 批量更新网格
     *
     * @param
     * @return List<GridListVO>
     * @Author：zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格管理
     */
    public int updateGridBatch(List<GridVO> list) {
        int i = bdmGridDAO.updateGridBatch(list);
        return i;
    }


    public String brushDataBasedWithLongitudeAndLatitude(GridParam param) {
        ExecutorService executor = Executors.newFixedThreadPool(200);
        List<CommonRegionVO> regionVOS = new ArrayList<>();
        //获取数据
        //需要查询的个数
        int limit = param.getPageSize();
        // 分页开始的数
        int offset = (param.getPageNum() - 1) * limit;
        List<CommonRegionVO> deviceInfoVOS = bdmGridDAO.getDeviceInfoVOS( limit, offset);
        List<CommonRegionVO> gridkeeperVOS = bdmGridDAO.getGridkeeperVOS( limit, offset);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceInfoVOS)) {
            regionVOS.addAll(deviceInfoVOS);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(gridkeeperVOS)) {
            regionVOS.addAll(gridkeeperVOS);
        }
        List<CommonRegionVO> commonRegionVOS = Collections.synchronizedList(new ArrayList<>());
        if (!regionVOS.isEmpty()) {
            ArrayList<CommonRegionVO> arrayList = new ArrayList<>(regionVOS.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(p -> p.getLongitude() + ";" + p.getLatitude()))), TreeSet::new)));
            List<Future<CommonRegionVO>> futures = new ArrayList<>();
            for (CommonRegionVO regionVO : arrayList) {
                futures.add(executor.submit(() -> setRegion(regionVO)));
            }
            for (Future<CommonRegionVO> future : futures) {
                try {
                    CommonRegionVO commonRegionVO = future.get();
                    if (commonRegionVO != null) {
                        commonRegionVOS.add(commonRegionVO);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.info("处理数据出现问题{}", e);
                    Thread.currentThread().interrupt(); // 保留中断状态
                }
            }
        }
        if (!commonRegionVOS.isEmpty()) {
            bdmGridDAO.updateAllTable(commonRegionVOS);
            return "处理" + commonRegionVOS.size() + "条数据";
        } else {
            return "没有数据需要处理";
        }
    }


    protected CommonRegionVO setRegion(CommonRegionVO regionVO) {
        String lon = lonAndLatFormat(Double.parseDouble(regionVO.getLongitude()), 9);
        String lat = lonAndLatFormat(Double.parseDouble(regionVO.getLatitude()), 9);
        Region region = getRegion(lon, lat);
        if (region != null) {
            regionVO.setLatitude(lat);
            regionVO.setLongitude(lon);
            regionVO.setProvinceCode(region.getProvinceCode());
            regionVO.setProvinceName(region.getProvince());
            regionVO.setCityCode(region.getCityCode());
            regionVO.setCityName(region.getCity());
            regionVO.setCountyCode(region.getCountyCode());
            regionVO.setCountyName(region.getCounty());
            regionVO.setTownCode(region.getTownCode());
            regionVO.setTownName(region.getTown());
            return regionVO;
        }
        return null;
    }

    /**
     * 处理经纬度信息，小数位超出9位则四舍五入，不够则补0
     *
     * @param value
     * @param scale
     * @return
     */
    private static String lonAndLatFormat(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value);
        // 如果小数位数不足9位，则补0
        if (bd.scale() < scale) {
            bd = bd.setScale(scale, RoundingMode.HALF_UP);
        }
        // 如果小数位数超出9位，则四舍五入
        return bd.setScale(scale, RoundingMode.HALF_UP).toString();

    }

    /**
     * 根据经纬度获取所属省市区
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public Region getRegion(String longitude, String latitude) {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        Region region = null;
        try {
            log.info("-根据经纬度获取所属省市区请求报文=={}" , JSON.toJSONString(req));
            Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
            log.info("-根据经纬度获取所属省市区响应报文=={}" , JSON.toJSONString(result));
            if (ObjectUtil.isNotEmpty(result) && ResultCode.SUCCESS.getCode() == result.getCode()) {
                List<PoiContraryGeocodingQueryRes> list = result.getData();
                if (!CollectionUtils.isEmpty(list)) {
                    PoiContraryGeocodingQueryRes resMap = list.get(0);
                    region = resMap.getRegion();
                }
            }
        } catch (Exception e) {
            log.error("===========ancientTreeGroupServiceImpl.getRegion error:{}", e);
        }
        return region;
    }




    /**
     * 根据网格名称查询当前客户，当前行业的网格列表
     */
    public Result<List<GridVO>> findGridList(GridNoPageVO gridNoPageVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridNoPageVO.setTenantId(user.getTenantId());
        gridNoPageVO.setIndustryCode(user.getIndustryCode());
        List<GridVO> list = bdmGridDAO.findGridList(gridNoPageVO);
        return Result.data(list);
    }

    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", gridColumn = "a.grid_id")
    public List<GridListVO> getGridListByRegion(GridLayerParam gridLayerParam) {
        return bdmGridDAO.getGridLayerList(gridLayerParam);
    }
}
