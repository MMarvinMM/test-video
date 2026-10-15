package com.chinatower.emergency.service.impl;

import com.chinatower.emergency.dto.Constant;
import com.chinatower.emergency.dto.EmergencyAttachVO;
import com.chinatower.emergency.dto.EmergencyEventInfoVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: video-platform
 * @ClassName: BaseEventInfoService
 * @description: 应急事件信息基础方法类，用于封装共同方法
 * @author: fengty
 * @create: 2024-10-29 14:25
 */
public class BaseEventInfoService {

    @Resource
    FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     *  私有方法封装对象共通属性，createFlag true 新增， false 更新
     * @param t
     * @param createFlag
     * @throws NoSuchFieldException
     */
    public <T> void setUserInfo(T t, boolean createFlag) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            Field tenantIdField = t.getClass().getDeclaredField("tenantId");
            Field industryCodeField = t.getClass().getDeclaredField("industryCode");
            Field appVerCodeField = t.getClass().getDeclaredField("appVerCode");
            Field creatorField = t.getClass().getDeclaredField("creator");
            Field modifierField = t.getClass().getDeclaredField("modifier");

            tenantIdField.setAccessible(true);
            industryCodeField.setAccessible(true);
            appVerCodeField.setAccessible(true);
            creatorField.setAccessible(true);
            modifierField.setAccessible(true);
            tenantIdField.set(t, user.getTenantId());
            industryCodeField.set(t, user.getIndustryCode());
            appVerCodeField.set(t, user.getAppVerCode());
            if(createFlag) {
                creatorField.set(t, SecurityUtils.getOperator());
            } else {
                modifierField.set(t, SecurityUtils.getOperator());
            }
        } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
        }
    }

    /**
     * 处理附件路径
     * @param vo
     */
    public void handleAttachPath(EmergencyEventInfoVO vo, Map<Integer, List<EmergencyAttachVO>> attachMap) {

        List<String> feignParam = new ArrayList<>();
        attachMap.forEach((k, v) -> {
            if(!CollectionUtils.isEmpty(v)) {
                for (EmergencyAttachVO attachVO : v) {
                    feignParam.add(attachVO.getOriginUrl());
                }
            }
        });
        Map<String, String> stringStringMap = callObsDoPath(feignParam);

        attachMap.forEach((k, v) -> {
            if(!CollectionUtils.isEmpty(v)) {
                for (EmergencyAttachVO attachVO : v) {
                    attachVO.setOriginUrl(stringStringMap.get(attachVO.getOriginUrl()));
                }
            }
        });
        vo.setImgUrlList(attachMap.get(Constant.ATTACH_TYPE_IMG));
        vo.setVideoUrlList(attachMap.get(Constant.ATTACH_TYPE_VIDEO));
        vo.setFileUrlList(attachMap.get(Constant.ATTACH_TYPE_FILE));
        vo.setAudioUrlList(attachMap.get(Constant.ATTACH_TYPE_AUDIO));
    }

    /**
     * 文件解密
     * @param feignParam
     * @return
     */
    private Map<String, String> callObsDoPath (List<String> feignParam) {
        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(feignParam);
        return stringStringMap;
    }

    /**
     * 生成事件编码 yyyyMMdd+时间毫秒
     * @return
     */
    public String makeEventCode() {
        //yyyyMMdd+时间毫秒
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        long timeInMillis = System.currentTimeMillis();
        return date + timeInMillis;
    }

}
