package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.event.vo.entity.AnalysisVO;
import com.chinatower.event.vo.entity.UavDeviceStatisticsVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 无人机设备信息mapper
 *
 * @ClassName UavDeviceMapper
 * @auther: luo.zhenghong
 * @date: 2023/6/8
 * @version: release_v1.5.95
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface UavDeviceMapper {

    List<String> queryUavNestCodeByCondition(@Param("param") UavDeviceDTO param);

    /**
     * 根据条件查询无人机设备及其通道信息
     *
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavDeviceInfoList(@Param("param") UavDeviceDTO param);

    UavDeviceStatisticsVo getUavDeviceStatistics(AnalysisVO analysisVO);

}
