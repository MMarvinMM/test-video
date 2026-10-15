package com.chinatower.platform.project.node.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.AuthFeignClient;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.node.entity.PlatFormVO;
import com.chinatower.platform.project.node.entity.SwitchUserVO;
import com.chinatower.platform.project.node.mapper.NodeMapper;
import com.chinatower.platform.project.node.service.INodeService;
import com.chinatower.platform.project.sysbusiness.common.Constants;
import com.chinatower.platform.utils.CascadeConnectUtils;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.ReplaceTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class NodeServiceImpl implements INodeService {
    @Resource
    private NodeMapper noteMapper;
    @Autowired
    private CascadeConnectUtils cascadeConnectUtils;

    @Override
    public List<PlatFormVO> selectNodeByTenantId(PlatFormVO platFormVO) {
        return noteMapper.selectNodeByTenantId(platFormVO);
    }

    /**
     * 通过本级外部网关调用属地化模拟登录接口，返回加密串
     * @param replaceTokenVO
     * @return
     */
    @Override
    public Result<String> replaceToken(ReplaceTokenVO replaceTokenVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        replaceTokenVO.setTowerUser(loginUser);
        //来源属地编码
        String fromPlatformCode = loginUser.getUser().getPlatformCode();
        //目的地属地编码
        String toPlatformCode = replaceTokenVO.getPlatformCode();

        if(StringUtils.isEmpty(fromPlatformCode)){
            return Result.fail("平台编码未配置！");
        }

        SysUser sysUser = noteMapper.selectUserByUserName(replaceTokenVO.getUserName());
        if(ObjectUtils.isEmpty(sysUser)){
            return Result.fail("目标客户无效");
        }

        //目的地公钥加密
        String encryptStr = AESUtil.encrypt(JSONObject.toJSONString(replaceTokenVO));
        // 定义请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("encryptStr", encryptStr);

        Result result = RemoteNodeUtils.create(toPlatformCode)
                .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                .doSwitchLogin(param);

        log.info("doSwitchLogin-return" + JSONObject.toJSONString(result));

        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            return Result.data((String)result.getData());
        }
        return Result.fail(Constants.REPLACE_TOKEN_ERROR);
    }

    public boolean validateSwitchUser(SwitchUserVO dto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        user = noteMapper.selectUserByUserId(user.getUserId());
        if (user.getUserType().equalsIgnoreCase("0")) {
            SysDept dept = noteMapper.getDeptById(user.getDeptId());
            if (!dept.getDeptLevel().equalsIgnoreCase("1")) {
                SysDept deptById = noteMapper.getDeptById(user.getDeptId().substring(0, 6));
                List<SysDept> list = noteMapper.getDeptByCurrentDeptId(deptById);
                List<String> tenantIds = noteMapper.getTenantByTowerDeptId(list);
                StringBuffer sb = new StringBuffer(com.chinatower.platform.common.utils.StringUtils.format(" and {} in {} ", "u.tenant_id", joinParameter(tenantIds)));
                dto.setDataScope(sb.toString());
            } else {
                dto.setDataScope(null);
            }
        }else{
            return false;
        }
        Long aLong = noteMapper.validateSwitchUser(dto);
        return (aLong != null) && (aLong > 0);
    }

    private String joinParameter(List<String> deviceCodes) {
        if (deviceCodes == null || deviceCodes.isEmpty()) {
            return "('')";
        }
        StringBuffer deviceStrBuf = new StringBuffer();
        deviceStrBuf.append("(");
        for (String deviceCode : deviceCodes) {
            deviceStrBuf.append("'" + deviceCode + "',");
        }
        deviceStrBuf.deleteCharAt(deviceStrBuf.length() - 1);
        deviceStrBuf.append(")");
        return deviceStrBuf.toString();
    }
}

