package com.chinatower.bdm.modules.inspection.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.gridKeeper.dao.BdmGridKeeperDAO;
import com.chinatower.bdm.modules.gridKeeper.dao.BmKeeperTrailDAO;
import com.chinatower.bdm.modules.gridKeeper.entity.GridKeeperTrail;
import com.chinatower.bdm.modules.gridKeeper.service.BdmGridKeeperService;
import com.chinatower.bdm.modules.inspection.dao.BdmInspectionDAO;
import com.chinatower.bdm.modules.inspection.entity.*;
import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BdmInspectionService {
    @Resource
    private BdmInspectionDAO bdmInspectionDAO;
    @Resource
    private BdmGridKeeperDAO bdmGridKeeperDAO;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    BdmGridKeeperService bdmGridKeeperService;

    @Autowired
    private BmKeeperTrailDAO bmKeeperTrailDAO;


    /**
     * 巡查计划配置保存与编辑接口
     * @Author：zl
     * @param inspectionConfVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    @DataScope(industryCode = "a.industry_code",tenantColumn = "a.tenant_id")
    @Transactional(rollbackFor = Exception.class)
    public Result addConfInfo(InspectionConfVO inspectionConfVO) throws ParseException {
        log.info("addConfInfo入参:"+JSON.toJSONString(inspectionConfVO));
        //任务名称重复校验 规则为租户、行业下计划名称不能重复
        if (!StringUtils.isEmpty(bdmInspectionDAO.checkConf(inspectionConfVO))) {
            return Result.fail(Constants.DUPLICAT_GRID);
        }

        //校验不重复情况下，当天派发任务派发时间需大于当前时间
        Date now = new Date();
        String dataTime = DateUtil.format(DateUtil.parse(inspectionConfVO.getDataTime()), Constants.DATE_FORMATE);
        String levelEndTime = inspectionConfVO.getLevelEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMATE_DAY);
        log.info("下发时间:"+DateUtils.parseDate(dataTime)+",当前时间:"+DateUtils.parseDate(DateUtils.getTime()));
        if(Constants.ZERO.equals(inspectionConfVO.getIsRepeat()) && sdf.format(now).equals(levelEndTime) &&
                DateUtils.parseDate(dataTime).before(DateUtils.parseDate(DateUtils.getTime()))){
            return Result.fail(Constants.MORE_TIME);
        }
        inspectionConfVO.setDataTime(dataTime);
        inspectionConfVO.setCreateTime(now);

        //巡查时段处理
        ObjectMapper mapper = new ObjectMapper();
        if (!CollectionUtils.isEmpty(inspectionConfVO.getConfPeriodList())) {
            try {
                inspectionConfVO.setInspectionPeriod(mapper.writeValueAsString(inspectionConfVO.getConfPeriodList()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.error(Constants.INSPECTION_PERIOD_ERROR,e);
                return Result.fail(Constants.ADD_CONF_INFO_ERROR);
            }
        }

        if(StringUtils.isEmpty(inspectionConfVO.getConfId())){
            //新增
            inspectionConfVO.setConfId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
            bdmInspectionDAO.insertConfInfo(inspectionConfVO);
        }else{
            //查询conf_status状态，已完成状态的任务不可修改
            InspectionConfVO dbInspectionConfInfo = bdmInspectionDAO.getConfStatus(inspectionConfVO.getConfId());
            if (Constants.ONE.equals(dbInspectionConfInfo.getConfStatus())){
                return Result.fail(Constants.NO_EDIT);
            }
            //修改前处理逻辑
            stopLogic(inspectionConfVO.getConfId());
            //修改
            inspectionConfVO.setUpdateTime(now);
            inspectionConfVO.setModifier(inspectionConfVO.getCreator());
            bdmInspectionDAO.updateConfInfo(inspectionConfVO);
        }

        //如果不重复今天的任务直接插入数据到task表
        if(Constants.ZERO.equals(inspectionConfVO.getIsRepeat()) && sdf.format(now).equals(levelEndTime)){
            List<InspectionConfVO> periodList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(inspectionConfVO.getConfPeriodList())) {
                //生成多条任务
                for (InspectionConfPeriodVO periodVO : inspectionConfVO.getConfPeriodList()) {
                    InspectionConfVO dto = new InspectionConfVO();
                    BeanUtils.copyProperties(inspectionConfVO,dto);
                    dto.setInspectionStartTime(periodVO.getInspectionStartTime());
                    dto.setInspectionEndTime(periodVO.getInspectionEndTime());
                    dto.setTaskId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
                    periodList.add(dto);
                }
            } else {
                inspectionConfVO.setTaskId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
                periodList.add(inspectionConfVO);
            }
            int i = bdmInspectionDAO.batchInsertTaskInfo(periodList);
            if(i>0){
                bdmInspectionDAO.updateRepeatConfStatus(inspectionConfVO);
                batchPushAll(periodList);
            }
        }
        return Result.success(Constants.GOOD);
    }


    /**
     * 抽共通方法 存入redis推送消息
     * @param inspectionConfVOList
     * @return
     */
    public void batchPushAll(List<InspectionConfVO> inspectionConfVOList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMATE);
        if(!CollectionUtils.isEmpty(inspectionConfVOList)){
            //根据keeperId查出userId
            String keeperIds = inspectionConfVOList.get(0).getKeeperId();
            List<String> keeperList= Arrays.asList(keeperIds.split(Constants.COMMA));
                List<String> userIdList = bdmInspectionDAO.getUserIdByKeerperList(keeperList);
            if (!CollectionUtils.isEmpty(userIdList)){
                for (int i = 0; i < inspectionConfVOList.size(); i++) {
                    InspectionConfVO inspectionConfVO = inspectionConfVOList.get(i);
                    List<String> argList = new ArrayList<>();
                    for (String userId : userIdList) {
                        //把task任务存在redis里 需要的4个值
                        InspectionTaskVO inspectionTaskVO = new InspectionTaskVO();
                        inspectionTaskVO.setUserId(userId);
                        inspectionTaskVO.setTaskId(inspectionConfVO.getTaskId());
                        inspectionTaskVO.setTaskName(inspectionConfVO.getCnofName());
                        inspectionTaskVO.setCreateTime(inspectionConfVO.getDataTime());
                        inspectionTaskVO.setTaskStatus(Constants.TWO);
                        inspectionTaskVO.setAppUrl(Constants.APP_URL_PREFIX+inspectionConfVO.getTaskId()+Constants.APP_URL_SUFFIX+Constants.TWO);
                        inspectionTaskVO.setConfDesc(inspectionConfVO.getConfDesc());
                        inspectionTaskVO.setIndustryCode(inspectionConfVO.getIndustryCode());
                        argList.add(JSON.toJSONString(inspectionTaskVO));
                    }
                    log.info("存入redis巡查任务对象：{}", JSON.toJSONString(argList));
                    RedisUtil.lRightPushAll(Constants.TASK_JOB,argList, RedisDBType.PRIMARY);
                }
            }
        }
    }


    /**
     * 巡查计划单条数据获取findOneInfo
     * @Author：zl
     * @param confId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    public Result findOneInfo(String confId,String flag) {
        InspectionConfVO inspectionConfVO = bdmInspectionDAO.findOne(confId);
        if(ObjectUtil.isNotEmpty(inspectionConfVO)) {
            //获取网格层级
            StringBuilder stringBuilder = new StringBuilder();
            String parentGridName = getParentGridName(stringBuilder, Long.valueOf(inspectionConfVO.getGridId()));
            inspectionConfVO.setGridName(parentGridName);
        }

        return Result.data(inspectionConfVO);
    }

    /**
     * 巡查计划获取列表数据getConfList
     * @Author：zl
     * @param inspectionConfParam
     * @return List<InspectionConfListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查计划相关接口
     */
    public List<InspectionConfListVO> getConfList(InspectionConfParam inspectionConfParam) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        inspectionConfParam.setIndustryCode(user.getIndustryCode());
        inspectionConfParam.setTenantId(user.getTenantId());
        log.info("getConfList接口入参:"+JSON.toJSONString(inspectionConfParam));
        return bdmInspectionDAO.getConfList(inspectionConfParam);
    }


    /**
     * 巡查任务修改前判断未下发任务的处理
     * @Author：zl
     * @param confId
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    private void stopLogic(String confId) {
        //已完成的数据不动，未下发的数据不再下发，已下发的任务不撤回
        //查出task表里conf_id相等的所有数据，send_flag= 0-未下发的数据del掉
        List<InspectionTaskVO> inspectionTaskVOList = bdmInspectionDAO.CheckTaskByConfId(confId);
        List<String> delTaskIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inspectionTaskVOList)){
            for (InspectionTaskVO inspectionTaskVO : inspectionTaskVOList) {
                //send_flag= 0未下发的数据del掉
                if (0==inspectionTaskVO.getSendFlag()){
                    String delTaskId = inspectionTaskVO.getTaskId();
                    delTaskIdList.add(delTaskId);
                }
            }
            if (!CollectionUtils.isEmpty(delTaskIdList)){
                bdmInspectionDAO.delTask(delTaskIdList);
            }
        }
    }

    /**
     * 巡查计划删除接口
     * @Author：zl
     * @param param
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台_巡查计划相关接口
     */
    @Transactional(rollbackFor = Exception.class)
    public Result deleteConf(InspctionConfStatusParam param) throws ParseException {
        log.info("deleteConf接口入参:"+JSON.toJSONString(param));
        //状态为已完成的任务不能停用  1-已完成
        InspectionConfVO inspectionConfVO = bdmInspectionDAO.getConfStatus(param.getConfId());
        if (Constants.ONE.equals(inspectionConfVO.getConfStatus())){
            return Result.fail(Constants.NO_STOP_TASK);
        }

        //状态为“未完成”且由该计划产生的任务均未执行时可以删除
        List<InspectionTaskVO> list = bdmInspectionDAO.CheckTaskByConfId(param.getConfId());
        //只要存在该计划下的任务为已完成，不可删除
        if (list.stream().filter(vo -> Constants.ONE.equals(vo.getTaskStatus())).findAny().isPresent()) {
            return Result.fail(Constants.INSPECTION_NO_STOP_TASK);
        }

        int i = bdmInspectionDAO.deleteConf(param);
        //删除该计划产生的任务数据也被删除
        bdmInspectionDAO.deleteTask(param);
        return Result.condition(i > 0);
    }

    /**
     * 巡查历史列表数据
     * @Author：zl
     * @param inspectionTaskParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public List<InspectionTaskListVO> getTaskList(InspectionTaskParam inspectionTaskParam) {
        return bdmInspectionDAO.getTaskList(inspectionTaskParam);
    }

    /**
     * 巡查历史列表数据getTaskList
     * @Author：zl
     * @param inspectionTaskParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "a.keeper_id")
    public List<InspectionTaskListVOExtend> getTaskListExtend(InspectionTaskParam inspectionTaskParam) {
        return bdmInspectionDAO.getTaskListExtend(inspectionTaskParam);
    }

    /**
     * 获取上级网格名
     * @Author：zl
     * @param grid
     * @return String
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    private String getParentGridName(StringBuilder stringBuilder,Long grid){
        log.info("当前gridId为:"+grid);
        GridVO gridVO = bdmInspectionDAO.getParentGridInfo(grid);
        log.info("当前返回gridVO为:"+ JSONObject.toJSONString(gridVO));
        stringBuilder.insert(0, Constants.LGT);
        stringBuilder.insert(0,gridVO.getGridName());
        if (gridVO.getParentId() != 0){
            getParentGridName(stringBuilder,gridVO.getParentId());
        }
        if (Constants.LGT.equals(NullProcessUtil.nvlToString(stringBuilder.charAt(stringBuilder.length()-1),""))){
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(Constants.LGT));
        }
        return NullProcessUtil.nvlToString(stringBuilder, "");
    }

    /**
     * 巡查历史详情
     * @Author：liuxt
     * @param taskId
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<InspectionTaskVO> getTaskInfo(String taskId){
        log.info(Constants.TASK_INFO_ID + taskId);
        //APP巡查任务需求 add by liuxt 20230504 新增和修改字段
        InspectionTaskVO vo = bdmInspectionDAO.getTaskInfo(taskId);
        log.info(Constants.TASK_INFO_VO + JSON.toJSONString(vo));
        if(ObjectUtil.isNotEmpty(vo)) {
            return getTaskDetailCommmon(vo);
        }
        return Result.data(vo);
    }



    /**
     * 根据网格id查询巡查任务详情(取巡查日期最新的一条)
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<InspectionTaskVO> getTaskInfoByCondition(InspectionTaskNoPageParam inspectionTaskNoPageParam){
        log.info("根据网格id查询巡查任务详情:" + JSON.toJSONString(inspectionTaskNoPageParam));
        //APP巡查任务需求 add by liuxt 20230504 新增和修改字段
        InspectionTaskVO vo = bdmInspectionDAO.getTaskInfoByCondition(inspectionTaskNoPageParam);
        log.info(Constants.TASK_INFO_VO + JSON.toJSONString(vo));
        if(ObjectUtil.isNotEmpty(vo)) {
            return getTaskDetailCommmon(vo);
        }
        return Result.data(vo);
    }



    /**
     * 获取详细任务共通方法
     * @Author：liuxt
     * @param vo
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<InspectionTaskVO> getTaskDetailCommmon(InspectionTaskVO vo){
        vo.setCurGridName(vo.getGridName());
        //获取网格层级
        StringBuilder stringBuilder = new StringBuilder();
        log.info("getTaskDetailCommmon-gridId为:"+vo.getGridId());
        String parentGridName = getParentGridName(stringBuilder, Long.valueOf(vo.getGridId()));
        vo.setGridName(parentGridName);
        if(!StringUtils.isEmpty(vo.getKeeperId())){//增加返回网格长列表和网格员列表
            GridKeeperVO gridKeeperVO = new GridKeeperVO();
            List<String> curKeeperIdList = Arrays.asList(vo.getKeeperId().split(Constants.COMMA));
            gridKeeperVO.setKeeperIdList(curKeeperIdList);
            List<GridKeeperVO> keeperInfoList = bdmInspectionDAO.getKeeperInfo(gridKeeperVO);
            if(!CollectionUtils.isEmpty(keeperInfoList)){
                List<String> isGridManagerNameList = keeperInfoList.stream().filter(a ->
                        Constants.ONE.equals(a.getKeeperType())).map(GridKeeperVO::getKeeperName).collect(Collectors.toList());//网格长列表
                List<String> noGridManagerNameList = keeperInfoList.stream().filter(a ->
                        !Constants.ONE.equals(a.getKeeperType())).map(GridKeeperVO::getKeeperName).collect(Collectors.toList());//网格员列表
                vo.setIsGridManagerList(isGridManagerNameList);
                vo.setNoGridManagerList(noGridManagerNameList);
                if(Constants.TWO.equals(vo.getTaskStatus())){//待办任务返回toDoFlag标识（当前登录人的待办）
                    String curKeeperId = "";
                    //根据返回的网格员id查询网格员
                    GridKeeperVO toDoGridKeeperVO = new GridKeeperVO();
                    SysUser user = SecurityUtils.getLoginUser().getUser();
                    toDoGridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
                    toDoGridKeeperVO.setTenantId(user.getTenantId());
                    toDoGridKeeperVO.setIndustryCode(user.getIndustryCode());
                    GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(toDoGridKeeperVO);
                    if(!ObjectUtil.isEmpty(keeper)){
                        curKeeperId = keeper.getKeeperId().toString();
                        if(vo.getKeeperId().contains(curKeeperId)){
                            //设置标识
                            vo.setToDoFlag(Constants.ONE);
                        }
                    }
                }
            }
        }
        if(Constants.ONE.equals(vo.getTaskStatus())){//“待完成”状态的任务详情不展示巡查反馈信息,已完成的任务详情才展示巡查反馈信息
            vo.setFjId(dealUrlCommon(vo.getFjId()));
            vo.setVideoUrl(dealUrlCommon(vo.getVideoUrl()));
            vo.setCoverUrl(dealUrlCommon(vo.getCoverUrl()));
            //add by liuxt video-file改造
            vo.setVoiceUrl(fileUrlEncryptUtil.dealObsFileUrl(vo.getVoiceUrl()));
            return getTrailStartEndTimeCommmon(vo);
        }
        return Result.data(vo);
    }



    /**
     * 获取轨迹起止时间共通方法
     * @Author：liuxt
     * @param vo
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<InspectionTaskVO> getTrailStartEndTimeCommmon(InspectionTaskVO vo){
        //巡查轨迹为如下起止时间内的轨迹：
        //	（1）如果巡查反馈时间在巡查时段结束时间之后，则为巡查时段开始时间到巡查反馈时间的轨迹；
        //	如果巡查反馈时间已经超过巡查日期 (非当天反馈) ，则为巡查时段开始时间到巡查日期的 23:59:59
        //	（2）如果巡查反馈时间晚于巡查时段开始时间、早于巡查时段结束时间，则为巡查时段开始时间至巡查反馈时间内的轨迹；
        //	（3）如果巡查反馈时间早于巡查时段开始时间，或者巡查时段为空，则为任务下发时间到巡查反馈时间的轨迹。
        //	如果巡查反馈时间已经超过巡查日期(非当天反馈) ，则为任务下发时间到巡查日期的 23:59:59。
        String createTime = vo.getCreateTime();//任务下发时间
        String inspectionTime = vo.getInspectionTime();//巡查反馈时间
        String createDate = "";//巡查日期
        String inspectionEndTime = "";//巡查时段开始时间(带日期)
        String inspectionStartTime = "";//巡查时段结束时间(带日期)
        String todayTime = "";//巡查日期的 23:59:59
        if(!StringUtils.isEmpty(createTime)){
            createDate = vo.getCreateTime().substring(Constants.NUMBER_ZERO,Constants.NUMBER_TEN);
        }else{
            return Result.fail(Constants.CREATE_TIME_NOT_NULL_INFO);
        }
        if(!StringUtils.isEmpty(vo.getInspectionStartTime())){
            inspectionStartTime = createDate + " " + vo.getInspectionStartTime();//巡查时段开始时间
        }
        if(!StringUtils.isEmpty(vo.getInspectionEndTime())){
            inspectionEndTime = createDate + " " + vo.getInspectionEndTime();//巡查时段结束时间
        }
        if(!StringUtils.isEmpty(inspectionTime)){//巡查反馈时间不为空
            if(!inspectionTime.substring(Constants.NUMBER_ZERO,Constants.NUMBER_TEN).equals(createDate)){
                todayTime = createDate + Constants.H_M_S;
            }
            if(StringUtils.isEmpty(inspectionStartTime) || StringUtils.isEmpty(inspectionEndTime) || DateUtils.parseDate(inspectionTime).before(DateUtils.parseDate(inspectionStartTime)) ){
                vo.setTrailStartTime(createTime);
                if(!StringUtils.isEmpty(todayTime)){
                    vo.setTrailEndTime(todayTime);
                }else{
                    vo.setTrailEndTime(inspectionTime);
                }
            }else if(DateUtils.parseDate(inspectionTime).after(DateUtils.parseDate(inspectionStartTime)) && DateUtils.parseDate(inspectionTime).before(DateUtils.parseDate(inspectionEndTime))){
                vo.setTrailStartTime(inspectionStartTime);
                vo.setTrailEndTime(inspectionTime);
            }else if(DateUtils.parseDate(inspectionTime).after(DateUtils.parseDate(inspectionEndTime)) ){
                vo.setTrailStartTime(inspectionStartTime);
                if(!StringUtils.isEmpty(todayTime)){
                    vo.setTrailEndTime(todayTime);
                }else{
                    vo.setTrailEndTime(inspectionTime);
                }
            }
        }

        return Result.data(vo);
    }





    public String dealUrlCommon(String url){
        String urlString = "";
        if(!StringUtil.isEmpty(url)){
            List<String> urlArray = Arrays.asList(url.split(Constants.COMMA));
            //add by liuxt video-file改造
            Map<String, String> urlList = fileUrlEncryptUtil.dealObsFileUrlBatch(urlArray);
            if (!CollectionUtils.isEmpty(urlList)) {
                for(String i : urlList.keySet()){
                    urlString +=urlList.get(i) + Constants.COMMA;
                }
            }
            urlString = urlString.substring(0,urlString.length()-1);
        }
        return urlString;
    }




    /**
     * 查询巡查轨迹
     * @Author：liuxt
     * @param @param taskI
     * @return Result<InspectionTaskVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<List<List<GridKeeperTrailNew>>> getTaskTrail(InspectionTaskNoPageParam inspectionTaskNoPageParam){
        String[] keeperIdList = inspectionTaskNoPageParam.getKeeperIdList();
        String taskId = inspectionTaskNoPageParam.getTaskId();
        String flag = inspectionTaskNoPageParam.getFlag();//页面标识 1为待办 2为已办  1需要前台传轨迹开始时间和轨迹结束时间两个参数 2不需要前台传参
        log.info(Constants.TASK_INFO_ID + taskId);
        InspectionTaskVO vo = bdmInspectionDAO.getTaskInfo(taskId);
        log.info(Constants.TASK_INFO_VO + JSON.toJSONString(vo));
        QueryTrailsParam queryTrailsParam = new QueryTrailsParam();
        queryTrailsParam.setKeeperIdList(keeperIdList);
        if(ObjectUtil.isNotEmpty(vo)) {
            if(Constants.TWO.equals(flag)){//已完成的任务后台计算轨迹开始时间和轨迹结束时间
                Result<InspectionTaskVO>  result = getTrailStartEndTimeCommmon(vo);
                if (!ObjectUtils.isEmpty(result.getData())) {
                    queryTrailsParam.setStartTime(result.getData().getTrailStartTime());
                    queryTrailsParam.setEndTime(result.getData().getTrailEndTime());
                }
            }else if(Constants.ONE.equals(flag)){//未完成的任务前台传轨迹开始时间和轨迹结束时间
                if(StringUtils.isEmpty(inspectionTaskNoPageParam.getTrailStartTime()) || StringUtils.isEmpty(inspectionTaskNoPageParam.getTrailEndTime())){
                    return Result.fail(Constants.TRAIL_TIME_NOT_NULL_INFO);
                }
                queryTrailsParam.setStartTime(inspectionTaskNoPageParam.getTrailStartTime());
                queryTrailsParam.setEndTime(inspectionTaskNoPageParam.getTrailEndTime());
            }
        }
        return this.getKeeperTrail(queryTrailsParam);
    }



    /**
     * 网格员轨迹获取
     * @Author：zl
     * @param queryTrailsParam
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    public Result<List<List<GridKeeperTrailNew>>> getKeeperTrail(QueryTrailsParam queryTrailsParam) {

        List<List<GridKeeperTrailNew>> returnList = new ArrayList<>();
        List<String> keeperIdList = Arrays.asList(queryTrailsParam.getKeeperIdList());
        GridKeeperVO vo = new GridKeeperVO();
        vo.setKeeperIdList(keeperIdList);
        List<GridKeeperVO> keeperInfoList = bdmInspectionDAO.getKeeperInfo(vo);
        if(!CollectionUtils.isEmpty(keeperInfoList)){
            QueryEntity query=new QueryEntity(Constants.GRID_KEEPER_TRAIL,GridKeeperTrail.class);
            query.in(Constants.KEEPER_ID,queryTrailsParam.getKeeperIdList());
            if(org.apache.commons.lang.StringUtils.isNotEmpty(queryTrailsParam.getStartTime()) && org.apache.commons.lang.StringUtils.isNotEmpty(queryTrailsParam.getEndTime())){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATE);
                try {
                    Date start = simpleDateFormat.parse(queryTrailsParam.getStartTime());
                    Date end = simpleDateFormat.parse(queryTrailsParam.getEndTime());
                    query.between(Constants.CREATE_TIME,start,end);
                    SortEntity sort=new SortEntity();
                    sort.asc(Constants.CREATE_TIME);
                    List<GridKeeperTrail> sourceList= bmKeeperTrailDAO.findList(query,sort, MongoDBType.PRIMARY);
                    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMATE);
                    List<GridKeeperTrailNew> list = sourceList.stream().map(v -> {
                        return new GridKeeperTrailNew(v.getKeeperId(), v.getLongitude(),v.getLatitude(),sdf.format(v.getCreateTime()),v.getTenantId(),
                                v.getIndustryCode(),v.getAppVerCode(),v.getKeeperName(),v.getKeeperType());
                    }).collect(Collectors.toList());//转换格式 转为api-bdm的实体类

                    if(!CollectionUtils.isEmpty(list)){
                        for (GridKeeperTrailNew gridKeeperTrailNew : list) {//list添加网格员姓名和网格员类型
                            for (GridKeeperVO gridKeeperVO : keeperInfoList) {
                                if(gridKeeperTrailNew.getKeeperId().equals(gridKeeperVO.getKeeperId().toString())){//用网格员id判断万无一失，因为网格员名称会有重复的情况
                                    gridKeeperTrailNew.setKeeperName(gridKeeperVO.getKeeperName());
                                    gridKeeperTrailNew.setKeeperType(gridKeeperVO.getKeeperType());
                                }
                            }
                        }
                        Map<String, List<GridKeeperTrailNew>> groupedItems = list.stream()
                                .collect(Collectors.groupingBy(GridKeeperTrailNew::getKeeperId));
                        for (Map.Entry<String, List<GridKeeperTrailNew>> entry : groupedItems.entrySet()) {//转换为前端需要的格式
                            returnList.add(entry.getValue());
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.data(returnList);
    }

    

    /**
     * app端/pc端获取巡查任务
     * @Author：liuxt
     * @param inspectionTaskParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "a.keeper_id")
    public List<InspectionTaskListVO> getTaskListByKeeper(InspectionTaskParam inspectionTaskParam){
        log.info("获取巡查任务:" + JSON.toJSONString(inspectionTaskParam));
        String flag = inspectionTaskParam.getFlag();
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return new ArrayList<>();
        }
        //  如果参数传递了，就优先使用传
        if (StringUtils.isEmpty(inspectionTaskParam.getKeeperId())) {
            inspectionTaskParam.setKeeperId(keeper.getKeeperId().toString());
        }
        inspectionTaskParam.setDeleteFlag(Constants.NUMBER_ZERO);
        if(Constants.TWO.equals(flag) || Constants.THREE.equals(flag) ){
            inspectionTaskParam.setFeedbackKeeperId(keeper.getKeeperId().toString());
        }
        Integer pageNum = inspectionTaskParam.getPageNum();
        Integer pageSize = inspectionTaskParam.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, true, false, null);
        }
        List<InspectionTaskListVO> list = bdmInspectionDAO.getTaskList(inspectionTaskParam);
        return list;
    }


    /**
     * 我的待办
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "a.keeper_id")
    public List<InspectionTaskListVO> getToDoTaskList(InspectionTaskNoPageParam inspectionTaskNoPageParam){
        log.info("获取巡查任务:" + JSON.toJSONString(inspectionTaskNoPageParam));
        //查询当前待办
        inspectionTaskNoPageParam.setTaskStatus(Constants.TWO);
        inspectionTaskNoPageParam.setFlag(Constants.ONE);
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return new ArrayList<>();
        }
        inspectionTaskNoPageParam.setKeeperId(keeper.getKeeperId().toString());
        inspectionTaskNoPageParam.setDeleteFlag(Constants.NUMBER_ZERO);
        List<InspectionTaskListVO> list = bdmInspectionDAO.getToDoTaskList(inspectionTaskNoPageParam);
        if(!CollectionUtils.isEmpty(list)){
            //根据返回的网格员id查询网格员
            if(!CollectionUtils.isEmpty(list)){
                return getGridManagerListCommon(Constants.TWO,list);
            }
        }
        return list;
    }

    /**
     * 行业大屏获取巡查任务列表
     * @Author：liuxt
     * @param inspectionTaskNoPageParam
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code", gridColumn = "a.grid_id", gridKeeperColumn = "a.keeper_id")
    public List<InspectionTaskListVO> getTaskListByCondition(InspectionTaskNoPageParam inspectionTaskNoPageParam){
        log.info("行业大屏获取巡查任务列表:" + JSON.toJSONString(inspectionTaskNoPageParam));
        List<InspectionTaskListVO> list = bdmInspectionDAO.getTaskListByCondition(inspectionTaskNoPageParam);
        //根据返回的网格员id查询网格员
        if(!CollectionUtils.isEmpty(list)){
            return getGridManagerListCommon(Constants.ONE,list);
        }
        return list;
    }


    /**
     * 获取网格长列表/网格员列表共通方法
     * @Author：liuxt
     * @param list
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<InspectionTaskListVO> getGridManagerListCommon(String flag , List<InspectionTaskListVO> list){
        String curKeeperId = "";
        if(Constants.ONE.equals(flag)){
            //根据返回的网格员id查询网格员
            GridKeeperVO gridKeeperVO = new GridKeeperVO();
            SysUser user = SecurityUtils.getLoginUser().getUser();
            gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
            gridKeeperVO.setTenantId(user.getTenantId());
            gridKeeperVO.setIndustryCode(user.getIndustryCode());
            GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
            if(!ObjectUtil.isEmpty(keeper)){
                curKeeperId = keeper.getKeeperId().toString();
            }
        }
        String keeperIdStr = "";
        for (int i = 0; i < list.size() ; i++) {
            keeperIdStr = keeperIdStr + list.get(i).getKeeperId() + Constants.COMMA;
        }
        keeperIdStr = keeperIdStr.substring(Constants.ZERO_NUM,keeperIdStr.length()-Constants.ONE_NUM );
        List<String> keeperIdList = Arrays.asList(keeperIdStr.split(Constants.COMMA));
        GridKeeperVO vo = new GridKeeperVO();
        vo.setKeeperIdList(keeperIdList);
        List<GridKeeperVO> keeperInfoList = bdmInspectionDAO.getKeeperInfo(vo);
        if(!CollectionUtils.isEmpty(keeperInfoList)){
            List<GridKeeperVO> isGridManagerList = keeperInfoList.stream().filter(a ->
                    Constants.ONE.equals(a.getKeeperType())).collect(Collectors.toList());//网格长列表
            List<GridKeeperVO> noGridManagerList = keeperInfoList.stream().filter(a ->
                    !Constants.ONE.equals(a.getKeeperType())).collect(Collectors.toList());//网格员列表
            for (int i = 0; i < list.size() ; i++) {
                InspectionTaskListVO task = list.get(i);
                List<String> curKeeperIdList = Arrays.asList(task.getKeeperId().split(Constants.COMMA));
                List<String> isGridManagerNameList = new ArrayList<>();//要返回的网格长名称列表
                List<String> noGridManagerNameList = new ArrayList<>();//要返回的网格员名称列表
                for (int j = 0; j < curKeeperIdList.size(); j++) {
                    String keeperId = curKeeperIdList.get(j);
                    for (GridKeeperVO isGridManager : isGridManagerList) {
                        if(keeperId.equals(isGridManager.getKeeperId().toString())){//用网格员id判断万无一失，因为网格员名称会有重复的情况
                            isGridManagerNameList.add(isGridManager.getKeeperName());
                        }
                    }
                    for (GridKeeperVO noGridManager : noGridManagerList) {
                        if(keeperId.equals(noGridManager.getKeeperId().toString())){//用网格员id判断万无一失，因为网格员名称会有重复的情况
                            noGridManagerNameList.add(noGridManager.getKeeperName());
                        }
                    }
                }
                if(Constants.ONE.equals(flag) && !StringUtils.isEmpty(curKeeperId) && task.getKeeperId().contains(curKeeperId)){
                    //设置标识
                    task.setToDoFlag(Constants.ONE);
                }
                task.setIsGridManagerList(isGridManagerNameList);
                task.setNoGridManagerList(noGridManagerNameList);
            }
        }
        return list;
    }

    /**
     * 认领任务
     * @Author：zl
     * @param taskId
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result claimTask(String taskId){
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(user.getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        InspectionTaskVO vo = new InspectionTaskVO();
        vo.setTaskId(taskId);
        vo.setKeeperId(keeper.getKeeperId().toString());
        vo.setKeeperName(keeper.getKeeperName());
        vo.setTaskStatus(Constants.TWO);
        return Result.condition(bdmInspectionDAO.updateTask(vo) > Constants.NUMBER_ZERO);

    }

    /**
     * 处理巡查任务
     * @Author：zl
     * @param inspectionTaskParamVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    @Transactional(rollbackFor = Exception.class)
    public Result claimTask(InspectionTaskParamVO inspectionTaskParamVO){
        log.info("claimTask入参：{}", JSON.toJSONString(inspectionTaskParamVO));
        if(StringUtils.isNotEmpty(inspectionTaskParamVO.getTaskId())){
            InspectionTaskVO inspectionTaskVO = bdmInspectionDAO.getTaskInfo(inspectionTaskParamVO.getTaskId());
            if(Constants.ONE.equals(inspectionTaskVO.getTaskStatus())){
                return Result.fail(Constants.TASK_COMPLETED_INFO);
            }
        }else{
            return Result.fail(Constants.TASK_ID_NOT_NULL_INFO);
        }
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        gridKeeperVO.setUserId(bdmGridKeeperDAO.getUserByLoginName(user.getUserName()).getUserId());
        gridKeeperVO.setTenantId(user.getTenantId());
        gridKeeperVO.setIndustryCode(user.getIndustryCode());
        GridKeeperVO keeper = bdmGridKeeperDAO.getKeeperIdByUserId(gridKeeperVO);
        if(ObjectUtil.isEmpty(keeper)){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        InspectionTaskVO vo = new InspectionTaskVO();
        List<InspectionFjldParam> fjIdList = inspectionTaskParamVO.getFjId();
        vo.setFjId(dealListCommon(null,fjIdList,""));
        //add by liuxt 巡查任务需求 支持多视频
        List<InspectionVideoParam> videoUrlList = inspectionTaskParamVO.getVideoUrlList();
        vo.setVideoUrl(dealListCommon(videoUrlList,null,Constants.ONE));
        vo.setCoverUrl(dealListCommon(videoUrlList,null,Constants.TWO));
        if(!StringUtils.isEmpty(inspectionTaskParamVO.getVoiceUrl())) {
            vo.setVoiceUrl(inspectionTaskParamVO.getVoiceUrl());
        }
        vo.setTaskId(inspectionTaskParamVO.getTaskId());
        vo.setTaskStatus(inspectionTaskParamVO.getTaskStatus());
        vo.setRetroaction(inspectionTaskParamVO.getRetroaction());
        vo.setFeedbackEnd(inspectionTaskParamVO.getFeedbackEnd());
        vo.setFeedbackKeeperId(keeper.getKeeperId().toString());
        vo.setIndustryCode(keeper.getIndustryCode());
        log.info("调用updateTask入参：{}", JSON.toJSONString(vo));
        int i = bdmInspectionDAO.updateTask(vo);
        if (i > Constants.NUMBER_ZERO){
            //获取conf_id 获取相同conf_id的task任务的状态
            List<InspectionTaskParamVO> taskStatusInfo = bdmInspectionDAO.getTaskStatus(vo.getTaskId());
            //判断集合中有无 2-未完成状态的数据
            List<InspectionTaskParamVO> collect = taskStatusInfo.stream().filter((item) -> Constants.TWO.equals(item.getTaskStatus())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)){
                //没有2未完成的，去修改
                String changStatusConfId = taskStatusInfo.get(Constants.NUMBER_ZERO).getConfId();
                //修改conf_status
                bdmInspectionDAO.updateConfStatus(changStatusConfId);
            }
        }
        return Result.success(Constants.GOOD);
    }



    /**
     * 查询巡查人列表
     * @Author：zl
     * @param inspectionTaskVO
     * @return List<InspectionTaskListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public Result<List<InspectionTaskVO>> getUserInfoList(InspectionTaskVO inspectionTaskVO) {
        List<InspectionTaskVO> userList = bdmInspectionDAO.getUserInfoList(inspectionTaskVO);
        if (!CollectionUtils.isEmpty(userList)) {
            for (int i = 0; i < userList.size(); i++) {
                //手机号加密
                userList.get(i).setKeeperPhoneNumber(AESUtil.encrypt(userList.get(i).getKeeperPhoneNumber()));
            }
            Set<String> fileUrls = userList.stream().map(InspectionTaskVO::getAvatar).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(fileUrls)){
                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                //set加密后的url
                userList.stream().forEach(item -> item.setAvatar(result.get(item.getAvatar())));
            }
        }
        return Result.data(userList);
    }


    public String dealListCommon(List<InspectionVideoParam> videoUrlList,List<InspectionFjldParam> fjIdList,String flag){
        String urlString = "";
        StringBuilder stringBuilder = new StringBuilder();
        if(!CollectionUtils.isEmpty(videoUrlList)){
            for (InspectionVideoParam inspectionVideoUrlParam : videoUrlList) {
                if(Constants.ONE.equals(flag)){
                    stringBuilder.append(inspectionVideoUrlParam.getFileVideoUrl()).append(Constants.COMMA);
                }else if(Constants.TWO.equals(flag)){
                    stringBuilder.append(inspectionVideoUrlParam.getCoverUrl()).append(Constants.COMMA);
                }
            }
            urlString = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(Constants.COMMA)).toString();
        }
        if(!CollectionUtils.isEmpty(fjIdList)){
            for (InspectionFjldParam inspectionFjldParam : fjIdList) {
                stringBuilder.append(inspectionFjldParam.getTagPictureUrl()).append(Constants.COMMA);
            }
            urlString = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(Constants.COMMA)).toString();
        }
        return urlString;
    }

    /**
     * 获取租户-网格-组织-设备下的行政区域
     * @Author：zl
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public List<DeviceGridAreaTreeVO> queryGridAreaTree(DeviceGridReqDTO deviceGridReqDTO) {
//        deviceGridReqDTO.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        return bdmInspectionDAO.queryGridAreaTree(deviceGridReqDTO);
    }

    /**
     * 按日期统计用户巡查次数趋势
     * @Author：zl
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__按日期统计用户巡查次数趋势
     */
    public List<InspectionCountResponse> inspectionCountTrend(QueryCountTrend queryCountTrend) {
        log.info("=======按日期统计用户巡查次数趋势========={}", JSON.toJSONString(queryCountTrend));

        Map searchMap = bdmInspectionDAO.inspectionCountTrend(queryCountTrend);

        //获取日期集合
        List<String> labelString = this.getRangeDate(queryCountTrend.getBeginTime(), queryCountTrend.getEndTime());

        List<InspectionCountResponse> list = new ArrayList<>();
        //构建数据
        this.structureRangeDate(list, labelString, searchMap);

        return list;
    }

    /**
     * 遍历数据
     *
     * @param list
     * @param labelString
     * @param searchMap
     */
    public void structureRangeDate(List<InspectionCountResponse> list, List<String> labelString, Map<String, Map> searchMap) {
        InspectionCountResponse vo;
        for (String s : labelString) {
            vo = new InspectionCountResponse();
            vo.setDate(s);
            Integer num = 0;
            if (null != searchMap) {
                Map map = searchMap.get(s);
                if(null != map){
                    num = ((Long) map.get("sum")).intValue();
                }
            }
            vo.setTodayAll(num == null ? 0 : num);
            list.add(vo);
        }
    }

    public List<String> getRangeDate(String startTime, String endTime) {
        //定义时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<String> list = new ArrayList<>();
        try {
            // 转化成日期类型
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime() <= endDate.getTime()) {
                // 把日期添加到集合
                list.add(simpleDateFormat.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                // 获取增加后的日期
                startDate = calendar.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 编辑网格员名称时，同步更新巡查人的名称
     * @Author：zhaozj
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public void synchUpdateKeeperName(InspectionTaskVO vo) {
        log.info("synchUpdateKeeperName入参对象：{}", JSON.toJSONString(vo));
        if (StringUtils.isNotEmpty(vo.getKeeperId()) && StringUtils.isNotEmpty(vo.getKeeperName())) {
            //查询该网格员所在的有效巡查
            List<InspectionTaskVO> confKeeperList = bdmInspectionDAO.getConfKeeperList(vo);
            List<InspectionTaskVO> taskKeeperList = bdmInspectionDAO.getTaskKeeperList(vo);
            //网格员名称替换处理
            List<InspectionTaskVO> confList = keeperNameHandle(confKeeperList, vo);
            List<InspectionTaskVO> taskList = keeperNameHandle(taskKeeperList, vo);
            //更新巡查表网格员名称字段
            if (!CollectionUtils.isEmpty(confList)){
                bdmInspectionDAO.updateConfKeeperList(confList);
            }
            if (!CollectionUtils.isEmpty(taskList)){
                bdmInspectionDAO.updateTaskKeeperList(taskList);
            }
        }
    }

    /**
     * 网格员名称替换处理
     * @Author：zhaozj
     * @return TableDataInfo
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__巡查任务相关接口
     */
    public List<InspectionTaskVO> keeperNameHandle(List<InspectionTaskVO> keeperList,InspectionTaskVO vo) {
        for (InspectionTaskVO task : keeperList) {
            if (StringUtils.isNotEmpty(task.getKeeperId()) && StringUtils.isNotEmpty(task.getKeeperName())) {
                List<String> keeperArr = Arrays.asList(task.getKeeperId().split(Constants.COMMA));
                List<String> nameArr = Arrays.asList(task.getKeeperName().split(Constants.COMMA));
                if (nameArr.size() > keeperArr.indexOf(vo.getKeeperId())){
                    nameArr.set(keeperArr.indexOf(vo.getKeeperId()), vo.getKeeperName());
                    task.setKeeperName(String.join(Constants.COMMA, nameArr));
                }
            }
        }
        return keeperList;
    }



}
