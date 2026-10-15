package com.chinatower.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/14 11:41
 */
@Slf4j
public class AssembleUrlUtil {

    /**
     * 拼接属地编码到文件地址
     * @param accessNode 接入节点
     * @param url 文件地址
     * @param correctLocationFlag 是否可拼接属地（纯属第不可拼接：false  下沉节点可拼接:true）
     * @return
     */
    public static String getAssembleUrl(String accessNode,String url,boolean correctLocationFlag){
        log.info("AssembleUrlUtil-合并前路径:{}",url);
        String resUrl="";
        try{
            if(!correctLocationFlag || org.springframework.util.StringUtils.isEmpty(accessNode) || "10000000001".equals(accessNode) || org.springframework.util.StringUtils.isEmpty(url)){
                return url;
            }
            String[] urls = url.split(Constants.COMMA);
            for (String realUrl:urls) {
                if(realUrl.contains("http") || realUrl.contains("https")){
                    //将属地编码拼接在域名之后
                    int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                    // 拼接字符串，在第三个斜杠之后
                    StringBuilder res = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                    res.append(accessNode+"/");
                    res.append(realUrl.substring(thirdSlashIndex + 1));
                    realUrl = res.toString();
                }else{
                    realUrl = "/"+accessNode+realUrl;
                }
                if(StringUtils.isEmpty(resUrl)){
                    resUrl = realUrl;
                }else{
                    resUrl += Constants.COMMA+realUrl;
                }
            }
        }catch (Exception e){
            log.error("getAssembleUrl拼接属地编码异常-返回原地址"+e);
            return url;
        }
        log.info("AssembleUrlUtil-合并后路径:{}",resUrl);
        return resUrl;
    }

}
