package com.chinatower.platform.controller;

import cn.hutool.core.util.DesensitizedUtil;
import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.bdm.api.entity.ResourceVO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.IResourceConfService;
import com.chinatower.platform.utils.JNConstants;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 *
 * video-export
 * 功能说明：资源管理controller
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Api(tags = "资源")
@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceConfController extends TowerBaseController {
    @Autowired
    IResourceConfService resourceConfService;

    /**
     *
     * 资源导出
     * 功能说明: 资源导出
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @param response 输出流
     * @return 文件输出流
     * @throws
     * @requestID
     */
    @ApiOperation(value = "资源导出")
    @TowerLog(title = "资源导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody ResourceConf resourceConf, HttpServletResponse response) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        resourceConf.setPageSize(null);
        resourceConf.setPageNum(null);
        List<ResourceVO> list = resourceConfService.selectResourceExportList(resourceConf);
        if (list.size() > JNConstants.FIVE_THOUSAND) {
            throw new CustomException(JNConstants.CHECK_EXPORT_NUMS);
        }
        //add by liuxt 经纬度小数点后自动补齐九位
        if(!CollectionUtils.isEmpty(list)){
            for (ResourceVO resourceVO : list) {
                String longitude = resourceVO.getLongitude();
                String latitude = resourceVO.getLatitude();
                if(!StringUtils.isEmpty(longitude) && !StringUtils.isEmpty(latitude)){
                    try {
                        Double.parseDouble(longitude);
                        Double.parseDouble(latitude);
                        if(longitude.contains(JNConstants.POINT)){
                            String result = longitude.substring(longitude.indexOf(JNConstants.POINT) + JNConstants.ONE_NUM);//取小数点后部分
                            resourceVO.setLongitude(longitude.substring(JNConstants.ZERO_NUM,longitude.indexOf(JNConstants.POINT)+ JNConstants.ONE_NUM) + padZero(result,JNConstants.NINE_NUM));//小数点后自动补齐九位
                        }else{
                            resourceVO.setLongitude(longitude+JNConstants.BUQININE);
                        }
                        if(latitude.contains(JNConstants.POINT)){
                            String result = latitude.substring(latitude.indexOf(JNConstants.POINT) + JNConstants.ONE_NUM);//取小数点后部分
                            resourceVO.setLatitude(latitude.substring(JNConstants.ZERO_NUM,latitude.indexOf(JNConstants.POINT)+ JNConstants.ONE_NUM) + padZero(result,JNConstants.NINE_NUM));//小数点后自动补齐九位
                        }else {
                            resourceVO.setLatitude(latitude+JNConstants.BUQININE);
                        }
                    } catch (NumberFormatException e) {
                        log.error("resourceId:"+resourceVO.getResourceId(),e);
                    }
                }
            }
        }
        FileUtils.download(response, new ExcelUtil<>(ResourceVO.class).exportBytes(list));
    }



    public static String padZero(String str, int length) {
        if (str.length() >= length) {
            return str.substring(JNConstants.ZERO_NUM,length);
        }
        StringBuilder sb = new StringBuilder(str);
        int zerosToAdd = length - str.length();
        for (int i = 0; i < zerosToAdd; i++) {
            sb.append(JNConstants.ZERO);
        }
        return sb.toString();
    }


    

}
