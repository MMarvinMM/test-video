package com.chinatower.manage.api.dto;


import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 属地版本管理实体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizedVersion extends PageDTO implements Serializable {

    /**
     * 属地版本id
     */
    private Long id;

    /**
     * 属地版本id
     */
    private Long localizedVersionId;

    /**
     * 属地版本号
     */
    private String localizedVersion;

    /**
     * 属地平台域名
     */
    private String localizedDomainName;

    /**
     * 创建者
     */
    private String creator;


    /**
     * 更新者
     */
    private String modifier;

    private String createTimeStart;

    private String createTimeEnd;

    private String updateTimeStart;

    private String updateTimeEnd;

    private String sort;

    private String sortName;

    /**
     * 所属行业应用编码
     */
    private List<String> industryCodeList;

    /**
     * 视角：0铁塔 1客户
     */
    private List<String> ifTower;

    List<LocalizedConfig> localizedConfigList;

    List<LocalizedDictType> localizedDictTypeList;

    List<LocalizedDictData> localizedDictDataList;

    List<LocalizedMenu> localizedMenuList;

    /**
     * 中心域名前缀
     */
    private List<String> centerScreenUrlPrefix;

    //大屏配置
    List<SysIndustryScreenEditionVO> screenInfo;

    //行业配置
    List<SysIndustryConfEdition> industryConfigInfo;

    //应用配置
    List<SysIndustryVerConfEdition> industryVerConfigInfo;

    //应用大屏配置
    List<SysIndustryViewRelEdition> industryViewRelInfo;

    //菜单权限
    List<SysIndustryAuthEdition> industryAuthInfo;

    //菜单与角色权限
    List<SysRoleIndustryAuthEdition> roleIndustryAuthInfo;

    //数据与角色权限
    List<SysRoleDataRelEdition> roleDataRelInfo;

    //资源
    List<ResourceTypeIntEdition> resourceTypeIntInfo;

    //行业默认流程环节信息
    List<LocalizationFlowLinkIndustryConfigDO> localizedFlowLinkList;

    //行业默认流程
    List<LocalizationFlowTemplateIndustryDO> localizedFlowTemplateList;

    private Integer offset;
    private Integer limit;

    private String industryCodeStr;

    private String ifTowerStr;

    /**
     * 中心版本id
     */
    private String centerId;
}
