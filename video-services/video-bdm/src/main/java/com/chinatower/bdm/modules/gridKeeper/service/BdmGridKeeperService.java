package com.chinatower.bdm.modules.gridKeeper.service;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.NonceUtil;
import com.chinatower.bdm.common.TreeByBdmUtils;
import com.chinatower.bdm.modules.bmattendance.service.BmAttendanceService;
import com.chinatower.bdm.modules.grid.dao.BdmGridDAO;
import com.chinatower.bdm.modules.gridKeeper.dao.BdmGridKeeperDAO;
import com.chinatower.bdm.modules.gridKeeper.dao.BmKeeperTrailDAO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperParamVO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTTVO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTrail;
import com.chinatower.bdm.modules.gridKeeper.entity.GridRefGridKeeperVO;
import com.chinatower.bdm.modules.inspection.service.BdmInspectionService;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.TownShipDictData;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class BdmGridKeeperService {
    @Resource
    private BdmGridKeeperDAO bdmGridKeeperDAO;
    @Resource
    private BdmGridDAO bdmGridDAO;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private BmKeeperTrailDAO bmKeeperTrailDAO;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private BdmInspectionService bdmInspectionService;
    @Resource
    private BmAttendanceService bmAttendanceService;

    @Autowired
    private IVideoManageRemoteService videoManageRemoteService;

    /**
     * 新增网格管理员
     * @Author：zl
     * @param gridKeeperVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @Transactional(rollbackFor = Exception.class)
    public Result addGridKeeperInfo(GridKeeperVO gridKeeperVO) {
        // 校验网格是否填写
        String[] gridIds = gridKeeperVO.getGridIds();
        if(gridIds.length == 0){
            return Result.fail(Constants.GRID_ID_NOT_NULL);
        }
        if(Constants.ONE.equals(gridKeeperVO.getKeeperType())){
            // 如果是网格长，判断所选网格下是否已存在网格长
            if(!CollectionUtils.isEmpty(bdmGridKeeperDAO.getKeeperLeader(Arrays.asList(gridKeeperVO.getGridIds()),gridKeeperVO.getKeeperId(),gridKeeperVO.getTenantId(),gridKeeperVO.getIndustryCode()))){
                return Result.fail(Constants.KEEPER_LEADER_EXIST);
            }
        }
        if(Constants.ONE.equals(gridKeeperVO.getKeeperType())){
            gridKeeperVO.setIsGridManager(Constants.ZERO);
        }
        //1、先获取本网格的网格id 关联 ref表 得到网格员 取出网格员id 2、
        GridKeeperVO vo = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(gridKeeperVO.getKeeperId())){
            if(ObjectUtil.isNotEmpty(vo)){
                //查询网格下有没有该网格员
                GridKeeperParamVO gridKeeperParamVO = new GridKeeperParamVO();
                gridKeeperParamVO.setKeeperId(vo.getKeeperId());
                gridKeeperParamVO.setGridIdList(Arrays.asList(gridKeeperVO.getGridIds()));
                List<GridKeeperParamVO> gk = bdmGridKeeperDAO.CheckGridKeeperByUserGrid(gridKeeperParamVO);
                //有网格员 不新增
                if (!CollectionUtils.isEmpty(gk)){
                    return Result.fail(Constants.USER_EXIST);
                }
                //gk为空 看userId想不想等 不相等新增
                if (!vo.getStaffId().equals(gridKeeperVO.getUserId())){
                    String keeperId = NonceUtil.getNonceStr();
                    gridKeeperVO.setKeeperId(Long.parseLong(keeperId));
                    gridKeeperVO.setKeeperCode(keeperId);
                    bdmGridKeeperDAO.insertGridKeeperInfo(gridKeeperVO);
                    // 同步大屏配置
                    syncScreenDataConfig(gridKeeperVO);
                }
            }else{
                //没有 可新增
                String keeperId = NonceUtil.getNonceStr();
                gridKeeperVO.setKeeperId(Long.parseLong(keeperId));
                gridKeeperVO.setKeeperCode(keeperId);
                bdmGridKeeperDAO.insertGridKeeperInfo(gridKeeperVO);
                // 同步大屏配置
                syncScreenDataConfig(gridKeeperVO);
            }
        }else{
            if(ObjectUtil.isNotEmpty(vo) && !vo.getKeeperId().equals(gridKeeperVO.getKeeperId())){
                return Result.fail(Constants.USER_EXIST);
            }
            //编辑
            gridKeeperVO.setModifier(gridKeeperVO.getCreator());
            GridKeeperVO oldGridKeeperVO = bdmGridKeeperDAO.findGridKeeperInfo(gridKeeperVO);
            bdmGridKeeperDAO.updateGridKeeperInfo(gridKeeperVO);
            //add by liuxt 判断网格员名称是否变化 若发生变化 同步更新巡查任务相关表
            if(!gridKeeperVO.getKeeperName().equals(oldGridKeeperVO.getKeeperName())){
                InspectionTaskVO inspectionTaskVO = new InspectionTaskVO();
                inspectionTaskVO.setKeeperId(gridKeeperVO.getKeeperId().toString());
                inspectionTaskVO.setKeeperName(gridKeeperVO.getKeeperName());
                log.info("同步更新巡查人的名称："+ JSONObject.toJSONString(inspectionTaskVO));
                bdmInspectionService.synchUpdateKeeperName(inspectionTaskVO);
            }
        }

        // 先删除本网格的关系表 保存bm_grid_keeper_ref
        gridKeeperVO.setGridIdList(Arrays.asList(gridKeeperVO.getGridIds()));
        bdmGridKeeperDAO.deleteGridKeeperRefInfo(gridKeeperVO);
        List <GridKeeperRefVO> listRef = new ArrayList<>();
        for (String x:gridIds) {
            GridKeeperRefVO gridKeeperRefVO = new GridKeeperRefVO();
            gridKeeperRefVO.setKeeperId(ObjectUtil.isEmpty(gridKeeperVO.getKeeperId())?vo.getKeeperId():gridKeeperVO.getKeeperId());
            String refId = NonceUtil.getNonceStr();
            gridKeeperRefVO.setRefId(Long.parseLong(refId));
            gridKeeperRefVO.setGridId(x);
            gridKeeperRefVO.setCreator(gridKeeperVO.getCreator());
            listRef.add(gridKeeperRefVO);
        }
        bdmGridKeeperDAO.insertGridKeeperRefInfo(listRef);

        try {
            //新增或编辑网格员，考勤签到处理，落表bm_attendance_line
            bmAttendanceService.insertAttendanceLine(gridKeeperVO.getKeeperId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(Constants.SUCCESS);
    }

    private void syncScreenDataConfig(GridKeeperVO gridKeeperVO){
        log.info("新增网格员同步大屏配置入参："+JSONObject.toJSONString(gridKeeperVO));
        String[] gridIds = gridKeeperVO.getGridIds();
        List<ScreenDataConfigVO> screenDataConfigVOList = new ArrayList<>();
        ScreenDataConfigVO configVO = new ScreenDataConfigVO();
        List<ScreenDataConfigParticipationVO> gridKeeperList = new ArrayList<>();
        configVO.setTenantId(gridKeeperVO.getTenantId());
        configVO.setIndustryCode(gridKeeperVO.getIndustryCode());
        for (String gridId:gridIds) {
            ScreenDataConfigParticipationVO participationVO = new ScreenDataConfigParticipationVO();
            participationVO.setSource(gridId);
            participationVO.setCode(gridKeeperVO.getKeeperId().toString());
            gridKeeperList.add(participationVO);
        }
        configVO.setGridKeeperList(gridKeeperList);
        screenDataConfigVOList.add(configVO);
        videoManageRemoteService.addConfig(screenDataConfigVOList);
    }


    /**
     * 网格员单条数据查询findGridKeeperInfo
     * @Author：zl
     * @param keeperId
     * @return Result<GridKeeperVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result<GridKeeperVO> findGridKeeperInfo(Long keeperId) {
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        gridKeeperVO.setKeeperId(keeperId);
        GridKeeperVO result= bdmGridKeeperDAO.findGridKeeperInfo(gridKeeperVO);
        if(ObjectUtil.isEmpty(result)){
            return Result.fail(Constants.NO_DATA);
        }
        result.setAesPhoneNumber(AESUtil.encrypt(result.getAesPhoneNumber()));
        List<Map> keeperRefs = bdmGridKeeperDAO.findKeeperRef(gridKeeperVO);
        GridKeeperVO res = formatGridKeeperInfo(result, keeperRefs);
        return Result.data(res);
    }

    /**
     * 处理网格数据，添加责任网格和名称
     * @Author：zl
     * @param result
     * @param keeperRefs
     * @return GridKeeperVO
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    private GridKeeperVO formatGridKeeperInfo(GridKeeperVO result,List<Map> keeperRefs){
        if(!CollectionUtils.isEmpty(keeperRefs)){
            String[] gridIds = new String[keeperRefs.size()];
            String[] gridNames = new String[keeperRefs.size()];
            for (int i=0; i<keeperRefs.size(); i++){
                gridIds[i]=NullProcessUtil.nvlToString(keeperRefs.get(i).get(Constants.GRID_ID),StringUtils.EMPTY);
                gridNames[i]= NullProcessUtil.nvlToString(keeperRefs.get(i).get(Constants.GRID_NAME),StringUtils.EMPTY);
            }
            result.setGridIds(gridIds);
            result.setGridNames(gridNames);
            if(gridNames.length>Constants.NUMBER_ZERO){
                result.setGridNameStr(String.join(Constants.COMMA, gridNames));
            }
        }
        return result;
    }


    /**
     * 网格员单条数据查询
     * @Author：zl
     * @param keeperId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result findGridKeeperInfoNoEncryption(Long keeperId){
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        gridKeeperVO.setKeeperId(keeperId);
        GridKeeperVO result= bdmGridKeeperDAO.findGridKeeperInfoNoEncryption(gridKeeperVO);
        if(ObjectUtil.isEmpty(result)){
            return Result.fail(Constants.NO_DATA);
        }
        result.setAesPhoneNumber(com.chinatower.platform.common.utils.security.AESUtil.encryptBase64(result.getAesPhoneNumber()));
        result.setAesKeeperIdCardNumber(com.chinatower.platform.common.utils.security.AESUtil.encryptBase64(result.getAesKeeperIdCardNumber()));
        List<Map> keeperRefs = bdmGridKeeperDAO.findKeeperRef(gridKeeperVO);
        GridKeeperVO res = formatGridKeeperInfo(result, keeperRefs);
        return Result.data(res);
    }

    /**
     * 网格员删除
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result deleteGridKeeperInfo(List<Long> keepers) {
        //判断名下是否有代办项
        List<InspectionTaskVO> inspectionTaskVOList = bdmGridKeeperDAO.checkTask(keepers);
        if (!CollectionUtils.isEmpty(inspectionTaskVOList)){
            if (inspectionTaskVOList.get(0).getSendFlag() == 0){
                return Result.fail(Constants.DEL_EXIT_TASK_NOT_SEND);
            } else {
                return Result.fail(Constants.DEL_EXIT_TASK_SEND);
            }
        }
        //存在未生成巡查任务的巡查计划
        List<InspectionTaskVO> inspectionConfList = bdmGridKeeperDAO.checkInspectionConf(keepers);
        if (!CollectionUtils.isEmpty(inspectionConfList)){
            return Result.fail(Constants.DEL_EXIT_TASK_NOT_SEND);
        }

        //add by liuxt 20230415 删除网格人员关系表
        // 先删除本网格的关系表 保存bm_grid_keeper_ref
        bdmGridKeeperDAO.deleteGridKeeperRefInfoBatch(keepers);

        return Result.condition(bdmGridKeeperDAO.deleteGridKeeperInfo(keepers, SecurityUtils.getLoginUser().getUser().getUserId()) > 0);
    }

    /**
     * 网格员启用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result openGridKeeperInfo(List<Long> keepers) {
        return Result.condition(bdmGridKeeperDAO.openGridKeeperInfo(keepers,SecurityUtils.getLoginUser().getUser().getUserId()) > 0);
    }

    /**
     * 网格员禁用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result stopGridKeeperInfo(List<Long> keepers) {
        //判断名下是否有代办项
        List<InspectionTaskVO> inspectionTaskVOList = bdmGridKeeperDAO.checkTask(keepers);
        if (!CollectionUtils.isEmpty(inspectionTaskVOList)){
            if (inspectionTaskVOList.get(0).getSendFlag() == 0){
                return Result.fail(Constants.STOP_EXIT_TASK_NOT_SEND);
            } else {
                return Result.fail(Constants.STOP_EXIT_TASK_SEND);
            }
        }
        //存在未生成巡查任务的巡查计划
        List<InspectionTaskVO> inspectionConfList = bdmGridKeeperDAO.checkInspectionConf(keepers);
        if (!CollectionUtils.isEmpty(inspectionConfList)){
            return Result.fail(Constants.STOP_EXIT_TASK_NOT_SEND);
        }
        return Result.condition(bdmGridKeeperDAO.stopGridKeeperInfo(keepers,SecurityUtils.getLoginUser().getUser().getUserId()) > 0);

    }

    /**
     * 网格员定位启用功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result openGridKeeperLocal(List<Long> keepers) {
        return Result.condition(bdmGridKeeperDAO.openGridKeeperLocal(keepers,SecurityUtils.getLoginUser().getUser().getUserId()) > 0);
    }

    /**
     * 网格员定位关闭功能
     * @Author：zl
     * @param keepers
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result stopGridKeeperLocal(List<Long> keepers) {
        return Result.condition(bdmGridKeeperDAO.stopGridKeeperLocal(keepers,SecurityUtils.getLoginUser().getUser().getUserId()) > 0);

    }

    /**
     * 网格员分页查询getGridKeeperList
     * @Author：zl
     * @param gridKeeperVO
     * @return List<GridKeeperListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<GridKeeperListVO> getGridKeeperList(GridKeeperParam gridKeeperVO) {
        List<GridKeeperListVO> list = bdmGridKeeperDAO.getGridKeeperList(gridKeeperVO);
        if(!CollectionUtils.isEmpty(list)){
            List<String> areaCodes = list.stream().filter(Objects::nonNull)
                    .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                    .distinct().collect(Collectors.toList());
            Result<Map<String, String>> areaRedisMapResult = systemFeignClient.getAreaNameCaffeineMap(areaCodes);
            if(areaRedisMapResult.getData()!=null){
                Map<String, String> areaRedisMap = areaRedisMapResult.getData();
                for(GridKeeperListVO vo : list){
                    StringBuilder address = new StringBuilder();
                    if(StringUtils.isNotEmpty(vo.getProvinceId())) {
                        address.append(MapUtils.getString(areaRedisMap,vo.getProvinceId(),StringUtils.EMPTY));
                    }
                    if(StringUtils.isNotEmpty(vo.getCityId())) {
                        address.append(MapUtils.getString(areaRedisMap,vo.getCityId(),StringUtils.EMPTY));
                    }
                    if(StringUtils.isNotEmpty(vo.getCountyId())) {
                        address.append(MapUtils.getString(areaRedisMap,vo.getCountyId(),StringUtils.EMPTY));
                    }
                    vo.setAddress(NullProcessUtil.nvlToString(address,StringUtils.EMPTY));
                }
            }
        }
        return list;
    }


    /**
     * 根据网格id查询所属网格员
     * @Author：zl
     * @param gridVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<GridKeeperListByGridIdVO> getGridKeeperListByGridId(GridInfoParam gridVO){
        return bdmGridKeeperDAO.getGridKeeperListByGridId(gridVO);
    }

    /**
     * 导入
     * @Author：zl
     * @param params
     * @return List<SysImportDetail>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public List<SysImportDetail> importData(ImportParams<GridKeeperExportVO> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        SysUser loginUser = MapUtil.get(params.getExtraParams(),Constants.LOGIN_USER, SysUser.class);

        //查询字典表 add by liuxt 20230307
        boolean flag1 = false;
        boolean flag2 = false;
        String[] dictTypes = {Constants.POLITICAL_STATUS,Constants.SYS_USER_SEX};
        Result<Map<String, List<TownShipDictData>>> sysResult = systemFeignClient.dictType(dictTypes);
        Map<String, List<TownShipDictData>> dictList = sysResult.getData();
        for(GridKeeperExportVO vo : params.getList()){
            SysImportDetail detail = new SysImportDetail(params.getImportId(),rowNum);
            String entryTime = null;
            String keeperBirthDate = null;
            try {
                if (!StringUtils.isEmpty(vo.getEntryTime())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date parse = sdf.parse(vo.getEntryTime());
                    entryTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parse);
                } else {
                    detail.setFailReason(Constants.ENTRY_TIME_NOT_NULL);
                    rowNum++;
                    details.add(detail);
                    continue;
                }
            } catch (Exception e) {
                detail.setFailReason("选用" + Constants.DATE_FORMAT_ERROR);
                rowNum++;
                details.add(detail);
                log.error(e.getMessage(), e);
                continue;
            }

            try {
                if (!StringUtils.isEmpty(vo.getKeeperBirthDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date parse = sdf.parse(vo.getKeeperBirthDate());
                    keeperBirthDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parse);
                }
            } catch (Exception e) {
                detail.setFailReason("出生" + Constants.DATE_FORMAT_ERROR);
                rowNum++;
                details.add(detail);
                log.error(e.getMessage(), e);
                continue;
            }
            try{
                if(StringUtils.isEmpty(vo.getGridName())){
                    detail.setFailReason(Constants.GRID_NAME_NOT_NULL);
                    continue;
                }
                if(StringUtils.isEmpty(vo.getKeeperName())){//姓名
                    detail.setFailReason(Constants.KEEPER_NAME_NOT_EXIST);
                    continue;
                }
                if(StringUtils.isEmpty(vo.getKeeperPhoneNumber())){//联系电话
                    detail.setFailReason(Constants.PHONE_NUMBER_NOT_EXIST);
                    continue;
                }
                if(StringUtils.isEmpty(vo.getKeeperType())){//网格员类型
                    detail.setFailReason(Constants.KEEPER_TYPE_NOT_EXIST);
                    continue;
                }
                if(StringUtils.isEmpty(vo.getLocationFlag())){//是否开启定位
                    detail.setFailReason(Constants.LOCATION_FLAG_NOT_EXIST);
                    continue;
                }
                if(StringUtils.isEmpty(vo.getKeeperBirthDate())){
                    vo.setKeeperBirthDate(null);
                }

                if (!CollectionUtils.isEmpty(dictList)) {
                    //校验政治面貌 如果非字典值 则置为空落表 add by liuxt 20230307
                    for (TownShipDictData sysDictData : dictList.get(Constants.POLITICAL_STATUS)) {
                        if(sysDictData.getDictValue().equals(vo.getParty())){
                            flag1 = true;
                            break;
                        }
                    }
                    if(!flag1){
                        vo.setParty(null);
                    }
                    //校验性别 如果非字典值 则置为空落表 add by liuxt 20230307
                    for (TownShipDictData sysDictData : dictList.get(Constants.SYS_USER_SEX)) {
                        if(sysDictData.getDictValue().equals(vo.getKeeperGender())){
                            flag2 = true;
                            break;
                        }
                    }
                    if(!flag2){
                        vo.setKeeperGender(null);
                    }
                }

//                String gridId = bdmGridDAO.getGridIdByGridName(vo.getGridName());
                GridNoPageVO gridNoPageVO = new GridNoPageVO();
                gridNoPageVO.setGridName(vo.getGridName());
                gridNoPageVO.setGridNameList(Arrays.asList(vo.getGridName().split(Constants.COMMA)));
                gridNoPageVO.setTenantId(loginUser.getTenantId());
                gridNoPageVO.setIndustryCode(loginUser.getIndustryCode());
                String gridId = bdmGridDAO.getGridIdByGridNameNew(gridNoPageVO);
                if(StringUtils.isEmpty(gridId)){
                    detail.setFailReason(Constants.GRID_NAME_NOT_EXIST);
                    continue;
                }
                String[] split = gridId.split(Constants.COMMA);
                if(split.length != vo.getGridName().split(Constants.COMMA).length){
                    detail.setFailReason(Constants.GRID_NAME_NOT_EXIST);
                    continue;
                }

                vo.setGridIds(split);
                GridKeeperVO gridKeeperVO = bdmGridKeeperDAO.getUserByLoginName(vo.getLoginName());
                if(ObjectUtil.isEmpty(gridKeeperVO)){
                    detail.setFailReason(Constants.LOGIN_NAME_NOT_EXIST);
                    continue;
                }
                if(!ObjectUtil.isEmpty(gridKeeperVO)){
                    gridKeeperVO.setStaffId(gridKeeperVO.getUserId());
                    if(!vo.getKeeperName().equals(gridKeeperVO.getKeeperName())){
                        detail.setFailReason(Constants.KEEPER_NAME_NOT_SAME);
                        continue;
                    }
                    if(!vo.getKeeperPhoneNumber().equals(gridKeeperVO.getKeeperPhoneNumber())){
                        detail.setFailReason(Constants.PHONE_NUMBER_NOT_SAME);
                        continue;
                    }
                    if(!ObjectUtil.isEmpty(vo.getKeeperGender())&&!ObjectUtil.isEmpty(gridKeeperVO.getKeeperGender())&&!vo.getKeeperGender().equals(gridKeeperVO.getKeeperGender())){
                        detail.setFailReason(Constants.SEX_NOT_SAME);
                        continue;
                    }
                    if(!ObjectUtil.isEmpty(vo.getKeeperEmail())&&!ObjectUtil.isEmpty(gridKeeperVO.getKeeperEmail())&&!vo.getKeeperEmail().equals(gridKeeperVO.getKeeperEmail())){
                        detail.setFailReason(Constants.EMAIL_NOT_SAME);
                        continue;
                    }
                }
                BeanUtils.copyProperties(vo,gridKeeperVO);
                gridKeeperVO.setEntryTime(entryTime);
                if(StringUtils.isNotEmpty(keeperBirthDate)){
                    gridKeeperVO.setKeeperBirthDate(keeperBirthDate);
                }
                gridKeeperVO.setTenantId(loginUser.getTenantId());
                gridKeeperVO.setCreator(loginUser.getUserId().toString());
                gridKeeperVO.setIndustryCode(loginUser.getIndustryCode());
                gridKeeperVO.setAppVerCode(loginUser.getAppVerCode());
                Result result = addGridKeeperInfo(gridKeeperVO);
                if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                    detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);
                } else {
                    detail.setFailReason(result.getMsg());
                }
            }catch (Exception e) {
                detail.setFailReason("处理异常："+ ExceptionUtil.stacktraceToString(e,1000));
                log.error(e.getMessage(), e);
            }finally {
                rowNum++;
                details.add(detail);
            }
        }
        return details;
    }

    /**
     * 根据网格员获取所属网格
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public Result<List<GridVO>> getGridByKeeper(GridKeeperVO gridKeeperVO){
        gridKeeperVO.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        gridKeeperVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        gridKeeperVO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return Result.fail(Constants.NOT_GRID_KEEPER);
        }
        return Result.data(bdmGridKeeperDAO.getGridByKeeper(keeper));
    }


    /**
     * 根据网格员获取所属网格(新)
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "b.keeper_id")
    public Result<List<GridVO>> getGridByKeeperNew(GridKeeperVO gridKeeperVO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return Result.data(new ArrayList<>());
        }
        keeper.setDataScope(gridKeeperVO.getDataScope());
        return Result.data(bdmGridKeeperDAO.getGridByKeeper(keeper));
    }

    /**
     * 根据租户获取所属网格
     * @Author：zl
     * @return Result<List<GridVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id")
    public Result<List<GridVO>> getGridByTenantId(GridKeeperVO gridKeeperVO){
        return Result.data(bdmGridKeeperDAO.getGridByTenantId(gridKeeperVO));
    }

    /**
     * 周边网格员搜索
     * @Author：zl
     * @param vo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridKeeperColumn = "a.keeper_id")
    public Result<List<GridKeeperTTVO>> getKeeperByPeriphery(KeeperPeripheryParam vo){
        List<GridKeeperTTVO> keeperByPeriphery = bdmGridKeeperDAO.getKeeperByPeriphery(vo);
        List<Long> keeperIds = new ArrayList<>();
        for (GridKeeperTTVO gridKeeperVO : keeperByPeriphery) {
            Long keeperId = gridKeeperVO.getKeeperId();
            keeperIds.add(keeperId);
        }
        List<GridRefGridKeeperVO> gridKeeperVOList = bdmGridKeeperDAO.getKeeperGridByKeeperIds(keeperIds);
        for (GridKeeperTTVO gridKeeperTTVO : keeperByPeriphery) {
            List<GridRefGridKeeperVO> gridKeeperList = new ArrayList<>();
            for (GridRefGridKeeperVO gridRefGridKeeperVO : gridKeeperVOList) {
                if (gridKeeperTTVO.getKeeperId().equals(gridRefGridKeeperVO.getKeeperId())){
                    gridKeeperList.add(gridRefGridKeeperVO);
                }
            }
            gridKeeperTTVO.setGridRefGridKeeperVOList(gridKeeperList);
        }
        return Result.data(keeperByPeriphery);
    }

    /**
     * 查询网格员树
     * @Author：zl
     * @param param
     * @return List<TreeSelectByBdm>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridKeeperColumn = "a.keeper_id")
    public List<TreeSelectByBdm> getGridKeeperTree(KeeperTreeSelectParam param) {
        List<TreeSelectByBdm> tree= new ArrayList<>();
        List<TreeSelectByBdm> list = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        if(Constants.ONE.equals(param.getType())){
            list = bdmGridKeeperDAO.getGridKeeperTree(param);
        }else if(Constants.TWO.equals(param.getType())){
            list=bdmGridKeeperDAO.getGridKeeperTreeNew(param);

        }
        List<TreeSelectByBdm> keeperList=bdmGridKeeperDAO.getGridKeeperLine(param);
        for(TreeSelectByBdm treeVO:list){
            int allTotal=Constants.NUMBER_ZERO;
            int lineTotal=Constants.NUMBER_ZERO;
            for(TreeSelectByBdm vo:keeperList){
               if(Constants.ZERO.equals(treeVO.getType())&&vo.getGridLevelCode().contains(treeVO.getId())&&Constants.ONE.equals(vo.getIsLine())){
                   allTotal=allTotal+Constants.NUMBER_ONE;
                   lineTotal=lineTotal+Constants.NUMBER_ONE;
               }else if(Constants.ZERO.equals(treeVO.getType())&&vo.getGridLevelCode().contains(treeVO.getId())&&Constants.ZERO.equals(vo.getIsLine())){
                   allTotal=allTotal+Constants.NUMBER_ONE;
               }
            }
            treeVO.setAllTotal(allTotal+"");
            treeVO.setLineTotal(lineTotal+"");
        }
        for(TreeSelectByBdm item: list){
            GridKeeperTree vo=new GridKeeperTree();
            vo.setId(item.getId());
            vo.setPid(item.getPid());
            vo.setLabel(item.getLabel());
            vo.setType(item.getType());
            vo.setAllTotal(item.getAllTotal());
            vo.setLineTotal(item.getLineTotal());
            vo.setIsLine(item.getIsLine());
            vo.setCenter(item.getCenter());
            vo.setTel(item.getTel());
            vo.setColor(item.getColor());
            vo.setUserId(item.getUserId());
            vo.setUserName(item.getUserName());
            vo.setLocationFlag(item.getLocationFlag());
            tree.add(new TreeSelectByBdm(vo));
        }
        return TreeByBdmUtils.listToTreeSelect(tree);
    }


    /**
     * 网格员分页查询getGridKeeperList
     * @Author：zl
     * @param gridKeeperLayerParam
     * @return List<GridKeeperListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "c.grid_id", gridKeeperColumn = "a.keeper_id")
    public List<GridKeeperListVO> getGridKeeperListByLayer(GridKeeperLayerParam gridKeeperLayerParam) {
        List<GridKeeperListVO> list = bdmGridKeeperDAO.getGridKeeperListByFloor(gridKeeperLayerParam);
        if(!CollectionUtils.isEmpty(list)){
            //为了不改变原有sql逻辑，此处分组处理gridId
            Map<String, String> keeperGridsMap = list.stream().collect(Collectors.groupingBy(GridKeeperListVO::getKeeperId, Collectors.mapping(GridKeeperListVO::getGridId, Collectors.joining(","))));
            List<String> areaCodes = list.stream().filter(Objects::nonNull)
                    .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                    .distinct().collect(Collectors.toList());
            Result<Map<String, String>> areaRedisMapResult = systemFeignClient.getAreaNameCaffeineMap(areaCodes);
            Map<String, String> areaRedisMap = Maps.newHashMap();
            if(areaRedisMapResult.getData()!=null){
                areaRedisMap = areaRedisMapResult.getData();
            }
            for (GridKeeperListVO vo : list) {
                StringBuilder address = new StringBuilder();
                if (StringUtils.isNotEmpty(vo.getProvinceId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getProvinceId(), StringUtils.EMPTY));
                }
                if (StringUtils.isNotEmpty(vo.getCityId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getCityId(), StringUtils.EMPTY));
                }
                if (StringUtils.isNotEmpty(vo.getCountyId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getCountyId(), StringUtils.EMPTY));
                }
                vo.setGridId(keeperGridsMap.get(vo.getKeeperId()));
                //增加aes加密手机号字段
                vo.setAesPhoneNumber(AESUtil.encrypt(vo.getAesPhoneNumber()));
                vo.setAddress(address.toString());
                //add by liuxt video-file改造
                vo.setKeeperPhotoUrl(fileUrlEncryptUtil.dealObsFileUrl(vo.getKeeperPhotoUrl()));
            }

        }
        return list;
    }



    /**
     * 网格员分页查询getGridKeeperList
     * @Author：zl
     * @param gridKeeperLayerParam
     * @return List<GridKeeperListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridKeeperColumn = "a.keeper_id")
    public List<GridKeeperListVO> getGridKeeperListByLayerNew(GridKeeperLayerParam gridKeeperLayerParam) {
        List<GridKeeperListVO> list = bdmGridKeeperDAO.getGridKeeperListByFloorNew(gridKeeperLayerParam);
        if(!CollectionUtils.isEmpty(list)){
            List<String> areaCodes = list.stream().filter(Objects::nonNull)
                    .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                            com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                    .distinct().collect(Collectors.toList());
            Result<Map<String, String>> areaRedisMapResult = systemFeignClient.getAreaNameCaffeineMap(areaCodes);
            Map<String, String> areaRedisMap = Maps.newHashMap();
            if(areaRedisMapResult.getData()!=null){
                areaRedisMap = areaRedisMapResult.getData();
            }
            for (GridKeeperListVO vo : list) {
                StringBuilder address = new StringBuilder();
                if (StringUtils.isNotEmpty(vo.getProvinceId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getProvinceId(), StringUtils.EMPTY));
                }
                if (StringUtils.isNotEmpty(vo.getCityId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getCityId(), StringUtils.EMPTY));
                }
                if (StringUtils.isNotEmpty(vo.getCountyId())) {
                    address.append(MapUtils.getString(areaRedisMap, vo.getCountyId(), StringUtils.EMPTY));
                }
                //增加aes加密手机号字段
                vo.setAesPhoneNumber(AESUtil.encrypt(vo.getAesPhoneNumber()));
                vo.setAddress(address.toString());
                //add by liuxt video-file改造
                vo.setKeeperPhotoUrl(fileUrlEncryptUtil.dealObsFileUrl(vo.getKeeperPhotoUrl()));
            }

        }
        return list;
    }



    /**
     * 网格员打点接口
     * @Author：zl
     * @param vo
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result saveLngLatByKeeper(SaveLngLatParam vo) {
        log.info("网格员打点接口参数 - SaveLngLatParam{}" , JSONObject.toJSONString(vo));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //铁塔切换至客户视角（网格员账号）：不记录该铁塔用户手机位置信息。
        if (SecurityUtils.isSwitch()) {
            return Result.fail(Constants.NOT_LOCATION);
        }

        //网格员打点接口--更新网格员信息共通接口
        updateKeeperCommon(vo, user.getIndustryCode());
        //网格员轨迹存储
        vo.setCreateTime(new Date());
        saveTrail(trailVOCommon(vo, user));
        return Result.success(Constants.SUCCESS);
    }


    /**
     * 网格员批量打点接口
     * @Author：zhaozj
     * @param paramList
     * @return Result
     * @version release_v1.6.35
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result saveLngLatByKeeperBatch(List<SaveLngLatParam> paramList) {
        log.info("网格员批量打点接口saveLngLatByKeeperBatch参数-SaveLngLatParam{}", JSONObject.toJSONString(paramList));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //如果入参集合为空，返回报错
        if (CollectionUtils.isEmpty(paramList)) {
            return Result.fail(Constants.FAIL);
        }
        //铁塔切换至客户视角（网格员账号）：不记录该铁塔用户手机位置信息。
        if (SecurityUtils.isSwitch()) {
            return Result.fail(Constants.NOT_LOCATION);
        }

        List<GridKeeperTrail> trailList = new ArrayList<>();
        for (int i = 0; i < paramList.size(); i++) {
            // 网格员轨迹存储
            trailList.add(trailVOCommon(paramList.get(i), user));
            //更新网格员信息，以最后一条数据为准；
            if (i == paramList.size() - 1) {
                //网格员打点接口--更新网格员信息共通接口
                updateKeeperCommon(paramList.get(i), user.getIndustryCode());
            }
        }
        //批量新增数据
        if (!CollectionUtils.isEmpty(trailList)) {
            bmKeeperTrailDAO.insertAll(trailList, Constants.GRID_KEEPER_TRAIL,MongoDBType.PRIMARY);
        }
        return Result.success(Constants.SUCCESS);
    }

    /**
     * 网格员打点接口--实体类对象赋值共通接口
     * @Author：zhaozj
     * @param vo
     * @return
     * @version release_v1.6.35
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public GridKeeperTrail trailVOCommon(SaveLngLatParam vo, SysUser user) {
        GridKeeperTrail gridKeeperTrail=new GridKeeperTrail();
        gridKeeperTrail.setKeeperId(vo.getKeeperId());
        gridKeeperTrail.setLatitude(vo.getLatitude());
        gridKeeperTrail.setLongitude(vo.getLongitude());
        gridKeeperTrail.setCreateTime(vo.getCreateTime());
        gridKeeperTrail.setTenantId(user.getTenantId());
        gridKeeperTrail.setAppVerCode(user.getAppVerCode());
        gridKeeperTrail.setIndustryCode(user.getIndustryCode());
        return gridKeeperTrail;
    }

    /**
     * 网格员打点接口--更新网格员信息共通接口
     * @Author：zhaozj
     * @param vo
     * @return
     * @version release_v1.6.35
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public void updateKeeperCommon(SaveLngLatParam vo, String industryCode) {
        GridKeeperVO gridKeeperVO=new GridKeeperVO();
        gridKeeperVO.setLatitude(vo.getLatitude());
        gridKeeperVO.setLongitude(vo.getLongitude());
        gridKeeperVO.setIsLine(Constants.ONE);
        gridKeeperVO.setKeeperId(Long.parseLong(vo.getKeeperId()));
        gridKeeperVO.setTakePointTime(DateUtils.getTime());
        gridKeeperVO.setIndustryCode(industryCode);
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(vo.getLongitude() + Constants.COMMA + vo.getLatitude());
        try {
            log.info(Constants.POI_CONTRARY_GEOCODE_PARAM+JSONObject.toJSONString(req));
            Result<List<PoiContraryGeocodingQueryRes>> resArea = gisMapApi.poiContraryGeocodingQuery(req);
            log.info(Constants.POI_CONTRARY_GEOCODE_RETURN+JSONObject.toJSONString(resArea));
            if (ObjectUtil.isNotEmpty(resArea) && ResultCode.SUCCESS.getCode() == resArea.getCode()) {
                List<PoiContraryGeocodingQueryRes> list = resArea.getData();
                if(!CollectionUtils.isEmpty(list)){
                    PoiContraryGeocodingQueryRes resMap = list.get(0);
                    Region region = resMap.getRegion();
                    gridKeeperVO.setProvinceId(region.getProvinceCode());
                    gridKeeperVO.setCityId(region.getCityCode());
                    gridKeeperVO.setCountyId(region.getCountyCode());
                    gridKeeperVO.setKeeperAddress(resMap.getAddress());
                }
            }
            log.info("updateGridKeeperInfo网格员批量打点接口参数 - gridKeeperVO{}",JSONObject.toJSONString(gridKeeperVO));
            bdmGridKeeperDAO.updateGridKeeperInfo(gridKeeperVO);
        } catch (Exception e) {
            log.error(Constants.SAVE_LNG_LAT_ERROR);
        }
    }


    /**
     * 网格员退出登录
     * @Author：zl
     * @param keeperId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result closeKeeper(Long keeperId) {
        GridKeeperVO gridKeeperVO=new GridKeeperVO();
        gridKeeperVO.setKeeperId(keeperId);
        bdmGridKeeperDAO.closeKeeper(gridKeeperVO);
        return Result.data(Constants.SUCCESS);
    }

    /**
     * 根据userId 获取网格员ID
     * @Author：zl
     * @param userId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result getKeeperByUserId(String userId) {
        GridKeeperVO vo= bdmGridKeeperDAO.findKeeperByUserId(userId,SecurityUtils.getLoginUser().getUser().getTenantId(),SecurityUtils.getLoginUser().getUser().getIndustryCode());
        List<Map> keeperRefs = bdmGridKeeperDAO.findKeeperRef(vo);
        GridKeeperVO result = formatGridKeeperInfo(vo, keeperRefs);
        return Result.data(result);
    }
    /**
     * 网格员轨迹存储
     * @Author：zl
     * @param vo
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result saveTrail(GridKeeperTrail vo) {
        try {
            bmKeeperTrailDAO.insert(vo, MongoDBType.PRIMARY);
        } catch (Exception e) {
            Result.fail(Constants.RES_TRAIL_FAIL+e.getMessage());
        }
        return  Result.data(Constants.RES_TRAIL_SUCCESS);
    }

    /**
     * 网格员轨迹获取
     * @Author：zl
     * @param queryTrailsParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result getKeeperTrail(QueryTrailsParam queryTrailsParam) {
        QueryEntity query=new QueryEntity(Constants.GRID_KEEPER_TRAIL,GridKeeperTrail.class);
        query.eq(Constants.KEEPER_ID,queryTrailsParam.getKeeperId());
//        query.eq(Constants.INDUSTRY_CODE,SecurityUtils.getLoginUser().getUser().getIndustryCode());
//        query.eq(Constants.TENANT_ID,SecurityUtils.getLoginUser().getUser().getTenantId());
        if(StringUtils.isNotEmpty(queryTrailsParam.getStartTime()) &&StringUtils.isNotEmpty(queryTrailsParam.getEndTime())){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATE);
            try {
                Date start = simpleDateFormat.parse(queryTrailsParam.getStartTime());
                Date end = simpleDateFormat.parse(queryTrailsParam.getEndTime());
                query.between(Constants.CREATE_TIME,start,end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        SortEntity sort=new SortEntity();
        sort.asc(Constants.CREATE_TIME);
        List<GridKeeperTrail> list=bmKeeperTrailDAO.findList(query,sort,MongoDBType.PRIMARY);


       return Result.data(list);
    }

    /**
     * 根据租户查询网格员列表sdk
     * @Author：zl
     * @param param
     * @return Result<List<GridKeeperVO>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridKeeperColumn = "a.keeper_id")
    public Result<List<GridKeeperVO>> findGridKeeperAll(CommonFeignEntity param) {
        List<GridKeeperVO> result= bdmGridKeeperDAO.findGridKeeperAll(param);
        result.stream().forEach(gridKeeperVO -> {
            if (StringUtils.isNotEmpty(gridKeeperVO.getAesPhoneNumber())) {
                gridKeeperVO.setAesPhoneNumber(AESUtil.encrypt(gridKeeperVO.getAesPhoneNumber()));
            }
        });
        if(CollectionUtils.isEmpty(result)){
            return Result.fail(Constants.NO_DATA);
        }
        return Result.data(result);
    }

    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridKeeperColumn = "a.keeper_id")
    public List<GridKeeperVO> searchGridKeeper(GridKeeperSearchParam param) {
        List<GridKeeperVO> list = bdmGridKeeperDAO.searchGridKeeper(param);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<Long> keeperIds = list.stream().map(GridKeeperBaseVO::getKeeperId).collect(Collectors.toList());
        List<GridRefGridKeeperVO> gridList = bdmGridKeeperDAO.getKeeperGridByKeeperIds(keeperIds);
        Map<Long, List<GridRefGridKeeperVO>> keeperGridMap = gridList.stream().collect(Collectors.groupingBy(GridRefGridKeeperVO::getKeeperId));
        for (GridKeeperVO vo : list) {
            vo.setAesPhoneNumber(AESUtil.encrypt(vo.getAesPhoneNumber()));
            List<GridRefGridKeeperVO> grids = keeperGridMap.get(vo.getKeeperId());
            if(!CollectionUtils.isEmpty(grids)){
                int size = grids.size();
                String[] gridIds = new String[size];
                String[] gridNames = new String[size];
                for (int i = 0; i < size; i++) {
                    gridIds[i] = NullProcessUtil.nvlToString(grids.get(i).getGridId(), StringUtils.EMPTY);
                    gridNames[i] = NullProcessUtil.nvlToString(grids.get(i).getGridName(), StringUtils.EMPTY);
                }
                vo.setGridIds(gridIds);
                vo.setGridNames(gridNames);
                if (gridNames.length > Constants.NUMBER_ZERO) {
                    vo.setGridNameStr(String.join(Constants.COMMA, gridNames));
                }
            }
        }
        return list;
    }

    /**
     * 判断名下是否有代办项
     * @Author：zl
     * @param gridVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result checkTaskByGrid(GridVO gridVO) {
        //判断名下是否有代办项
        List<Long> keepers = new ArrayList<>();
        String keeperId = gridVO.getKeeperId();
        if(StringUtils.isEmpty(keeperId)){
            return Result.fail(Constants.NOT_EMPTY_KEEPER_ID);
        }
        keepers.add(Long.valueOf(keeperId));
        List<InspectionTaskVO> inspectionTaskVOList = bdmGridKeeperDAO.checkTask(keepers);
        if (!CollectionUtils.isEmpty(inspectionTaskVOList)){
            if (inspectionTaskVOList.get(Constants.NUMBER_ZERO).getSendFlag() == Constants.NUMBER_ZERO){
                return Result.fail(Constants.EDIT_EXIT_TASK_NOT_SEND);
            } else {
                return Result.fail(Constants.EDIT_EXIT_TASK_SEND);
            }
        }
        //存在未生成巡查任务的巡查计划
        List<InspectionTaskVO> inspectionConfList = bdmGridKeeperDAO.checkInspectionConf(keepers);
        if (!CollectionUtils.isEmpty(inspectionConfList)){
            return Result.fail(Constants.EDIT_EXIT_TASK_NOT_SEND);
        }
        return Result.success(Constants.SUCCESS);
    }


    /**
     * 判断是否是网格员
     * @Author：zl
     * @param gridKeeperVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result checkGridUser(GridKeeperVO gridKeeperVO) {
        //判断是否是网格员
        GridKeeperVO vo = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if (ObjectUtil.isNotEmpty(vo)) {
            //查询网格下有没有该网格员
            GridKeeperParamVO gridKeeperParamVO = new GridKeeperParamVO();
            gridKeeperParamVO.setKeeperId(vo.getKeeperId());
            List<GridKeeperParamVO> list  = bdmGridKeeperDAO.checkGridUser(gridKeeperParamVO);
            //有网格员 不新增
            if (ObjectUtil.isNotEmpty(list)) {
                return Result.fail(Constants.USER_EXIST);
            }
        } else {
            return Result.success(Constants.SUCCESS);
        }
        return Result.success(Constants.SUCCESS);
    }

    /**
     * 同步区域信息
     * @Author：zl
     * @return Result<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    public Result syncAreaInfo(List<String> tenantIdList) {
        List<GridKeeperListVO> gridKeeperListVOList = bdmGridKeeperDAO.getGridKeeperListByTenantId(tenantIdList);
        if (!CollectionUtils.isEmpty(gridKeeperListVOList)){
            for (GridKeeperListVO gridKeeperListVO : gridKeeperListVOList) {
                String longitude = gridKeeperListVO.getLongitude();
                String latitude = gridKeeperListVO.getLatitude();
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(longitude+ Constants.COMMA + latitude);
                log.info("逆地理编码查询接口入参："+JSONObject.toJSONString(req));
                Result<List<PoiContraryGeocodingQueryRes>> resArea = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("逆地理编码查询接口返回："+JSONObject.toJSONString(resArea));
                if (ObjectUtil.isNotEmpty(resArea) && ResultCode.SUCCESS.getCode() == resArea.getCode()) {
                    List<PoiContraryGeocodingQueryRes> list = resArea.getData();
                    if(!CollectionUtils.isEmpty(list)){
                        PoiContraryGeocodingQueryRes resMap = list.get(0);
                        gridKeeperListVO.setAddress(resMap.getAddress());
                    }
                }
            }
            bdmGridKeeperDAO.updateGridKeeperArea(gridKeeperListVOList);
        }
        return Result.success(Constants.GOOD);
    }

    public Result getGridKeeperById(String industryCode, String userId, String tenantId) {
        GridKeeperVO gridKeeperVO= bdmGridKeeperDAO.selectGridKeeperById(industryCode,userId,tenantId);
        return Result.data(gridKeeperVO);
    }

    @DataScope(gridKeeperColumn = "a.keeper_id")
    public List<GridKeeperListVO> getKeeperListByLonLat(GridKeeperListVO gridKeeperListVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        gridKeeperListVO.setTenantId(loginUser.getUser().getTenantId());
        gridKeeperListVO.setIndustryCode(loginUser.getUser().getIndustryCode());
        return bdmGridKeeperDAO.getKeeperListByLonLat(gridKeeperListVO);
    }
}
