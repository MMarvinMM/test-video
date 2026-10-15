package com.chinatower.manage.dto.open;

import com.chinatower.manage.dataobject.open.OpenIntfInfoVO;
import com.chinatower.manage.dataobject.open.OpenIntfReqInfoVO;
import com.chinatower.manage.dataobject.open.OpenManageInfoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *  SDK接口管理实体
 * @author tianhao
 * @version 1.0
 * @date 2022-6-27
 */
@Data
public class OpenIntfWholeInfoModel implements Serializable {
    private static final long serialVersionUID = -6304589028686390170L;

    /** 开放平台接口基本信息*/
    private OpenManageInfoVO openManageInfo;

    /** 开放平台接口扩展信息*/
    private OpenIntfInfoVO openIntfInfo;

    /** 开放平台属性信息集合*/
    private List<OpenIntfReqInfoVO> openIntfReqInfos;

    /** 开放平台属性信息集合*/
    private Map<String, List<OpenIntfReqInfoVO>> openIntfReqInfosMap;
}
