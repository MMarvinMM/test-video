package com.chinatower.video.modules.common.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.api.entity.BaseErrorRecordVO;
import com.chinatower.video.api.entity.CommonResultEntiry;
import com.chinatower.video.api.entity.VideoBaseControlReq;
import com.chinatower.video.api.entity.VideoBaseReq;
import com.chinatower.video.modules.common.dao.VideoCommonDAO;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.enums.TableNameEnum;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.videocontrol.dto.SysUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * video-video公共服务类
 * Created by zhujun on 2022/1/14.
 */
@Service
@Slf4j
public class VideoCommonService {

    @Value("${video-video.appCode}")
    private String appCode;


    @Autowired
    private VideoCommonDAO videoCommonDAO;


    private static final String ERROR_CODE = "errorCode_";

    private static final String ERROR_CODE_CATCH_FOR_LOG = "错误编码获取";

    private static final String RESULT_CODE = "resultCode";
    private static final String RESULT_MSG = "resultMsg";

    private static final String MYSQL = " Mysql:";
    private static final String REDIS = " Redis:";



    public Map<String, String> checkDeviceAuth(VideoBaseControlReq baseReq) {
        VideoBaseReq req = new VideoBaseReq();
        req.setDeviceCode(baseReq.getDeviceCode());
        req.setChannelCode(baseReq.getChannelCode());
        return checkDeviceAuth(req);
    }

    //@TenantForDataScope(deviceAlias = "t.device_code")
    @DataScope(deviceColumn = "t.device_code", acceptColumn="t.accept_node", tenantColumn = "t.tenant_id")
    public Map<String, String> checkDeviceAuth(VideoBaseReq req) {
        String tableName = TableNameEnum.getTableName(req.getDeviceType());
        List<Map<String, String>> list = videoCommonDAO.queryOrderDeviceByTableName(req, tableName);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    public Map<String, String> checkDeviceNoAuth(VideoBaseControlReq baseReq) {
        VideoBaseReq req = new VideoBaseReq();
        req.setDeviceCode(baseReq.getDeviceCode());
        req.setChannelCode(baseReq.getChannelCode());
        return checkDeviceNoAuth(req);
    }


    /**
     * 只需判断是否订购，不加切面权限
     * @param req
     * @return
     */
    public Map<String, String> checkDeviceNoAuth(VideoBaseReq req) {
        req.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        String tableName = TableNameEnum.getTableName(req.getDeviceType());
        List<Map<String, String>> list = videoCommonDAO.queryOrderDeviceByTableNameNoAuth(req, tableName);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 根据错误编码返回对应名称
     *
     * @return
     */
    public String getErrorMsg(String exceptionMsg, String defaultTitle) {
    	return defaultTitle;
    }

    /**
     * 根据错误编码返回对应名称
     *
     * @return
     */
    public String getErrorMsg(CommonResultEntiry cr, String defaultTitle) {
        if(cr.getReturnCode() == null){
            Map<String,String> returnData = new HashMap<>();
            returnData.put(RESULT_CODE, DeviceConstants.ERORR_CODE);
            returnData.put(RESULT_MSG, defaultTitle);
            return JSON.toJSONString(returnData);
        }

        String errorCode = VideoCommonUtil.getOmErrorCode(cr);
        String sysDictData;
        String msg = null;
        if (StringUtils.isNotEmpty(errorCode)) {
            Boolean hasKey = RedisUtil.hasKey(ERROR_CODE+errorCode, RedisDBType.PRIMARY);
            //判读redis中是否存在该错误码
            if(hasKey){
                msg = RedisUtil.get(ERROR_CODE+errorCode,RedisDBType.PRIMARY);
                log.info(ERROR_CODE_CATCH_FOR_LOG + REDIS + errorCode + "_" + msg);
            } else {
                //如果不存在，从数据库获取放入redis
                sysDictData = videoCommonDAO.getErrorMsg(errorCode);
                if(sysDictData == null){
                    msg = Constants.DEFAULT_ERROR_MESSAGE;
                }else {
                    msg = sysDictData;
                }
                log.info(ERROR_CODE_CATCH_FOR_LOG + MYSQL + errorCode + "_" + msg);
                RedisUtil.set(ERROR_CODE+errorCode,msg,2, TimeUnit.DAYS,RedisDBType.PRIMARY);
            }
        }
        Map<String,String> returnData = new HashMap<>();
        returnData.put(RESULT_CODE, errorCode);
        returnData.put(RESULT_MSG, msg);
        return JSON.toJSONString(returnData);
    }

    /**
     * 根据错误编码返回对应名称
     *
     * @return
     */
    public String getErrorMsg(ResultEntity cr, String defaultTitle) {
        return getErrorMsgNew(cr,defaultTitle);
    }

    /**
     * 根据错误编码返回对应名称-南向有些接口跨平台受理会冲突，增加冲突的提示语
     *
     * @return
     */
    public String getErrorMsgNew(ResultEntity cr, String defaultTitle) {
        String errorCode = VideoCommonUtil.getOmErrorCode(cr);
        String sysDictData;
        String msg = null;
        if (StringUtils.isNotEmpty(errorCode)) {
            sysDictData = videoCommonDAO.getErrorMsg(errorCode);
            if(sysDictData == null){
                msg = cr.getErrorMsg();
                if(StringUtils.isEmpty(msg)){
                    msg = Constants.DEFAULT_ERROR_MESSAGE;
                }
            }else {
                msg = sysDictData;
            }
            log.info(ERROR_CODE_CATCH_FOR_LOG + MYSQL + errorCode + "_" + msg);
        }
        Map<String,String> returnData = new HashMap<>();
        returnData.put(RESULT_CODE, errorCode);
        returnData.put(RESULT_MSG, msg);
        return JSON.toJSONString(returnData);
    }

    private String currentDateToString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


    /**
     * 功能描述: 获取用户名称--脱敏处理
     * @param: userId
     * @return: String
     * @auther: zhaozj
     * @date: 2022/11/23
     * @version: release_v1.5.30
     */
    public SysUserDTO getUserName(SysUserDTO sysUserDTO) {
        return videoCommonDAO.getUserName(sysUserDTO);
    }

    /**
     * 添加基础报错信息记录(开启新事务，内部回滚，外部不回滚)
     * @param
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseErrorRecord(BaseErrorRecordVO vo) {
        videoCommonDAO.saveBaseErrorRecord(vo);
    }

    /**
     * 获取真实的巡航时段
     * 分钟数非10的倍数，则归为上一个时间段，例：获取的当前时间是00:06，则修正为00:00，当前时间是00:15，则修正为00:10
     * @return
     */
    public String getActualHour(){
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat(DeviceConstants.SIMPLE_DATE_FORMAT);
        String minuteLast = df.format(new Date()).substring(DeviceConstants.FIFTEEN_NUM, DeviceConstants.SIXTEEN_NUM);
        if(!minuteLast.equals(Constants.ZERO)){
            minuteLast = Constants.ZERO;
        }
        return df.format(new Date()).substring(DeviceConstants.ELEVEN_NUM, DeviceConstants.FIFTEEN_NUM) + minuteLast;
    }
}
