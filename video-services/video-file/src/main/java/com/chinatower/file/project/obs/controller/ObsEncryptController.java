package com.chinatower.file.project.obs.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.annotation.Trace;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.file.FileOperation;
import com.chinatower.file.config.runner.FileConfigRunner;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.domain.FileUrlOfsColonyCode;
import com.chinatower.file.domain.SysConfig;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.ofs.model.request.OfsRefreshUrlRequest;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.resp.ObsEncryptResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * video-file
 *
 * @className: ObsEncryptController
 * @description: obs加密方法
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Api(tags = "OBS管理")
@RestController
@RequestMapping("/file/obsFiles")
@SuppressWarnings("all")
public class ObsEncryptController extends BaseController {

    @Resource
    FileConfigRunner.FileStrategeManager fileStrategeManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    private final String FILE_URLS_NOT_NULL = "原始路径fileUrls不可为空";

    private final String SERVER_ERR = "内部服务异常";

    private final String CENTER_CODE = "10000000001";

    public static final String DEPENDENCE_MANAGE = "dependence_manage";
    /**
     * 5.16	 获取加密后的OBS文件地址
     */
    @ApiOperation(value = "OBS加密url获取")
    @Trace
    @PostMapping(value = "/getObsEncryptUrls")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrls(@RequestBody FileObsEncryptRequest fileObsEncryptRequest) {
        try {
            List<String> fileUrls = fileObsEncryptRequest.getFileUrls();
            logger.info("obs加密入参：{}", JSON.toJSONString(fileObsEncryptRequest));
            if(fileUrls == null || fileUrls.isEmpty()){
                return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1001,FILE_URLS_NOT_NULL);
            }
            //验签策略
            List<FileObsEncryptResponse> list = getEncryptList(fileUrls, fileObsEncryptRequest.getColonyCode());

            return ApiResult.SUCCESS(list,ApiResult.RETURN_CODE_SUCCESS_MSG);
        }catch (Exception e){
            logger.error("obs加密异常：{}",e);
            return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1004,SERVER_ERR);
        }
    }


    /**
     * 行业需要顺序返回，部分行业未做key value匹配
     * @param fileObsEncryptRequest
     * @return
     */
    @ApiOperation(value = " SDK OBS加密url获取")
    @PostMapping(value = "/getObsEncryptUrlsSDK")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrlsSDK(@RequestBody FileObsEncryptRequest fileObsEncryptRequest) {
        try {
            List<String> fileUrls = fileObsEncryptRequest.getFileUrls();
            logger.info("sdk 加密入参：{}", JSON.toJSONString(fileObsEncryptRequest));
            if(fileUrls == null || fileUrls.isEmpty()){
                return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1001,FILE_URLS_NOT_NULL);
            }
            List<FileObsEncryptResponse> list = new ArrayList<>();
            //验签策略
            List<String> strings = fileUrlEncryptUtil.rebuildUrl(fileUrls);
            Map<String, String> map = encryptBatch(strings);
            Map<String, String> resMap = fileUrlEncryptUtil.refillUrl(fileUrls, map);

            for (String url : fileUrls) {
                FileObsEncryptResponse urlData = new FileObsEncryptResponse();
                urlData.setFileUrl(url);
                String encryptUrl = resMap.get(url);
                urlData.setEncryptUrl(encryptUrl);
                list.add(urlData);
            }
            return ApiResult.SUCCESS(list, ApiResult.RETURN_CODE_SUCCESS_MSG);
        }catch (Exception e){
            logger.error("obs加密异常：{}",e);
            return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1004,SERVER_ERR);
        }
    }

    /**
     * 批量获取加密后的OBS文件地址
     *
     * @param urlList
     * @return
     */
    @Trace
    @PostMapping("/encryptBatch")
    public Map<String, String> encryptBatch(@RequestBody List<String> urlList) {
        logger.info("obs批量加密入参：{}", JSON.toJSONString(urlList));

        Map<String, String> result = new HashMap<>();

        FileObsEncryptRequest fileObsEncryptRequest = new FileObsEncryptRequest();
        fileObsEncryptRequest.setFileUrls(urlList);
        List<FileObsEncryptResponse> list = getEncryptList(urlList, null);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                if (null != item.getEncryptUrl()) {
                    result.put(item.getFileUrl(), item.getEncryptUrl());
                } else {
                    result.put(item.getFileUrl(), null);
                }
            });
        }
        return result;
