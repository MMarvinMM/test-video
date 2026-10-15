package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类名称: CheckpointDeviceMapper.java
 *
 * @author lc
 * @version v1.0.0
 * @date 2022/4/24
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface CheckpointDeviceMapper {

    /**
     * 通过条件获取卡口设备信息
     * @param checkpointDeviceInfoVO
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    List<CheckpointDeviceInfoVO> getCheckpointDeviceList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);
}
