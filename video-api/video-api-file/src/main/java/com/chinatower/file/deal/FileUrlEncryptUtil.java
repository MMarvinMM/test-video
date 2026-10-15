package com.chinatower.file.deal;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName FileUrlEncryptUtil
 * @Deacription Obs文件地址加密处理
 * @Author  peng.haibo
 * @Date 2022/8/2 10:25
 * @Version 1.0
 **/
@Slf4j
@Component
public class FileUrlEncryptUtil {

    @Autowired(required = false)
    private FileObsFeignClient fileObsFeignClient;

    private static final String SUCCESS_CODE = "0000";

    /**
     * @Description 处理Obs文件URL加密返回
     * @author peng.haibo
     * @date 2022/8/2
     * @param fileUrl 文件URL
     * @return String
     */
    public String dealObsFileUrl(String fileUrl) {
        return dealObsFileUrl(fileUrl, false, null);
    }

    public String dealObsFileUrl(String fileUrl, Boolean useOfs, String colonyCode) {
        if (StringUtils.isNotEmpty(fileUrl)) {
            // 库里面存的脏数据兼容下
            String activeProfile = SpringUtil.getActiveProfile();
            if (!activeProfile.equals("ter")) {
                if (fileUrl.contains("?")) {
                    fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
                }
            }
            List<String> fileUrls = new ArrayList<>();
            fileUrls.add(fileUrl);
            FileObsEncryptRequest obsEncryptRequest = new FileObsEncryptRequest();
            obsEncryptRequest.setUseOfs(useOfs);
            obsEncryptRequest.setColonyCode(colonyCode);
            obsEncryptRequest.setFileUrls(fileUrls);
            ApiResult<List<FileObsEncryptResponse>> apiResult = fileObsFeignClient.getObsEncryptUrls(obsEncryptRequest);

            if (StringUtils.equals(apiResult.getReturnCode(), SUCCESS_CODE)) {
                List<FileObsEncryptResponse> list = apiResult.getData();
                // 返回加密的文件URL
                return list.get(0).getEncryptUrl();
            }
        }
        return fileUrl;
    }

    public <T extends List<U>, U> List<U> dealObsFileUrlGeneric(T request, Class<U> outputType) {
        Object obj = fileObsFeignClient.genericEncryptBatch(request);
        String jsonString = JSON.toJSONString(obj);
        JSONArray jsonArray = JSON.parseArray(jsonString);

        List<U> objectList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            U newInstance = ReflectUtil.newInstance(outputType.getName());
            BeanUtil.fillBeanWithMap(jsonObject, newInstance, false);

            objectList.add(newInstance);
        }
        return objectList;
    }

    public Map<String,String> dealObsFileUrlBatch(List<String> fileUrlList) {
        List<String> urlList = rebuildUrl(fileUrlList);
        Map<String, String> map = fileObsFeignClient.encryptBatch(urlList);
        return refillUrl(fileUrlList, map);
    }

    public Map<String,String> dealObsFileUrlBatch(List<String> fileUrlList, Boolean useOfs, String colonyCode) {
        if (BooleanUtils.isNotTrue(useOfs)) {
            return dealObsFileUrlBatch(fileUrlList);
        } else {
            FileObsEncryptRequest obsEncryptRequest = new FileObsEncryptRequest();
            obsEncryptRequest.setUseOfs(useOfs);
            obsEncryptRequest.setColonyCode(colonyCode);
            obsEncryptRequest.setFileUrls(fileUrlList);
            ApiResult<List<FileObsEncryptResponse>> apiResult = fileObsFeignClient.getObsEncryptUrls(obsEncryptRequest);
            if (apiResult != null && CollectionUtils.isNotEmpty(apiResult.getData())) {
                try {
                    log.info("FileUrlEncryptUtil dealObsFileUrlBatch apiResult data:{}", JSON.toJSONString(apiResult.getData()));
                    return apiResult.getData().stream().filter(x -> x.getFileUrl() != null && x.getEncryptUrl() != null)
                            .collect(Collectors.toMap(FileObsEncryptResponse::getFileUrl, FileObsEncryptResponse::getEncryptUrl, (a, b) -> a));
                } catch (Exception e) {
                    log.error("FileUrlEncryptUtil dealObsFileUrlBatch error, e:", e);
                    return Maps.newHashMap();
                }
            } else {
                return Maps.newHashMap();
            }
        }
    }

    public static List<String> rebuildUrl(List<String> fileUrlList) {
        String activeProfile = SpringUtil.getActiveProfile();
        return fileUrlList.stream().filter(StringUtils::isNotBlank).map(fileUrl -> {
            if (!activeProfile.equals("ter")) {
                if (fileUrl.indexOf("?") > 0) {
                    fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
                }
            }
            return fileUrl;
        }).collect(Collectors.toList());
    }

    public static Map<String, String> refillUrl(List<String> fileUrlList, Map<String, String> map) {
        String activeProfile = SpringUtil.getActiveProfile();
        if (activeProfile.equals("ter")) {
            return map;
        }
        Map<String, String> resMap = new HashMap<>();
        for (String originUrl : fileUrlList) {
            if (Objects.isNull(originUrl)) {
                continue;
            }
            String newOriginUrl = originUrl;
            if (originUrl.contains("?")) {
                newOriginUrl = originUrl.substring(0, originUrl.indexOf("?"));
            }
            if (map.containsKey(newOriginUrl)) {
                resMap.put(originUrl, map.get(newOriginUrl));
            }
        }
        return resMap;
    }

    /**
     * @Description 批量处理Obs文件URL加密返回
     * @author hued
     * @date 2022年8月30日17:47:44
     * @param fileUrls 文件URL集合
     * @return String
     */
    public List<FileObsEncryptResponse> dealObsFileUrls(List<String> fileUrls) {
        List<FileObsEncryptResponse> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUrls)) {
            FileObsEncryptRequest obsEncryptRequest = new FileObsEncryptRequest();
            obsEncryptRequest.setFileUrls(fileUrls);
            ApiResult<List<FileObsEncryptResponse>>  apiResult = fileObsFeignClient.getObsEncryptUrls(obsEncryptRequest);
            if (StringUtils.equals(apiResult.getReturnCode(), SUCCESS_CODE)) {
                result = apiResult.getData();
            }
        }
        return result;
    }


    /**
     * 判断当前url是否为obs地址
     * @param url
     * @return
     */
    public Boolean isObs(@RequestBody String url) {
        return fileObsFeignClient.isObs(url);
    }


    /**
     * @Description 批量处理Obs文件URL加密返回Map方便后面获取
     * @author peng.haibo
     * @date 2022/9/15
     * @param fileUrls 文件URL集合
     * @return Map<String, String>
     */
    @Deprecated
    public Map<String, String> dealObsFileUrl4Map(List<String> fileUrls) {
        Map<String, String> result = new HashMap<>(16);
        if (StringUtils.isNotEmpty(fileUrls)) {
            List<String> dealFileUrls = new ArrayList<>();
            // 库里面存的脏数据兼容下
            for (String fileUrl: fileUrls) {
                if (StringUtils.isNotEmpty(fileUrl) && fileUrl.contains("?")) {
                    fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
                }
                dealFileUrls.add(fileUrl);
            }
            List<FileObsEncryptResponse> obsEncryptResponseList = dealObsFileUrls(dealFileUrls);
            result = obsEncryptResponseList.stream().collect(Collectors.toMap(FileObsEncryptResponse::getFileUrl, FileObsEncryptResponse::getEncryptUrl));
        }
        return result;
    }
}
