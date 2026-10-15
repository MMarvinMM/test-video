package com.chinatower.event.vo;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 请求参数接收实体
 * @author chmod
 * @version 1.0
 * @date 2022/3/12 14:46
 */
@Data
@ToString
public class SiteRequestVO extends BasePageDTO implements Serializable {
    private static final long serialVersionUID = -1435367550220441254L;
    //租户id
    private String tenantId;

    //站址id
    private String siteCode;

    //区县id
    private String countyId;

    //设备编码
    private String deviceCode;

    //设备名称
    private String devName;

    //0在线 1离线
    private String status;

    private String anthType;

    private List<String> devList;

    private String userId;

    private String orgId;

    private List<String> tenantList;

    private String orgLevel;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    //方圆几公里内
    private String distance;

    private String provinceId;
    private String cityId;
    private String creator;

    private List<String> orgIdList;

    private String likeOrgId;
    private List<String> deviceCodeList;

    private String source;
    private List<Long> labelIdList;
    /**
     * 行业应用编码
     */
    private String appVerCode;
}
