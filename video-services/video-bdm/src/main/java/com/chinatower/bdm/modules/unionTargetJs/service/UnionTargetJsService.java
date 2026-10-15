package com.chinatower.bdm.modules.unionTargetJs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.bdm.api.entity.UnionTargetJsParam;
import com.chinatower.bdm.modules.unionTargetJs.entity.ResultEntity;
import com.chinatower.bdm.modules.unionTargetJs.utils.UnionTargetJsCommonUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频查询服务类
 * Created by ldl on 2022/1/27.
 */
@Service
@Slf4j
public class UnionTargetJsService {
    @Autowired
    private UnionTargetJsCommonService unionTargetJsCommonService;
    @Resource
    private NetConnectUtils connectUtils;

    public Result targetFollow(UnionTargetJsParam param) {

        Result res = Result.fail("光电联动信令下发失败！");
        try{
            NetConnectUtils.RequestParamter<UnionTargetJsParam> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            parameter.setBody(param);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TARGET_FOLLOW);
            parameter.setPath(path);
            ResultEntity resultEntity = connectUtils.post(parameter, ResultEntity.class);

            // 获取返回日志
            log.info(Constants.TARGETFOLLOW_OP + Constants.RESPONSE_INTERFACE_LOG + JSON.toJSONString(resultEntity));
            res = dealRes(resultEntity);

        } catch (Exception e) {
            log.error("UnionTargetJsService targetFollow error:", e);
        }
        return res;
    }

    /**
     * 接口返回数据
     *
     *
     * @param cr
     * @param
     * @return
     */
    private Result dealRes(ResultEntity cr) {
        // 返回对象
        Result res;
        // 释放控制成功
        if (UnionTargetJsCommonUtil.crIsSuccess(cr)) {
            res = Result.success(Constants.SUCCESS_MSG);
        } else {
            res = Result.fail(Constants.TARGETFOLLOW_OP + "操作失败:"+cr.getMsg().toString());
        }
        return res;
    }


}
