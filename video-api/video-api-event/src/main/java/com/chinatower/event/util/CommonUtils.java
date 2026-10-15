package com.chinatower.event.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.ArrayUtil;

public class CommonUtils {

    /**
     * 是否含有领导角色
     *
     * @param roles
     * @return
     */
    public static boolean ifLeaderRole(Long[] roles) {
        for (Long roleId : roles) {
            if (roleId != null) {
                if (EnumUtil.BaseRole.COMPANY_LEADER.getCode().longValue() == roleId.longValue()
                        || EnumUtil.BaseRole.DEPARTMENT_LEADER.getCode().longValue() == roleId.longValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将json对象中包含的null和JSONNull属性修改成""
     *
     * @param jsonObj
     */
    public static JSONObject filterNull(JSONObject jsonObj) {
        Set<String> it = jsonObj.keySet();
        Object obj = null;
        for (String key : it) {
            obj = jsonObj.get(key);
            if (obj instanceof JSONObject) {
                filterNull((JSONObject) obj);
            }
            if (obj instanceof JSONArray) {
                JSONArray objArr = (JSONArray) obj;
                for (int i = 0; i < objArr.size(); i++) {
                    filterNull(objArr.getJSONObject(i));
                }
            }
            if (obj == null) {
                jsonObj.put(key, "");
            }
        }
        return jsonObj;
    }

    /**
     * 获取当前组织编码上级所有编码
     *
     * @param orgId
     * @return
     */
    public static List<String> getOrgIds(String orgId) {
        if (StringUtils.isEmpty(orgId)) {
            return null;
        }
        List<String> orgIds = new ArrayList<>();
        if (orgId.indexOf("-") >= 0) {
            String[] s = orgId.split("-");
            String startStr = s[0];
            String endStr = s[1];

            boolean ifHasNext = true;
            StringBuilder stringBuilder;
            String temp = endStr;

            while (ifHasNext) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(startStr).append("-").append(temp);
                orgIds.add(stringBuilder.toString());
                if (temp.length() > 2) {
                    temp = temp.substring(0, temp.length() - 2);
                } else {
                    ifHasNext = false;
                }
            }
        } else {
            // 组织编码不包含-，直接返回
            orgIds.add(orgId);
        }
        return orgIds;
    }

    /**
     * @param date        时间
     * @param tableModule 分表前缀
     * @return java.lang.String 存在返回表名，不存在返回nil
     * @Method: getTableName
     * @Description: 根据时间获取分表名
     * @author : gaoziying
     * @CreateDate: 2017/12/1 下午2:40
     */
    public static String getTableName(Date date, String tableModule) {
        ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<>();
        SimpleDateFormat sdf = dateFormat.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyyMM");
            dateFormat.set(sdf);
        }
        String tableName = tableModule + sdf.format(date);
        return tableName;
    }

    /**
     * 是否是处置角色
     * @param roleIds
     * @return
     */
    public static boolean ifHandleRole(Long[] roleIds) {
        if (!ArrayUtil.isEmpty(roleIds)) {
            for (Long id : roleIds) {
                if (id!=null && EnumUtil.BaseRole.HANDLE_MAN.getCode().longValue() == id.longValue()) {
                    return true;
                }
            }
        }
        return false;
    }
}
