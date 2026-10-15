package com.chinatower.emergency.mapper;

import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.emergency.dto.EmergencyResourceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmergencyResourceMapper {


    /**
     * 获取租户资源集合(tree格式用)
     * @author zhaol
     * @param vo 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID       *
     */
    List<TreeSelectByBdm> getEmergencyResourceTree(EmergencyResourceVO vo);

    int addEmergencyResource(List<EmergencyResourceVO> list);

    int deleteEmergencyResource(EmergencyResourceVO vo);


    int deleteEmergencyResourceByEventCodes(EmergencyResourceVO emergencyResourceVO);

    void updateResourceStatus(EmergencyResourceVO emergencyResourceVO);
}
