package com.chinatower.bdm.modules.bmattendance.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.CoordinateUtil;
import com.chinatower.bdm.modules.bmattendance.dao.BmAttendanceDAO;
import com.chinatower.bdm.modules.bmattendance.entity.BmAttendanceLineBdmVO;
import com.chinatower.bdm.modules.gridKeeper.dao.BdmGridKeeperDAO;
import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.MapUtil;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmAttendanceService {
    @Resource
    private BmAttendanceDAO bmAttendanceDAO;
    @Resource
    private BdmGridKeeperDAO bdmGridKeeperDAO;

    @Resource
    FileFeignClient fileFeignClient;

    /**
     * 网格员考勤列表
     *
     * @param bmAttendanceParam
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public List<BmAttendanceListVO> getList(BmAttendanceParam bmAttendanceParam) {
        List<BmAttendanceListVO> result = new ArrayList<>();
        bmAttendanceParam.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        bmAttendanceParam.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<BmAttendanceListVO> list = bmAttendanceDAO.getList(bmAttendanceParam);
        if (!CollectionUtils.isEmpty(list)) {
            //根据网格员id查询归属的网格名称
            List<GridKeeperRefVO> refVOList = bmAttendanceDAO.getGridNamesByKeeperBatch(getKeeperIds(JSONArray.parseArray(JSONArray.toJSONString(list))));
            if (!CollectionUtils.isEmpty(refVOList)) {
                for (BmAttendanceListVO vo : list) {
                    refVOList.forEach(item -> {
                        if (item.getKeeperId().toString().equals(vo.getKeeperId()) && item.getGridId().equals(vo.getGridId()) ) {
                            vo.setGridName(!StringUtils.isEmpty(item.getGridName()) ? item.getGridName().replace(Constants.COMMA, Constants.COMMA_CH) : Constants.EMPTY_STR);
                            vo.setLoginName(item.getLoginName());
                            result.add(vo);
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 查询历史打卡记录
     *
     * @param bmAttendanceHistoryParam
     * @return List<BmAttendanceHistoryListVO>
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public List<BmAttendanceHistoryListVO> getHistoryList(BmAttendanceHistoryParam bmAttendanceHistoryParam) {
        //查询历史打卡记录
        List<BmAttendanceHistoryListVO> list = bmAttendanceDAO.getHistoryList(bmAttendanceHistoryParam);
        if (!CollectionUtils.isEmpty(list)) {
            //根据网格员id查询归属的网格名称
            List<GridKeeperRefVO> refVOList = bmAttendanceDAO.getGridNamesByKeeper(list.get(Constants.NUMBER_ZERO));
            if (!CollectionUtils.isEmpty(refVOList)) {
                //根据当天时间给 当天的打卡记录赋 状态  或者 删除 还未到打卡时间的记录
                Iterator<BmAttendanceHistoryListVO> it = list.iterator();
                while (it.hasNext()) {
                    BmAttendanceHistoryListVO vo = it.next();
                    DateTime date = DateUtil.date();
                    String dateTime = DateUtil.format(date, Constants.DATE_FORMATE);
                    String[] s = dateTime.split(Constants.EMPTY_SPACE);
                    String dateStr = s[0];
                    if (dateStr.equals(vo.getDate())) {
                        if (Constants.LINE_CENTER.equals(vo.getTime())) {
                            if (Constants.ONE.equals(vo.getType())) {
                                if (date.getTime() > DateUtil.parse(vo.getDate() + Constants.EMPTY_SPACE + vo.getSignInTime()).getTime()) {
                                    vo.setStatus(Constants.THREE);
                                } else {
                                    it.remove();
                                }
                            } else {
                                //判断时间
                                if (date.getTime() > DateUtil.parse(vo.getDate() + Constants.EMPTY_SPACE + vo.getSignOutTime()).getTime()) {
                                    vo.setStatus(Constants.SIX);
                                } else {
                                    it.remove();
                                }
                            }
                        }
                    }
                    vo.setGridName(!StringUtils.isEmpty(refVOList.get(0).getGridName()) ? refVOList.get(0).getGridName().replace(Constants.COMMA, Constants.COMMA_CH) : Constants.EMPTY_STR);
                    vo.setLoginName(refVOList.get(0).getLoginName());
                }
            }
        }
        return list;
    }

    /**
     * 数据处理，jsonArray转list
     *
     * @param json
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    private List<String> getKeeperIds(JSONArray json) {
        List<String> list = new ArrayList<>();
        for (Object object : json) {
            JSONObject obj = (JSONObject) object;
            String keeperId = obj.getString("keeperId");
            list.add(keeperId);
        }
        System.out.println(list);
        return list;
    }


    /**
     * 保存考勤设置
     *
     * @param bmAttendanceSetVO
     * @return Result
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result doSaveSetData(BmAttendanceSetVO bmAttendanceSetVO) {
        if (StringUtils.isEmpty(bmAttendanceSetVO.getTenantId())) {
            return Result.fail(Constants.ERROR_PARAM);
        }
        //根据租户id、行业编码查询考勤设置
        BmAttendanceSetVO vo = bmAttendanceDAO.findOneSetData(bmAttendanceSetVO);
        if (ObjectUtil.isEmpty(vo)) {
            //新增考勤设置
            bmAttendanceDAO.insertSetData(bmAttendanceSetVO);
        } else {
            bmAttendanceSetVO.setModifier(bmAttendanceSetVO.getCreator());
            //add by liuxt 第一次更改考勤规则时，将旧的考勤规则放到redis里 start
            String cacheKey = this.getCacheKey(bmAttendanceSetVO.getTenantId(),bmAttendanceSetVO.getIndustryCode());
            BmAttendanceSetVO firstBmAttendanceSetVO = RedisUtil.get(cacheKey, BmAttendanceSetVO.class ,RedisDBType.PRIMARY);
            if(ObjectUtils.isEmpty(firstBmAttendanceSetVO)){//第一次更新考勤规则
                //设置缓存过期时间
                RedisUtil.set(cacheKey,vo,Constants.CACHE_KEY_TIME,RedisDBType.PRIMARY);
            }
            //add by liuxt 第一次更改考勤规则时，将旧的考勤规则放到redis里 end
            //修改考勤设置
            bmAttendanceDAO.updateSetData(bmAttendanceSetVO);
        }
        return Result.success(Constants.SUCCESS);
    }

    /**
     * 根据租户id、行业编码查询考勤设置
     *
     * @param bmAttendanceSetVO
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result<BmAttendanceSetVO> findOneSetData(BmAttendanceSetVO bmAttendanceSetVO) {
        return Result.data(bmAttendanceDAO.findOneSetData(bmAttendanceSetVO));
    }

    /**
     * 获取考勤设置
     *
     * @param vo
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result findOneAttendanceByKeeper(BmAttendanceLineVO vo) {
        String attendanceDate = vo.getAttendanceDate();
        if (StringUtils.isEmpty(attendanceDate)) {
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String keeperId = this.getKeeperId(user);
        vo.setKeeperId(keeperId);
        vo.setTenantId(user.getTenantId());
        vo.setIndustryCode(user.getIndustryCode());
        return Result.data(bmAttendanceDAO.findOneByKeeper(vo));
    }

    /**
     * 签到
     *
     * @param vo
     * @return Result
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result doSignIn(AttendanceSignParam vo) {
        log.info("------------------------------doSignIn----------------------------");
        log.info("doSignIn入参" + vo);

        //铁塔视角不允许签到/签退，直接返回成功
        boolean aSwitch = SecurityUtils.isSwitch();
        log.info("SecurityUtils.isSwitch()={}",aSwitch);
        if(aSwitch){
            return Result.condition(true);
        }

        LocalTime now = LocalTime.now();
        LocalDateTime nowTime = LocalDateTime.now();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String keeperId = this.getKeeperId(user);
        log.info("doSignIn人员校验通过");
        //查询网格员今日考勤数据
        BmAttendanceLineVO param = new BmAttendanceLineVO();
        param.setKeeperId(keeperId);
        param.setAttendanceDate(DateUtils.getDate());
        param.setGridId(vo.getGridId());
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        BmAttendanceLineVO lineVO = bmAttendanceDAO.findOneByKeeper(param);
        log.info("doSignIn lineVO:" + lineVO + "param:" + param);
        if (ObjectUtil.isEmpty(lineVO)) {
            return Result.fail(Constants.NO_DATA);
        }

        // 是否允许打卡
        // 打卡允许开始时间
        LocalTime startTime = LocalTime.parse(lineVO.getSignBegin());
        LocalTime busInTime = LocalTime.parse(lineVO.getBusInTime());
        LocalTime signInTime = LocalTime.parse(lineVO.getSignInTime());
        if (now.isBefore(startTime) || now.isAfter(signInTime)) {
            log.info(Constants.DOSIGNINSTATUS + Constants.NOT_ALLOW_SIGN_IN);
            return Result.fail(Constants.NOT_ALLOW_SIGN_IN);
        }

        if(!StringUtils.isEmpty(lineVO.getSignInAddTime())){//增加只能打卡一次的校验
            return Result.fail(Constants.ONE_SIGN_IN);
        }

        if (now.isAfter(busInTime)) {
            //迟到
            lineVO.setSignInStatus(Constants.TWO);
            log.info(Constants.DOSIGNINSTATUS + Constants.TWO);
        } else {
            //正常
            lineVO.setSignInStatus(Constants.ONE);
            log.info(Constants.DOSIGNINSTATUS + Constants.ONE);
        }
        // 获取网格员所属网格
        // 签到是否网格内,1是,0否
        lineVO.setSignInOutside(Constants.ZERO);
        //插入签到时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMATE);
        lineVO.setSignInAddTime(nowTime.format(dateTimeFormatter));
        //签到所在网格
        lineVO.setSignInGrid(null);
        GridKeeperVO keeper = new GridKeeperVO();
        keeper.setKeeperId(Long.valueOf(keeperId));
        keeper.setGridId(Long.valueOf(vo.getGridId()));
        List<Map> grids = bdmGridKeeperDAO.findKeeperRef(keeper);
        if (!CollectionUtils.isEmpty(grids)) {
            for (Map map : grids) {
                String gridRange = (String) map.get(Constants.GRID_RANGE);
                if (!StringUtils.isEmpty(gridRange)) {
                    gridRange = gridRange.replace(",[[", "-[");
                    String[] gridRangeArry = gridRange.split(Constants.LINE_CENTER);
//                    for (int i = 0; i < gridRangeArry.length; i++) {
//                        String range = gridRangeArry[i].replaceAll(Constants.CURLY_BRACES, "").replaceAll(Constants.ENCLOSED_CHECK, "");
//                        if (MapUtil.rayCasting(Double.valueOf(vo.getLongitude()), Double.valueOf(vo.getLatitude()), range)) {
//                            lineVO.setSignInGrid(String.valueOf(map.get(Constants.GRID_ID)));
//                            lineVO.setSignInOutside(Constants.ONE);
//                            break;
//                        }
//                    }
                    //替换java JTS方法
                    for (int i = 0; i < gridRangeArry.length; i++) {
                        String range = gridRangeArry[i].replace(Constants.TWO_SQUARE_BRACKETS, Constants.VERTICAL_LINE).replace(Constants.LEFT_SQUARE_BRACKET, "").replace(Constants.RIGHT_SQUARE_BRACKET, "");
                        Coordinate[] coordinateArray = CoordinateUtil.getCoordinateArray(range);
                        Coordinate coordinate = new Coordinate(Double.valueOf(vo.getLongitude()),  Double.valueOf(vo.getLatitude()));
                        if (CoordinateUtil.withinAndIntersects(coordinateArray, coordinate)) {
                            lineVO.setSignInGrid(String.valueOf(map.get(Constants.GRID_ID)));
                            lineVO.setSignInOutside(Constants.ONE);
                            break;
                        }
                    }
                }
            }
        }
        lineVO.setSignInCoordinate(vo.getLongitude() + Constants.COMMA + vo.getLatitude());
        lineVO.setSignInAddress(vo.getAddress());
        lineVO.setSignOutStatus(null);
        boolean b = bmAttendanceDAO.updateSignData(lineVO) > 0;
        log.info("doSignIn lineVO:" + lineVO + "boolean:" + b);
        return Result.condition(b);
    }

    /**
     * 签退
     *
     * @param vo
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result doSignOut(AttendanceSignParam vo) {
        log.info("------------------------------doSignOut----------------------------");
        log.info("doSignOut入参" + vo);

        //铁塔视角不允许签到/签退，直接返回成功
        boolean aSwitch = SecurityUtils.isSwitch();
        log.info("SecurityUtils.isSwitch()={}",aSwitch);
        if(aSwitch){
            return Result.condition(true);
        }

        LocalTime now = LocalTime.now();
        LocalDateTime nowTime = LocalDateTime.now();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String keeperId = this.getKeeperId(user);
        //查询网格员今日考勤数据
        BmAttendanceLineVO param = new BmAttendanceLineVO();
        param.setKeeperId(keeperId);
        param.setAttendanceDate(DateUtils.getDate());
        param.setGridId(vo.getGridId());
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        BmAttendanceLineVO lineVO = bmAttendanceDAO.findOneByKeeper(param);
        if (ObjectUtil.isEmpty(lineVO)) {
            return Result.fail(Constants.NO_DATA);
        }
        // 是否允许打卡
        // 打卡允许开始时间
        LocalTime startTime = LocalTime.parse(lineVO.getSignEnd());
        LocalTime busOutTime = LocalTime.parse(lineVO.getBusOutTime());
        LocalTime endTime = LocalTime.parse(lineVO.getSignOutTime());

        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return Result.fail(Constants.NOT_ALLOW_SIGN_OUT);
        }
        if (now.isBefore(busOutTime)) {
            //早退
            lineVO.setSignOutStatus(Constants.FIVE);
        } else {
            //正常
            lineVO.setSignOutStatus(Constants.FOUR);
        }
        //插入签退时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMATE);
        lineVO.setSignOutAddTime(nowTime.format(dateTimeFormatter));
        // 获取网格员所属网格
        lineVO.setSignOutOutside(Constants.ZERO);
        lineVO.setSignOutGrid(null);
        GridKeeperVO keeper = new GridKeeperVO();
        keeper.setKeeperId(Long.valueOf(keeperId));
        keeper.setGridId(Long.valueOf(vo.getGridId()));
        List<Map> grids = bdmGridKeeperDAO.findKeeperRef(keeper);
        if (!CollectionUtils.isEmpty(grids)) {
            for (Map map : grids) {
                String gridRange = (String) map.get(Constants.GRID_RANGE);
                if (!StringUtils.isEmpty(gridRange)) {
                    //处理一个网格标绘多个区域场景
                    gridRange = gridRange.replace(",[[", "-[");
                    String[] gridRangeArry = gridRange.split(Constants.LINE_CENTER);
                    //替换java JTS方法
                    for (int i = 0; i < gridRangeArry.length; i++) {
                        String range = gridRangeArry[i].replace(Constants.TWO_SQUARE_BRACKETS, Constants.VERTICAL_LINE).replace(Constants.LEFT_SQUARE_BRACKET, "").replace(Constants.RIGHT_SQUARE_BRACKET, "");
                        Coordinate[] coordinateArray = CoordinateUtil.getCoordinateArray(range);
                        Coordinate coordinate = new Coordinate(Double.valueOf(vo.getLongitude()),  Double.valueOf(vo.getLatitude()));
                        if (CoordinateUtil.withinAndIntersects(coordinateArray, coordinate)) {
                            lineVO.setSignOutGrid(String.valueOf(map.get(Constants.GRID_ID)));
                            lineVO.setSignOutOutside(Constants.ONE);
                            break;
                        }
                    }
//                    gridRange = gridRange.replaceAll(Constants.CURLY_BRACES, "");
//                    gridRange = gridRange.replaceAll(Constants.ENCLOSED_CHECK, "");
//                    if (MapUtil.rayCasting(Double.valueOf(vo.getLongitude()), Double.valueOf(vo.getLatitude()), gridRange)) {
//                        lineVO.setSignOutGrid(String.valueOf(map.get(Constants.GRID_ID)));
//                        lineVO.setSignOutOutside(Constants.ONE);
//                        break;
//                    }
                }
            }
        }
        lineVO.setSignOutCoordinate(vo.getLongitude() + Constants.COMMA + vo.getLatitude());
        lineVO.setSignOutAddress(vo.getAddress());

        return Result.condition(bmAttendanceDAO.updateSignData(lineVO) > 0);
    }

    /**
     * 根据网格员获取考勤配置数据(考勤规则)
     *
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result<BmAttendanceSetVO> findOneSetDataByKeeper(BmAttendanceSetVO bmAttendanceSetVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        bmAttendanceSetVO.setTenantId(user.getTenantId());
        bmAttendanceSetVO.setIndustryCode(user.getIndustryCode());
        String keeperId = this.getKeeperId(user);
        //add by liuxt 若redis里有考勤规则，从里面取出旧的考勤规则，若无考勤规则，查表取实时考勤规则 start
        BmAttendanceSetVO vo;
        String cacheKey = this.getCacheKey(bmAttendanceSetVO.getTenantId(),bmAttendanceSetVO.getIndustryCode());
        BmAttendanceSetVO firstBmAttendanceSetVO = RedisUtil.get(cacheKey, BmAttendanceSetVO.class ,RedisDBType.PRIMARY);
        if(!ObjectUtils.isEmpty(firstBmAttendanceSetVO)){
            //旧的考勤规则不为空 取缓存里的考勤规则
            vo = firstBmAttendanceSetVO;
        }else{
            //根据租户id、行业编码查询考勤设置
            vo = bmAttendanceDAO.findOneSetData(bmAttendanceSetVO);
        }
        //add by liuxt 若redis里有考勤规则，从里面取出旧的考勤规则，若无考勤规则，查表取实时考勤规则 end
        if (ObjectUtil.isEmpty(vo)) {
            return Result.fail(Constants.NO_ATTENDANCE_RULE);
        }
        GridKeeperVO keeper = new GridKeeperVO();
        keeper.setKeeperId(Long.valueOf(keeperId));
        if(!StringUtils.isEmpty(bmAttendanceSetVO.getGridId())){
            keeper.setGridId(Long.valueOf(bmAttendanceSetVO.getGridId()));
        }
        //根据网格员查网格信息
        List<Map> grids = bdmGridKeeperDAO.findKeeperRef(keeper);
        if (!CollectionUtils.isEmpty(grids)) {
            List<Map<String, Object>> signAddressAndSignGridRange = new ArrayList<>();
            for (Map map : grids) {
                String mapStrGridName = (String) map.get(Constants.GRID_NAME);
                List<String> signGridRange = new ArrayList<>();
                String gridRange = (String) map.get(Constants.GRID_RANGE);
                //将一个网格里的多个区域拆开
                List<String> jsonArray = JSONObject.parseArray(gridRange, String.class);
                if (!StringUtils.isEmpty(jsonArray)) {
                    signGridRange.addAll(jsonArray);
                }
                Map<String, Object> signAddressAndSignGridRangeInLoop = new HashMap<>();
                signAddressAndSignGridRangeInLoop.put(Constants.GRID_NAME, mapStrGridName);
                signAddressAndSignGridRangeInLoop.put(Constants.GRID_RANGE, signGridRange);
                signAddressAndSignGridRange.add(signAddressAndSignGridRangeInLoop);
            }
            vo.setSignGridNameAndRange(signAddressAndSignGridRange);
        }
        //查字典返回工作日名称
        List<SysDictData> sysDictData = bdmGridKeeperDAO.selectDictDataByType(Constants.SYS_WEEK_DAY);
        String[] onWeekdaysList = vo.getOnWeekdays().split(Constants.COMMA);
        Arrays.sort(onWeekdaysList);
        StringBuilder weekName = new StringBuilder();
        for (String s : onWeekdaysList) {
            for (SysDictData sysDictDatum : sysDictData) {
                if (sysDictDatum.getDictValue().equals(s)) {
                    weekName.append(sysDictDatum.getDictLabel()).append(Constants.COMMA_CH);
                }
            }
        }
        vo.setOnWeekdaysName(NullProcessUtil.nvlToString(weekName.deleteCharAt(weekName.lastIndexOf(Constants.COMMA_CH)), ""));
        return Result.data(vo);
    }

    /**
     * 月考勤数据
     *
     * @param param
     * @return Result<AttendanceStatisticsVO>
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result<AttendanceStatisticsVO> getAttendanceStatistics(AttendanceStatisticsParam param) {
        String month = param.getMonth();
        if (StringUtils.isEmpty(month)) {
            return Result.fail(Constants.ERROR_PARAM);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String keeperId = this.getKeeperId(user);
        param.setKeeperId(keeperId);
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        //查询月度统计信息，不包含当日
        AttendanceStatisticsVO attendanceStatistics = bmAttendanceDAO.getAttendanceStatistics(param);
        attendanceStatistics.setList(bmAttendanceDAO.getAttendanceStatisticsDetail(attendanceStatistics));

        //入参月份==当前月份,需要对当天打卡状态做特殊处理
        if(month.equals(DateUtils.getCurrentMonth())){
            LocalTime now = LocalTime.now();
            String date = DateUtils.getDate();
            //查询当天数据
            param.setAttendanceDate(date);
            BmAttendanceLineVO currentDayLine = bmAttendanceDAO.getCurrentDayLine(param);
            if(!ObjectUtil.isEmpty(currentDayLine)){
                String signInStatus = currentDayLine.getSignInStatus();
                String signOutStatus = currentDayLine.getSignOutStatus();
                String signInTime = currentDayLine.getSignInTime();
                String signOutTime = currentDayLine.getSignOutTime();
                String status;
                /**
                 * 考勤状态：正常
                 * 1、signInStatus=1 and signOutStatus=4
                 * 2、signInStatus=1 and signOutStatus is null and 当前时间 < 签退结束时间
                 * 考勤状态：无
                 * 1、signInStatus is null and 当前时间 < 签到结束时间
                 * 考勤状态：异常
                 *  除上述情况的其他场景
                 */
                if((Constants.ONE.equals(signInStatus) && Constants.FOUR.equals(signOutStatus))
                    || (Constants.ONE.equals(signInStatus) && StringUtils.isEmpty(signOutStatus) && now.isBefore(LocalTime.parse(signOutTime)))){
                    status = "正常";
                }else if(StringUtils.isEmpty(signInStatus) && now.isBefore(LocalTime.parse(signInTime))){
                    status = null;
                }else{
                    status = "异常";
                }
                if(!StringUtils.isEmpty(status)){
                    List<AttendanceStatisticsDetailVO> attendanceStatisticsDetail = attendanceStatistics.getList();
                    AttendanceStatisticsDetailVO attendanceStatisticsDetailVO = new AttendanceStatisticsDetailVO();
                    attendanceStatisticsDetailVO.setDate(date);
                    attendanceStatisticsDetailVO.setStatus(status);
                    attendanceStatisticsDetail.add(attendanceStatisticsDetailVO);
                    attendanceStatistics.setList(attendanceStatisticsDetail);
                }
            }
        }
        return Result.data(attendanceStatistics);
    }

    public Result<AttendanceCurrentMonthStatisticsVO> getCurrentMonthStatistics(AttendanceStatisticsParam param) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String keeperId = this.getKeeperId(user);
        param.setKeeperId(keeperId);
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        //统计本月签到、签退状态不为空的数据
        AttendanceCurrentMonthStatisticsVO currentMonthStatistics = bmAttendanceDAO.getCurrentMonthStatistics(param);
        //查询当天数据
        param.setAttendanceDate(DateUtils.getDate());
        BmAttendanceLineVO currentDayLine = bmAttendanceDAO.getCurrentDayLine(param);
        //如果存在当前考勤记录，并且签到状态 或者 签退状态 is null
        if(!ObjectUtil.isEmpty(currentDayLine) &&
                (StringUtils.isEmpty(currentDayLine.getSignInStatus()) || StringUtils.isEmpty(currentDayLine.getSignOutStatus()))){
            LocalTime now = LocalTime.now();
            Integer lackCount = currentMonthStatistics.getLackCount();
            //签到状态 is null 并且 当前时间>签到结束时间，则算作缺卡
            if(StringUtils.isEmpty(currentDayLine.getSignInStatus()) && now.isAfter(LocalTime.parse(currentDayLine.getSignInTime()))){
                lackCount ++;
            }
            //签退状态 is null 并且 当前时间>签退结束时间，则算作缺卡
            if(StringUtils.isEmpty(currentDayLine.getSignOutStatus()) && now.isAfter(LocalTime.parse(currentDayLine.getSignOutTime()))){
                lackCount ++;
            }
            currentMonthStatistics.setLackCount(lackCount);
        }
        return Result.data(currentMonthStatistics);
    }

    private String getKeeperId(SysUser user){
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        GridKeeperVO returnGridKeeperVO = bdmGridKeeperDAO.getUserByLoginName(user.getUserName());
        if(ObjectUtils.isEmpty(returnGridKeeperVO)){
            throw new CustomException(Constants.NO_USER);
        }
        gridKeeperVO.setUserId(returnGridKeeperVO.getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        //通过用户id、租户id、行业编码获取网格员id
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if (ObjectUtil.isEmpty(keeper)) {
            throw new CustomException(Constants.NOT_GRID_KEEPER);
        }
        return keeper.getKeeperId().toString();
    }


    /**
     * 获取考勤用户信息
     *
     * @return
     * @author zl
     * @version release_v1.5
     * @requestID 中国铁塔_铁塔视联平台__网格员考勤
     */
    public Result<BmAttendanceUserVO> getAttendanceUserInfo(BmAttendanceUserVO bmAttendanceUserVO) {
        log.info("BmAttendanceUserVO:"+JSONObject.toJSONString(bmAttendanceUserVO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        StringBuffer stringBuffer = new StringBuffer();
        String gridName = "";
        bmAttendanceUserVO.setUserName(user.getUserName());
        bmAttendanceUserVO.setTenantId(user.getTenantId());
        bmAttendanceUserVO.setIndustryCode(user.getIndustryCode());
        BmAttendanceUserVO userVO = bmAttendanceDAO.getAttendanceUserInfo(bmAttendanceUserVO);
        if (!ObjectUtils.isEmpty(userVO)) {
            List<BmAttendanceUserVO> gridList = bmAttendanceDAO.getAttendanceUserGridInfo(userVO);
            userVO.setGridList(gridList);
            Result<String> obsFileUrlEncrypt = fileFeignClient.getObsFileUrlEncrypt(userVO.getAvatar());
            if (obsFileUrlEncrypt != null) {
                userVO.setAvatar(obsFileUrlEncrypt.getData());
            }
        }
        return Result.data(userVO);
    }

    public void insertAttendanceLine(Long keeperId){
        log.info("keeperId={}",keeperId);
        if(keeperId == null){
            return;
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String startTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + "00:00:00";
        String endTimeStr = DateUtils.getDate() + VideoConstants.BLANK_SPACE + "01:00:00";
        //当前时间在0-1点，不需要执行以下逻辑，因为1点有attendanceLineHandler定时任务，会初始化当天数据
        if(!DateUtils.isEffectiveDateHMS(DateUtils.getTime(), startTimeStr, endTimeStr)){
            //获取今天星期几
            String week = getWeek(new Date());
            //查枚举值表
            String weekDay =  bmAttendanceDAO.selectDictDataByType(Constants.SYS_WEEK_DAY,week);
            //过滤掉不是工作日的数据
            List<BmAttendanceLineBdmVO> insertList = new ArrayList<>();
            String cacheKey = this.getCacheKey(user.getTenantId(),user.getIndustryCode());
            BmAttendanceSetVO firstBmAttendanceSetVO = RedisUtil.get(cacheKey, BmAttendanceSetVO.class ,RedisDBType.PRIMARY);
            if(!ObjectUtils.isEmpty(firstBmAttendanceSetVO)){
                log.info("firstBmAttendanceSetVO={}",JSONObject.toJSONString(firstBmAttendanceSetVO));
                //旧的考勤规则不为空 取缓存里的考勤规则
                if(!firstBmAttendanceSetVO.getOnWeekdays().contains(weekDay)){
                    return;
                }
                BmAttendanceUserVO bmAttendanceUserVO = new BmAttendanceUserVO();
                bmAttendanceUserVO.setKeeperId(String.valueOf(keeperId));
                List<BmAttendanceUserVO> gridList = bmAttendanceDAO.getAttendanceUserGridInfo(bmAttendanceUserVO);
                for (BmAttendanceUserVO attendanceUserVO : gridList) {
                    BmAttendanceLineBdmVO bmAttendanceLineBdmVO = new BmAttendanceLineBdmVO();
                    bmAttendanceLineBdmVO.setTenantId(user.getTenantId());
                    bmAttendanceLineBdmVO.setIndustryCode(user.getIndustryCode());
                    bmAttendanceLineBdmVO.setAppVerCode(user.getAppVerCode());
                    bmAttendanceLineBdmVO.setSignInTime(firstBmAttendanceSetVO.getSignOver());
                    bmAttendanceLineBdmVO.setSignOutTime(firstBmAttendanceSetVO.getSignClose());
                    bmAttendanceLineBdmVO.setSignBegin(firstBmAttendanceSetVO.getSignBegin());
                    bmAttendanceLineBdmVO.setSignEnd(firstBmAttendanceSetVO.getSignEnd());
                    bmAttendanceLineBdmVO.setBusInTime(firstBmAttendanceSetVO.getSignInTime());
                    bmAttendanceLineBdmVO.setBusOutTime(firstBmAttendanceSetVO.getSignOutTime());
                    bmAttendanceLineBdmVO.setKeeperId(attendanceUserVO.getKeeperId());
                    bmAttendanceLineBdmVO.setKeeperName(attendanceUserVO.getKeeperName());
                    bmAttendanceLineBdmVO.setGridId(attendanceUserVO.getGridId());
                    insertList.add(bmAttendanceLineBdmVO);
                }
            }else{
                insertList = bmAttendanceDAO.getKeeperInfo(weekDay,String.valueOf(keeperId));
            }
            log.info("insertList_before={}",JSONArray.toJSONString(insertList));
            if (!CollectionUtils.isEmpty(insertList)){
                //查询考勤表今日已有的考勤记录
                AttendanceStatisticsParam param = new AttendanceStatisticsParam();
                param.setKeeperId(String.valueOf(keeperId));
                param.setAttendanceDate(DateUtils.getDate());
                List<String> allLineList = bmAttendanceDAO.getAllLineList(param);
                //应有的考勤-考勤表里已有的
                insertList = insertList.stream().filter(pe -> containEntity(pe.getGridId(), allLineList) <= -1).collect(Collectors.toList());
                log.info("insertList_after={}",JSONArray.toJSONString(insertList));
                bmAttendanceDAO.insertAttendanceLineInfo(insertList);
            }
        }
    }

    public int containEntity(String gridId, List<String> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            if (gridId.equals(list.get(i))) {
                res = i;
                break;
            }
        }
        return res;
    }

    /**
     * 获取今天日期获取对应枚举值
     * @author zl
     * @param date
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员考勤
     */
    private static String getWeek(Date date){
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekIndex<0){
            weekIndex = 0;
        }
        return weeks[weekIndex];
    }

    private String getCacheKey(String tenantId,String industryCode){
        return Constants.ATTENDANCE_KEY + Constants.UNDERLINE + tenantId + Constants.UNDERLINE + industryCode + Constants.UNDERLINE + DateUtils.getDate();
    }
}
