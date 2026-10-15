package com.chinatower.stdu.common.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.stdu.common.dao.VideoCommonDAO;
import com.chinatower.stdu.common.entity.ResultEntity;
import com.chinatower.stdu.common.enums.TableNameEnum;
import com.chinatower.stdu.common.util.VideoCommonUtil;
import com.chinatower.video.api.entity.CommonResultEntiry;
import com.chinatower.video.api.entity.VideoBaseReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    private VideoCommonDAO videoCommonDAO;

    private static final String ERROR_CODE = "errorCode_";

    private static final String ERROR_CODE_CATCH_FOR_LOG = "错误编码获取";

    private static final String RESULT_CODE = "resultCode";
    private static final String RESULT_MSG = "resultMsg";

    private static final String MYSQL = " Mysql:";
    private static final String REDIS = " Redis:";



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

}