//        return fileStrategeManager.encryptBatch(urlList);
    }


    /**
     * 未使用的批量加密接口
     * @param serializable
     * @return
     */
    @Trace
    @PostMapping("/genericEncryptBatch")
    public List<?> genericEncryptBatch(@RequestBody List<?> serializable) {
        String jsonString = JSON.toJSONString(serializable);

        FileOperation fileOperationObs = fileStrategeManager.getObsStrategy();
        jsonString = parse(jsonString, fileOperationObs);
        FileOperation fileOperationOfs = fileStrategeManager.getObsStrategy();
        jsonString = parse(jsonString, fileOperationOfs);
        return JSON.parseArray(jsonString);
    }

    private static String parse(String jsonString, FileOperation fileOperationObs) {
        Pattern pattern = fileOperationObs.pattern();
        Matcher m = pattern.matcher(jsonString);
        while (m.find()) {
            String tempObsUrl = m.group(0);
            jsonString = jsonString.replaceAll(pattern.toString(), fileOperationObs.encrypt(tempObsUrl));
        }
        return jsonString;
    }

    /**
     * 文件加密v2
     * @param serializable
     * @return
     */
    @ApiOperation(value = "OBS加密url获取")
    @PostMapping(value = "/getObsEncryptUrls/v2")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrlsV2(@RequestBody FileObsEncryptRequest fileObsEncryptRequest) {
        return getObsEncryptUrls(fileObsEncryptRequest);
    }

    /**
     * 批量文件加密v2
     * @param urlList
     * @return
     */
    @PostMapping("/encryptBatch/v2")
    public Map<String, String> encryptBatchV2(@RequestBody List<String> urlList) {
        return encryptBatch(urlList);
    }


    /**
     * 未使用批量文件加密地址获取v2
     * @param serializable
     * @return
     */
    @PostMapping("/genericEncryptBatch/v2")
    public List<?> genericEncryptBatchV2(@RequestBody List<?> serializable) {
        return genericEncryptBatch(serializable);
    }


    /**
     * 判断当前url是否为obs地址
     * @param url
     * @return
     */
    @PostMapping("/isObs")
    public Boolean isObs(@RequestBody String url) {
        return fileStrategeManager.isObs(url);
    }

    private FileUrlOfsColonyCode setFileUrlOfsColonyCode(String url, int strategy) {
        return new FileUrlOfsColonyCode(url, strategy, null);
    }

    /**
     * 新版本加密策略改造
     * 1.url白名单
     * 2.obs加密
     * 3.ofsv1加密  纯属地节点、下沉节点文件地址不包含节点编码
     * 4.ofsv2加密  下沉节点包含节点编码，中心节点不含节点编码ofs
     * @param fileUrls urls
     * @param colonyCode
     * @return
     */
    private List<FileObsEncryptResponse> getEncryptList(List<String> fileUrls, String colonyCode) {

        //--------------------白名单判断----------------------
        String whiteAddr = null;
        //获取文件白名单调用域名集合
        SysConfig sysConfigWhite = sysConfigService.selectConfigByKey("third_whiteAddr");
        if (sysConfigWhite != null) {
            whiteAddr = sysConfigWhite.getConfigValue();
        }
        //白名单地址返回
        List<FileObsEncryptResponse> whiteAddrFileList = new ArrayList<>();

        List<String> obsAndOfs = new ArrayList<>();

        //url中包含白名单配置中的域名，直接返回原地址
        if (!org.apache.commons.lang3.StringUtils.isEmpty(whiteAddr)) {
            String[] whiteAddrs = whiteAddr.split(Constants.COMMA);
            for (String url : fileUrls) {
                // 检查 url 是否包含 whiteAddrs 中的任意一个地址
                if (Arrays.asList(whiteAddrs).stream().anyMatch(url::contains)) {
                    FileObsEncryptResponse fileObsEncryptResponse = new FileObsEncryptResponse();
                    fileObsEncryptResponse.setFileUrl(url);
                    fileObsEncryptResponse.setEncryptUrl(url);
                    whiteAddrFileList.add(fileObsEncryptResponse);
                } else {
                    obsAndOfs.add(url);
                }
            }
        } else {
            obsAndOfs.addAll(fileUrls);
        }
        logger.info("白名单下的url地址:{}", JSON.toJSONString(whiteAddrFileList));
        logger.info("obs and ofs url:{}", JSON.toJSONString(obsAndOfs));

        //多线程入参list
        List<FileUrlOfsColonyCode> thredList = new ArrayList<>();

        List<String> v1 = new ArrayList<>();
        List<String> v2 = new ArrayList<>();
        //区分 ofs 版本 根据http https 开头区分   V1 V2
        for (String url : obsAndOfs) {
            if (url.startsWith("http") || url.startsWith("https")) {
                v2.add(url);
            } else {
                v1.add(url);
            }
        }

        //添加 ofs v1参数，原逻辑
        getFileListByOfsV1(thredList, v1);

        //添加ofs v2 1.9.21 参数
        getFileListByOfsV2(thredList, v2);


        //-----------------------多线程验签------------------------------
        logger.info("验签多线程入参:{}", JSON.toJSONString(thredList));

        List<FileObsEncryptResponse> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(thredList)) {
            list = ThreadParallelUtil.run(thredList, it -> {
                FileObsEncryptResponse urlData = new FileObsEncryptResponse();
                urlData.setFileUrl(it.getUrl());
                String encryptUrl = fileStrategeManager.encryptStrategy(it.getUrl(), it.getStrategy());
                if (StringUtils.isNotEmpty(encryptUrl)) {
                    urlData.setEncryptUrl(encryptUrl);
                    return urlData;
                } else {
                    logger.info("验签多线程入参URL"+it.getUrl()+"----验签多线程入参strategy"+it.getStrategy());
                    return null;
                }
            }, ThreadParallelUtil.ConfigFactory.FILE_ENCRYPT);
        }
        logger.info("验签多线程返回:{}", JSON.toJSONString(list));

        //处理返回数据
        Map<String, String> resMap = new HashMap<>();
        for (FileObsEncryptResponse fileObsEncryptResponse : list) {
            if (null != fileObsEncryptResponse) {
                resMap.put(fileObsEncryptResponse.getFileUrl(), fileObsEncryptResponse.getEncryptUrl());
            }
        }
        List<FileObsEncryptResponse> resList = new ArrayList<>();
        for (FileUrlOfsColonyCode fileUrlOfsColonyCode : thredList) {
            FileObsEncryptResponse fileObsEncryptResponse = new FileObsEncryptResponse();
            fileObsEncryptResponse.setFileUrl(fileUrlOfsColonyCode.getUrl());
            if (resMap.containsKey(fileUrlOfsColonyCode.getUrl())) {
                fileObsEncryptResponse.setEncryptUrl(resMap.get(fileUrlOfsColonyCode.getUrl()));
            }
            resList.add(fileObsEncryptResponse);
        }

        //返回添加白名单url
        if (!whiteAddrFileList.isEmpty()) {
            resList.addAll(whiteAddrFileList);
        }
        return resList;
    }

    /**
     * 旧版本ofs加密 设置参数
     * @param thredList 线程处理数据
     * @param obsAndOfs 入参list
     * @return
     */
    public List<FileUrlOfsColonyCode> getFileListByOfsV1(List<FileUrlOfsColonyCode> thredList, List<String> obsAndOfs) {
        //--------------------obs  ofs 判断----------------------
        List<String> obsUrls = new ArrayList<>();
        List<String> ofsUrls = new ArrayList<>();
        //判断是否为obs
        if (!obsAndOfs.isEmpty()) {
            for (String url : obsAndOfs) {
                if (fileStrategeManager.isObs(url)) {
                    obsUrls.add(url);
                    //obs
                    thredList.add(setFileUrlOfsColonyCode(url, 0));
                } else {
                    ofsUrls.add(url);
                }
            }
        }
        logger.info("obs url:{}", JSON.toJSONString(obsUrls));
        logger.info("ofs v1 v2 url:{}", JSON.toJSONString(ofsUrls));

        //---------------------属地 下沉 ofs v1 v2 判断-----------------------------

        if (!ofsUrls.isEmpty()) {

            SysConfig sysConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
            if (sysConfig != null && Constants.ZERO.equals(sysConfig.getConfigValue())) {
                for (String ofsUrl : ofsUrls) {
                    thredList.add(setFileUrlOfsColonyCode(ofsUrl, 1));
                }
            } else {
                //下沉节点或者中心
                String configValue = "";
                SysConfig platformCodeConfig = sysConfigService.selectConfigByKey("platform_code");
                if (platformCodeConfig != null) {
                    configValue = platformCodeConfig.getConfigValue();
                }
                List<String> colonyCodeList = sysConfigService.getAllDownPlatformCode();

                for (String url : ofsUrls) {
                    int strategy = 1;
                    //文件路径中包含下沉节点code时走ofsv2逻辑
                    for (String code:colonyCodeList) {
                        if (url.contains(code)){
                            //ofs v2
                            strategy = 2;
                            break;
                        } else {
                            //ofs  节点=中心节点 v2
                            //     节点!=中心节点  v1
                            if (CENTER_CODE.equals(configValue) || url.contains(CENTER_CODE)) {
                                strategy = 2;
                                break;
                            }
                        }

                    }
                    logger.info("添加url:{},strategy:{}", url, strategy);
                    thredList.add(setFileUrlOfsColonyCode(url, strategy));
                }

            }
        }
        return thredList;
    }

    /**
     * 新版本ofs加密  V2 设置参数
     * @param thredList
     * @param obsAndOfs
     * @return
     */
    public List<FileUrlOfsColonyCode> getFileListByOfsV2(List<FileUrlOfsColonyCode> thredList, List<String> obsAndOfs) {
        obsAndOfs.forEach(url -> {
            thredList.add(setFileUrlOfsColonyCode(url, 3));
        });
        return thredList;
    }

}
