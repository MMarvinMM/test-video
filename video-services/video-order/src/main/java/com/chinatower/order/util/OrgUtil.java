package com.chinatower.order.util;

import com.chinatower.platform.mapper.AuthBaseMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrgUtil {

    @Autowired
    private static AuthBaseMapper authBaseMapper;

    @Autowired
    public OrgUtil(AuthBaseMapper authBaseMapper) {
        OrgUtil.authBaseMapper = authBaseMapper;
    }

    private static final String SEP = "-";

    public static String getOrgCodeByParentOrgIdAndTenantId(String parentId, String tenantId) {
        String orgId = "";
        // 如果父节点为空，查询判断是否有其他根节点，若没有，则新增新的，有，则再原先的根节点基础上加1
        if (StringUtils.isBlank(parentId) || "000000".equals(parentId)) {
            List<String> rootOrgs = authBaseMapper.getRootOrgsByTenantId(tenantId, parentId);
            if (CollectionUtils.isEmpty(rootOrgs)) {
                orgId = tenantId + SEP + String.format("%0" + 2 + "d", 1);
            }
            else {
                int max = rootOrgs.stream().mapToInt(l -> Integer.parseInt(l.split("-")[1])).max().getAsInt();
                orgId = tenantId + SEP + String.format("%0" + 2 + "d", ++max);
            }
        }
        else {
            // 父节点不为空，取当前父节点下的所有字节的，拼接最大值
            List<String> rootOrgs = authBaseMapper.getRootOrgsByTenantId(tenantId, parentId);
            if (CollectionUtils.isEmpty(rootOrgs)) {
                orgId = parentId + String.format("%0" + 2 + "d", 1);
            }
            else {
                int max = rootOrgs.stream().mapToInt(l -> Integer.parseInt(getLastTwo(l.split("-")[1]))).max()
                    .getAsInt();
                orgId = parentId + String.format("%0" + 2 + "d", ++max);
            }
        }
        return orgId;
    }

    /**
     *
     * @param parentId 父节点
     * @param tenantId 客户编码
     * @param num 生成数量
     * @return 生成组织id列表
     */
    public static List<String> getOrgCodeByParentOrgIdAndTenantIdV2(String parentId, String tenantId, int num) {
        List<String> orgIds = Lists.newArrayList();
        if (!"000000".equals(parentId)) {
            return orgIds;
        }
        List<String> rootOrgList = authBaseMapper.getRootOrgsByTenantId(tenantId, parentId);
        for (int i = 0; i < num; i++) {
            String orgId = "";
            // 如果父节点为空，查询判断是否有其他根节点，若没有，则新增新的，有，则再原先的根节点基础上加1
            if (CollectionUtils.isEmpty(rootOrgList)) {
                orgId = tenantId + SEP + String.format("%0" + 2 + "d", 1);
            } else {
                int max = rootOrgList.stream().mapToInt(l -> Integer.parseInt(l.split("-")[1])).max().getAsInt();
                orgId = tenantId + SEP + String.format("%0" + 2 + "d", ++max);
            }
            rootOrgList.add(orgId);
            orgIds.add(orgId);
        }
        return orgIds;
    }

    private static String getLastTwo(String strhours) {
        return strhours.substring(strhours.length() - 2);
    }
}
