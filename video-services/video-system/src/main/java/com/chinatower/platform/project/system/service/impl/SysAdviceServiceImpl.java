package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.AuthFeignClient;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.domain.SysAdvice;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.utils.HttpUtils;
import com.chinatower.platform.project.system.utils.RsaUtil;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.system.api.feign.client.CustomFeignClient;
import com.chinatower.system.api.model.FileDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.platform.project.system.domain.vo.DeptInfoVO;
import com.chinatower.platform.project.system.mapper.SysAdviceMapper;
import com.chinatower.platform.project.system.mapper.SysConfigMapper;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.service.ISysAdviceService;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysOrderAdviceVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

import static com.chinatower.platform.constants.Constants.PLATFORM_CODE;

@Service
@Slf4j
public class SysAdviceServiceImpl implements ISysAdviceService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysAdviceMapper sysAdviceMapper;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private NetConnectUtils connectUtils;

    private static final String CENTER_CODE = "10000000001";

    private static final String NEW_LANDBOOT_INNER = "newlandBootInner";

    @Value("${orderSystem.sk}")
    private String SECRET_KEY;
    @Value("${orderSystem.baseUrl}")
    private String BASE_URL;
    @Value("${orderSystem.ak}")
    private String APP_KEY;
    @Value("${orderSystem.methodUrl}")
    private String queryMethod;

    @Resource
    SystemFeignClient systemFeignClient;


    @Override
    public int insert(SysAdvice sysAdvice) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        SysUser user = sysUserMapper.selectUserOnlyByUserName(currentUser.getUserName());
        if (Objects.isNull(user)) {
            throw new CustomException("该用户信息为空");
        }
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserId(user.getUserId());
        String platformCode = currentUser.getPlatformCode();
        dto.setPlatformCode(platformCode);
        String userType = user.getUserType();
        boolean isTenant = VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userType);
        String provinceCode ="";
        if (isTenant) {
            dto.setIndustryCode(currentUser.getIndustryCode());
            // 租户信息查询订购省份信息
            List<String> businessOrgList = sysAdviceMapper.selectOrderCustomerOrgList(user.getTenantId());
            if (!CollectionUtils.isEmpty(businessOrgList)) {
                DeptInfoVO deptInfoVO = new DeptInfoVO();
                deptInfoVO.setDeptId(businessOrgList.get(0));
                List<DeptInfoVO> deptInfoVOList = sysAdviceMapper.selectDeptInfo(deptInfoVO);
                if (!CollectionUtils.isEmpty(deptInfoVOList)) {
                    String deptLevel = deptInfoVOList.get(0).getDeptLevel();
                     if (StringUtils.isNotEmpty(deptLevel) && "3".equals(deptLevel)) {
                        deptInfoVO.setDeptId(deptInfoVOList.get(0).getParentId());
                        List<DeptInfoVO> deptInfoVOList1 = sysAdviceMapper.selectDeptInfo(deptInfoVO);
                        if (!CollectionUtils.isEmpty(deptInfoVOList1)) {
                            provinceCode = deptInfoVOList1.get(0).getDeptId();
                        }
                    }else{
                        provinceCode = deptInfoVOList.get(0).getDeptId();
                    }
                }
            }else{
                provinceCode = currentUser.getTenantInfo().getProvinceId();
            }
        }else{
            //铁塔用户直接查询省份信息
            provinceCode = user.getDeptId();
        }
        SysUser sysUser = sysUserMapper.selectUserById(dto);
        if (Objects.nonNull(sysUser)) {
            sysAdvice.setPhoneNumber(sysUser.getPhonenumber());
            sysAdvice.setUserName(sysUser.getUserName());
            sysAdvice.setNickName(sysUser.getNickName());
            sysAdvice.setIndustryCode(sysUser.getIndustryCode());
            sysAdvice.setUserId(sysUser.getUserId());
            sysAdvice.setNodeCode(platformCode);
            sysAdvice.setProvince(provinceCode);
            if (isTenant) {
                sysAdvice.setTenantId(sysUser.getTenantId());
                sysAdvice.setTenantName(sysUser.getTenantName());
                sysAdvice.setIndustryName(sysUser.getIndustryName());
                sysAdvice.setAppVerCode(sysUser.getAppVerCode());
            }
        } else {
            log.error("新增意见反馈->查询用户信息为空:{}", JSON.toJSONString(dto));
        }
        if (!CollectionUtils.isEmpty(sysAdvice.getAdviceAttach())) {
            sysAdvice.setAdviceAttachJson(JSON.toJSONString(sysAdvice.getAdviceAttach()));
        }
        sysAdvice.setCreateTime(DateUtils.getNowDate());

        // 判断属地还是中心调用
        try {
            String platformCodeNew = getCascadeCode();
            String sysAdviceJson = JSON.toJSONString(sysAdvice);
            log.info("属地节点调用中心节点问题反馈sysAdviceJson:{}",sysAdviceJson);
            String encryptedJson = AESUtil.encrypt(sysAdviceJson);
            JSONObject body = new JSONObject();
            body.put("data", encryptedJson);
            /*
            中心 -> 不做处理
            属地 -> 调用级联网关 发送到中心节点的接口
         */
            if (StringUtils.isNotEmpty(platformCodeNew) && !CENTER_CODE.equals(platformCodeNew)) {
                ResultEntity cr = new ResultEntity();
                NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
                // 序列化 sysAdvice 为 JSON 字符串
                paramter.setBody(body);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_ORDER_SYSTEM);
                // 接口路径参数
                paramter.setPath(path);
                try {
                    cr = connectUtils.post(paramter, ResultEntity.class,
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(CENTER_CODE),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_YG),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                    log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
                } catch (Exception e) {
                    log.error("调用网关:{}", e);
                }
            }else{
                // TODO：调用工单平台接口
                this.adviceSyncOrderSystem(body);
            }
        } catch (Exception e) {
            log.info("属地节点到信息到中心节点出错===" + e);
        }
        return 1;
    }

    @Override
    public Result adviceSyncOrderSystem(JSONObject sysAdvice) {
        String json= sysAdvice.get("data").toString();
        String sysAdviceNew = AESUtil.decrypt(json);
        SysAdvice sysAdvice1 = JSON.parseObject(sysAdviceNew, SysAdvice.class);
        log.info("属地节点调用中心节点问题反馈sysAdvice1:{}",sysAdvice1);
        sysAdviceMapper.insertAdvice(sysAdvice1);
        //查询是否配置工单系统同步
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("sys_order_advice_sync");
        List<SysConfig>  sysConfigList  = sysConfigMapper.selectConfigList(sysConfig);
        //配置工单系统同步,如果没有配置，调用工单平台
        if(CollectionUtil.isNotEmpty(sysConfigList) && "1".equals(sysConfigList.get(0).getConfigValue())){
            log.info("属地调用中心调用工单平台接口");
            List<FileDTO> files = new ArrayList<>();
            if(!CollectionUtils.isEmpty(sysAdvice1.getAdviceAttach())){
                for(SysAdvice.Attach attach:sysAdvice1.getAdviceAttach()){
                    if(StringUtils.isNotEmpty(attach.getFileUrl())){
                        FileDTO dto1 = new FileDTO();
                        dto1.setFileUrl(attach.getFileUrl());
                        dto1.setOriginUrl(attach.getOriginUrl());
                        dto1.setPlatformCode(sysAdvice1.getNodeCode());
                        String suffix= "";
                        int lastDotIndex = attach.getFileUrl().lastIndexOf('.');
                        if (lastDotIndex != -1) {
                            // 提取后缀名
                            suffix = attach.getFileUrl().substring(lastDotIndex + 1);
                        }
                        String fileName = "反馈文件"+new Date().getTime()+"."+suffix;
                        if (fileName.contains("?")) {
                            // 截取问号之前的部分
                            fileName = fileName.split("\\?")[0];
                        }
                        dto1.setFileName(fileName);
                        files.add(dto1);
                    }
                }
            }
            JSONObject jsonObject = new JSONObject();
            // 用户部分
            jsonObject.put("customerName",sysAdvice1.getNickName());
            jsonObject.put("customerPhone",sysAdvice1.getPhoneNumber());
            jsonObject.put("askTime",new Date());
            jsonObject.put("creatorPro",sysAdvice1.getProvince());
            // 工单部分
            jsonObject.put("proTitle",sysAdvice1.getAdviceType());
            jsonObject.put("proContent",sysAdvice1.getAdviceText());
            jsonObject.put("proPri","0");
            jsonObject.put("proClassify","0");
            jsonObject.put("proType","PRO10005");
            jsonObject.put("businessType","b33907530");
            jsonObject.put("proCls","la37166631");
            jsonObject.put("systemSource","VideoLinkApp");
            jsonObject.put("province",sysAdvice1.getProvince());
            //加密验签
            String sign = "";
            try{
                sign = getSignature(jsonObject,SECRET_KEY,APP_KEY);
            }catch (Exception e){
                log.error("获取签名失败",e);
            }
            jsonObject.put("sign",sign);
            JSONArray jsonArray = (JSONArray) JSONArray.toJSON(files);
            jsonObject.put("files",jsonArray);
            String params = String.valueOf(jsonObject);
            JSONObject result = HttpUtils.doPostHeaders(params,BASE_URL+queryMethod,SECRET_KEY,APP_KEY);
            if("200".equals(result.getString("code"))){
                log.info("工单服务调用成功");
            }else{
                log.info("工单服务调用失败:{}",result.getString("msg"));
            }
        }
        return Result.success("操作成功");
    }

    private String getCascadeCode() {
        String platformCode = null;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
        if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
            com.chinatower.platform.entity.admin.SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), com.chinatower.platform.entity.admin.SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)) {
                platformCode = sysConfig.getConfigValue();
            }
        }
        return platformCode;
    }
    public static String getSignature(JSONObject jsonObject,String SecretKey,String APP_KEY) throws Exception {
        Map<String, Object> map = (Map<String, Object>) jsonObject.toJavaObject(Map.class);
        String sign = RsaUtil.sign(RsaUtil.generateSign(map), SecretKey, APP_KEY);
        return sign;
    }
}
