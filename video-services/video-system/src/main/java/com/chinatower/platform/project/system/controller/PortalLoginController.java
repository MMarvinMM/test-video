package com.chinatower.platform.project.system.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.domain.SysSsoUser;
import com.chinatower.platform.project.system.mapper.SysSsoMapper;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.project.system.utils.Aes;
import com.chinatower.platform.project.system.utils.HMAC_SHA1;
import com.chinatower.platform.project.system.utils.HttpResultNew;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static com.chinatower.platform.project.system.constants.Constants.*;

/**
 * video-system
 * 单点登录
 *
 * @author liuxt
 */
@Api(tags = "单点登录")
@RestController
@RequestMapping("/video/third")
@Slf4j
public class PortalLoginController extends TowerBaseController {

    @Autowired
    private SysSsoMapper sysSsoMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISysDictDataService dictDataService;


    /**
     * 单点登录接口
     */
    @ApiOperation(value = "单点登录")
    @PostMapping("/doPortalLogin")
    public HttpResultNew doPortalLogin() {
        String path = request.getRequestURI();
        String url = "";
        try{
            SimpleDateFormat df = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            String xAppKey = request.getHeader(XAPPKEY);
            String xSignature = request.getHeader(XSIGNATURE);
            String xMsgId = request.getHeader(XMSGID);
            String xRequestTime = request.getHeader(XREQUESTTIME);
            String xAppCode = request.getHeader(XAPPCODE);
            String xCustomerCode = request.getHeader(XCUSTOMERCODE);
            String xUserCode = request.getHeader(XUSERCODE);
            //新增字段服务端接口名称和加密串
            String xServerCode = request.getHeader(XSERVERCODE);
            String xPassWord = request.getHeader(XPASSWORD);
            long now = System.currentTimeMillis();
            long date = df.parse(xRequestTime).getTime();
            log.info(PATH+path+XAPPKEYCAMEL+xAppKey+XSIGNATURECAMEL+xSignature+XMSGIDCAMEL+xMsgId+XREQUESTTIMECAMEL+xRequestTime+XAPPCODECAMEL+xAppCode
                    +XCUSTOMERCODECAMEL+xCustomerCode+XUSERCODECAMEL+xUserCode+XSERVERCODECAMEL+xServerCode+XPASSWORDCAMEL+xPassWord+NOW+now+DATE+date);
            //判断时间戳和当前时间是否超过3min 测试时先注掉 升级需放开
            if((now - date) > NUM_18W){
                return new HttpResultNew(CODE_8888,"地址失效！",null);
            }
            //判断时间戳和当前时间是否超过3min 测试时先注掉 升级需放开
            String signature = path + xAppKey + xMsgId + xRequestTime;
            if (!StringUtils.isBlank(xCustomerCode)) {
                signature += xCustomerCode;
                if (!StringUtils.isBlank(xUserCode)) {
                    signature += xUserCode;
                }
            }
            //根据appCode、appKey查询secretKey
            Map map = new HashMap<>();
            map.put(APPCODE,xAppCode);
            map.put(APPKEY,xAppKey);
            Map resultMap = sysSsoMapper.getSecretKeyNew(map);
            if(!ObjectUtils.isEmpty(resultMap)){
                String secretKey = (String)resultMap.get(SECRETKEY);
                String mySignature = HMAC_SHA1.signByHmacSHA1(secretKey,(signature).toUpperCase());
                log.info(MYSIGNATURE+mySignature);
                //比较请求头的签名和生成签名是否一致
                if((xSignature).equals(mySignature)){
                    try{
                        SysSsoUser sysSsoUser = new SysSsoUser();
                        sysSsoUser.setUserId(xUserCode);
                        sysSsoUser.setTenantId(xCustomerCode);
                        List<SysSsoUser> userInfoList = sysSsoMapper.selectSsoUserInfo(sysSsoUser);
                        if(CollectionUtils.isEmpty(userInfoList)){
                            return new HttpResultNew(CODE_8888,"鉴权失败！该用户未在本系统注册",null);
                        }
                        SysSsoUser userInfo = userInfoList.get(0);
                        String userAccount = userInfo.getUserName();
                        String userPassword = userInfo.getPassword();
                        //截取SecretKey前16位
                        String key = secretKey.substring(ZERO,SIXTEEN);
                        //将SecretKey前16位转化为16进制获取密钥
                        String cipher = Aes.parseByte2HexStr(key.getBytes());
                        String myPassWord = Aes.decryptBase64(xPassWord, cipher);
                        //比较请求头的签名和生成签名是否一致
                        if(!SecurityUtils.matchesPassword(myPassWord, userPassword)){
                            return new HttpResultNew(CODE_8888, "鉴权失败！请求头的密码和生成密码不一致",null);
                        }
                        Map tokenMap = new HashMap<>();
                        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                        tokenMap.put(UUIDSTR,uuid);
                        tokenMap.put(TENANTID,xCustomerCode);
                        tokenMap.put(USERID,xUserCode);
                        sysSsoMapper.insertTokenConfig(tokenMap);
                        List<SysDictData> sysDictDataList = dictDataService.selectDictDataByType(CALLBACKURL);
                        String callbackUrl = "";
                        if(!CollectionUtils.isEmpty(sysDictDataList)){
                            callbackUrl = sysDictDataList.get(0).getDictValue();
                        }else{
                            return new HttpResultNew(CODE_8888, "鉴权失败！未配置callbackUrl枚举值",null);
                        }
                        url = callbackUrl+uuid;
                        return new HttpResultNew(CODE_0000, "鉴权通过！",url);
                    }catch (Exception e){
                        return new HttpResultNew( CODE_8888, e.getMessage(),null);
                    }
                }else{
                    return new HttpResultNew(CODE_8888, "鉴权失败！请求头的签名和生成签名不一致",null);
                }
            }else{
                return new HttpResultNew(CODE_8888, "鉴权失败！appKey、appCode无效",null);
            }
        } catch (Exception e){
            return new HttpResultNew(CODE_8888, e.getMessage(),null);
        }
    }

    /**
     * 获取登录人信息
     */
    @PostMapping("/getLoginUser")
    public LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取签名
     */
    @PostMapping("/getSignature")
    public Result getSignature() {
        String path = "/api/sdkToApi";
        String xAppKey = request.getHeader(XAPPKEY);
        String xMsgId = request.getHeader(XMSGID);
        String xRequestTime = request.getHeader(XREQUESTTIME);
        String xCustomerCode = request.getHeader(XCUSTOMERCODE);
        String xUserCode = request.getHeader(XUSERCODE);
        String xSecretKey = request.getHeader("X-SecretKey");
        //判断时间戳和当前时间是否超过3min 测试时先注掉 升级需放开
        String signature = path + xAppKey + xMsgId + xRequestTime;
        if (!StringUtils.isBlank(xCustomerCode)) {
            signature += xCustomerCode;
            if (!StringUtils.isBlank(xUserCode)) {
                signature += xUserCode;
            }
        }
        return Result.data(HMAC_SHA1.signByHmacSHA1(xSecretKey,(signature).toUpperCase()));
    }
}
