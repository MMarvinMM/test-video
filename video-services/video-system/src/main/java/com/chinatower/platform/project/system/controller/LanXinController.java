package com.chinatower.platform.project.system.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.project.system.domain.AppTokenData;
import com.chinatower.platform.project.system.domain.SysSsoUser;
import com.chinatower.platform.project.system.domain.UserInfoResponse;
import com.chinatower.platform.project.system.domain.UserTokenResponse;
import com.chinatower.platform.project.system.mapper.SysSsoMapper;
import com.chinatower.platform.project.system.service.ISysDictDataService;
import com.chinatower.platform.project.system.service.LanXinService;
import com.chinatower.platform.project.system.service.ModelConfigService;
import com.chinatower.platform.project.system.utils.Aes;
import com.chinatower.platform.project.system.utils.HMAC_SHA1;
import com.chinatower.platform.project.system.utils.HttpResultNew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.chinatower.platform.project.system.constants.Constants.*;

/**
 * video-system
 * 蓝信控制器
 * @author liuxt
 */
@RestController
@RequestMapping("/lanXin")
@Slf4j
public class LanXinController extends TowerBaseController {

    @Autowired
    private SysSsoMapper sysSsoMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISysDictDataService dictDataService;

    @Resource
    private LanXinService lanXinService;



    /**
     * 单点登录接口
     */
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
            String xLanXinCode = request.getHeader(XLANXINCODE);
            String xPhoneNumber = request.getHeader(XPHONENUMBER);
            long now = System.currentTimeMillis();
            long date = df.parse(xRequestTime).getTime();
            log.info(PATH+path+XAPPKEYCAMEL+xAppKey+XSIGNATURECAMEL+xSignature+XMSGIDCAMEL+xMsgId+XREQUESTTIMECAMEL+xRequestTime+XAPPCODECAMEL+xAppCode
                    +XLANXINCODECAMEL+xLanXinCode+XPHONENUMBERCAMEL+xPhoneNumber+NOW+now+DATE+date);
            //判断时间戳和当前时间是否超过3min 测试时先注掉 升级需放开
            if((now - date) > NUM_18W){
                return new HttpResultNew(CODE_8888,"地址失效！",null);
            }
            //判断时间戳和当前时间是否超过3min 测试时先注掉 升级需放开
            String signature = path + xAppKey + xMsgId + xRequestTime;
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
                        String phoneNumber = "";
                        if(!StringUtils.isEmpty(xLanXinCode)){
                            //根据xLanXinCode调蓝信开放平台接口，获取用户信息
                            String lanXinAppToken = lanXinService.getAppToken();
                            log.info("lanXinAppToken:"+lanXinAppToken);
                            String lanXinUserToken = lanXinService.getUserToken(xLanXinCode);
                            log.info("lanXinUserToken:"+lanXinUserToken);
                            phoneNumber = lanXinService.getUserInfo(lanXinAppToken,lanXinUserToken);
                            log.info("phoneNumber:"+phoneNumber);
                        }else{
                            //截取SecretKey前16位
                            String key = secretKey.substring(ZERO,SIXTEEN);
                            //将SecretKey前16位转化为16进制获取密钥
                            String cipher = Aes.parseByte2HexStr(key.getBytes());
                            phoneNumber = Aes.decryptBase64(xPhoneNumber, cipher);
                        }
                        if(!StringUtils.isEmpty(phoneNumber)){
                            sysSsoUser.setPhoneNumber(phoneNumber);
                            List<SysSsoUser> userInfoList = sysSsoMapper.selectSsoUserInfo(sysSsoUser);
                            if(CollectionUtils.isEmpty(userInfoList)){
                                return new HttpResultNew(CODE_8888,"鉴权失败！该用户未在本系统注册",null);
                            }
                            SysSsoUser userInfo = userInfoList.get(0);
                            Map tokenMap = new HashMap<>();
                            String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                            tokenMap.put(UUIDSTR,uuid);
                            tokenMap.put(TENANTID,userInfo.getTenantId());
                            tokenMap.put(USERID,userInfo.getUserId());
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
                        }else{
                            return new HttpResultNew(CODE_8888, "鉴权失败！未查出手机号",null);
                        }
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


//    @GetMapping("/getAppToken")
//    public Result<String> getAppToken() {
//        return Result.data(lanXinService.getAppToken());
//    }
//
//
//    @GetMapping("/getUserToken")
//    public Result<String> getUserToken(@RequestParam String code) {
//        return Result.data(lanXinService.getUserToken(code));
//    }
//
//    @GetMapping("/getUserDetail")
//    public Result<String> getUserDetail(@RequestParam String appToken,@RequestParam String userToken) {
//        return Result.data(lanXinService.getUserInfo(appToken,userToken));
//    }
//
//    @GetMapping("/getApiToken")
//    public Result<String> getApiToken(@RequestParam String code) {
//        return Result.data(lanXinService.getApiToken(code));
//    }


    @GetMapping("/getApiTokenInfo")
    public Result<AppTokenData> getApiTokenInfo(@RequestParam String url) {
        return Result.data(lanXinService.getApiTokenInfo(url));
    }
}
