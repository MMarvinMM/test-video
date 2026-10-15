package com.chinatower.receive.mapper;

import com.chinatower.receive.vo.entity.CheckpointAlarmOriginalAreaVO;
import com.chinatower.receive.vo.entity.CheckpointAlarmOriginalPointVO;
import com.chinatower.receive.vo.entity.ReceiveCheckpointAlarmVO;
import org.apache.ibatis.annotations.Mapper;

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
public interface CheckpointDeviceAlarmMapper {

    /**
     * 保存车辆卡口摄像机告警信息
     *
     * @param list
     * @return
     */
    int insertCheckpointDeviceAlarmInfo(List<ReceiveCheckpointAlarmVO> list);

    /**
     * 批量保存告警原始图片区域
     *
     * @param list
     * @return
     */
    int insertOriginalAreas(List<CheckpointAlarmOriginalAreaVO> list);

    /**
     * 批量保存告警原始图片区域点
     *
     * @param list
     * @return
     */
    int insertOriginalPoints(List<CheckpointAlarmOriginalPointVO> list);

}
