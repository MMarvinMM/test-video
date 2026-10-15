package com.chinatower.emergency.service;


import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.emergency.dto.EmergencyResourceVO;

import java.util.List;

public interface EmergencyResourceService {

    /**
     * 获取应急资源树(tree格式用)
     * @param vo 资源信息
     * @return 返回资源集合
     * @author zhaol
     * @throws
     * @requestID       *
     */
    List<TreeSelectByBdm> getEmergencyResourceTree(EmergencyResourceVO vo);

    /**
     * 资源调派
     * @author liuxt
     * @param list
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    int addEmergencyResource(List<EmergencyResourceVO> list);

    /**
     * 功能说明: 删除调派资源
     * @author liuxt
     * @param emergencyResourceVO
     * @return 删除结果
     * @throws
     * @throws
     * @requestID       *
     */
    int deleteEmergencyResource(EmergencyResourceVO emergencyResourceVO);



}
