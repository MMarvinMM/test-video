package com.chinatower.system.api.deal;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.ObsEncryptFeignClient;
import com.chinatower.system.api.request.file.ObsEncryptRequest;
import com.chinatower.system.api.resp.ObsEncryptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ObsFileUrlEncryptUtil
 * @Deacription Obs文件地址加密处理
 * @Author  peng.haibo
 * @Date 2022/8/2 10:25
 * @Version 1.0
 * @deprecated use {@link com.chinatower.file.deal.FileUrlEncryptUtil} instead
 **/
@Component
@Deprecated
public class ObsFileUrlEncryptUtil {

    @Autowired(required = false)
    private ObsEncryptFeignClient obsEncryptFeignClient;


    /**
     * @Description 处理Obs文件URL加密返回
     * @author peng.haibo
     * @date 2022/8/2
     * @param fileUrl 文件URL
     * @return String
     */
    public String dealObsFileUrl(String fileUrl) {
        if (StringUtils.isNotEmpty(fileUrl)) {
            // 库里面存的脏数据兼容下
            if (fileUrl.contains("?")) {
                fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
            }
            List<String> fileUrls = new ArrayList<>();
            fileUrls.add(fileUrl);
            ObsEncryptRequest obsEncryptRequest = new ObsEncryptRequest();
            obsEncryptRequest.setFileUrls(fileUrls);
            ApiResult<List<ObsEncryptResponse>>  apiResult = obsEncryptFeignClient.getObsEncryptUrls(obsEncryptRequest);

            if (StringUtils.equals(apiResult.getReturnCode(), SysConstants.SUCCESS_CODE)) {
                List<ObsEncryptResponse> list = apiResult.getData();
                // 返回加密的文件URL
                return list.get(0).getEncryptUrl();
            }
        }
        return fileUrl;
    }

    /**
     * @Description 批量处理Obs文件URL加密返回
     * @author hued
     * @date 2022年8月30日17:47:44
     * @param fileUrls 文件URL集合
     * @return String
     */
    public List<ObsEncryptResponse> dealObsFileUrls(List<String> fileUrls) {
        List<ObsEncryptResponse> result = new ArrayList<>();
        if (StringUtils.isNotEmpty(fileUrls)) {
            ObsEncryptRequest obsEncryptRequest = new ObsEncryptRequest();
            obsEncryptRequest.setFileUrls(fileUrls);
            ApiResult<List<ObsEncryptResponse>>  apiResult = obsEncryptFeignClient.getObsEncryptUrls(obsEncryptRequest);
            if (StringUtils.equals(apiResult.getReturnCode(), SysConstants.SUCCESS_CODE)) {
                result = apiResult.getData();
            }
        }
        return result;
    }

    /**
     * @Description 批量处理Obs文件URL加密返回Map方便后面获取
     * @author peng.haibo
     * @date 2022/9/15
     * @param fileUrls 文件URL集合
     * @return Map<String, String>
     */
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
            List<ObsEncryptResponse> obsEncryptResponseList = dealObsFileUrls(dealFileUrls);
            result = obsEncryptResponseList.stream().collect(Collectors.toMap(ObsEncryptResponse::getFileUrl, ObsEncryptResponse::getEncryptUrl));
        }
        return result;
    }
}
