package com.chinatower.event.controller;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.dto.InterceptDTO;
import com.chinatower.event.entity.dto.IntercepiResultVo;
import com.chinatower.event.entity.dto.ExportOrderInfoReq;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.InterceptConfigDTO;
import com.chinatower.event.repository.IWarningInterceptRepository;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * <p>
 * 告警拦截信息表 前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/intercept")
public class WarningInterceptController extends TowerBaseController {

    @Autowired
    private IWarningInterceptRepository warningInterceptRepository;

    /**
     * 功能描述：回收站列表
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getInterceptList")
    public TableDataInfo getInterceptList(@RequestBody WarningInterceptDTO dto) {
        log.info("WarningInterceptController-warningOrderInfoRel-req:{}", JSON.toJSONString(dto));
        TableDataInfo result = new TableDataInfo();
        startPage();
        IntercepiResultVo resultVo = warningInterceptRepository.getInterceptList(dto);
        if(resultVo == null){
            result.setCode(200);
            result.setTotal(0);
            return result;
        }
        if(!Constants.ONE.equals(resultVo.getFlag())){
            if(CollectionUtils.isEmpty(resultVo.getResultList())){
                result.setCode(200);
                result.setTotal(0);
                return result;
            }else{
                return getDataTable(resultVo.getResultList());
            }
        }else{
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(200);
            rspData.setRows(resultVo.getResultList());
            rspData.setTotal(resultVo.getTotal());
            return rspData;
        }
    }

    /**
     * 功能描述：获取回收站详情
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getInterceptInfo")
    public Result getInterceptInfo(@RequestBody WarningInterceptDTO dto) {
        List<Map> list = warningInterceptRepository.getInterceptInfo(dto);
        return result(list);
    }

    /**
     * 功能描述：获取回收站详情Api
     *
     * @param req
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getInterceptInfoApi")
    public Result getInterceptInfoApi(@RequestBody ExportOrderInfoReq req) {
        WarningInterceptDTO dto = new WarningInterceptDTO();
        dto.setOrderId(req.getWarningOrderId());
        List<Map> list = warningInterceptRepository.getInterceptInfo(dto);
        return Result.data(list.get(0));
    }

    /**
     * 功能描述：真警处理
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @PostMapping("/updateIntercept")
    public Result updateIntercept(@RequestBody InterceptDTO dto) throws InvocationTargetException, IllegalAccessException {
        return warningInterceptRepository.updateIntercept(dto);
    }

    /**
     * 功能描述：真警处理(还原告警数据)
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:42
     * @version:
     */
    @PostMapping("/restoreWarningOrder")
    public Result restoreWarningOrder(@RequestBody InterceptDTO dto){
        return warningInterceptRepository.restoreWarningOrder(dto);
    }

    /**
     * 功能描述：生成告警压制
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:44
     * @version:
     */
    @PostMapping("/createIntercept")
    @TowerLog(title = "研判压制", businessType = BusinessType.INSERT)
    public Result createIntercept(@RequestBody InterceptConfigDTO dto) {
//        log.info("生成告警压制入参:{}", JSON.toJSONString(dto));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        SysUser sysUser = loginUser.getUser();
        if (StringUtils.isEmpty(dto.getDeviceCode())) {
            return Result.fail("deviceCode不能为空！");
        }
        if (StringUtils.isEmpty(dto.getWarningTypeId())) {
            return Result.fail("warningTypeId不能为空！");
        }
        if (0 >= dto.getInterceptTime()) {
            return Result.fail("压制时间不能为空！");
        }
        else if (43200 < dto.getInterceptTime()) {
            return Result.fail("压制时间不能超过30天！");
        }
//        if (CollectionUtils.isEmpty(dto.getAppVerCodeList())) {
//            return Result.fail("appVerCodeList不能为空！");
//        }
        String key = Constants.INTERCEPT_CONFIG + sysUser.getTenantId() + "_" + sysUser.getIndustryCode() + "_" + dto.getDeviceCode() + "_" + dto.getWarningTypeId();
        RedisUtil.set(key, dto, RedisDBType.PRIMARY);
        RedisUtil.expire(key, dto.getInterceptTime(), TimeUnit.MINUTES, RedisDBType.PRIMARY);
        return Result.success(ResultCode.SUCCESS);
    }
}
