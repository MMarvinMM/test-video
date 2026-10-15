package com.chinatower.algorithm.mapper;

import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.chinatower.platform.entity.device.AlgAndSceSceVO;
import com.chinatower.platform.entity.device.AlgAndSceVO;
import com.chinatower.platform.entity.device.AlgorithmAreaVO;
import com.chinatower.platform.entity.device.AlgorithmConfigDTO;

import java.util.List;

/**
 * <p>
 * 算法基本信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
public interface AlgorithmInfoMapper extends BaseMapper<AlgorithmInfoDO> {

    /**
     * 功能描述: 根据算法CODE获取算法详情信息
     *
     * @param: algorithmInfoDO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/08/23
     * @version: release_v1.5
     */
    List<AlgorithmInfoDO> getAlgorithmInfo(AlgorithmInfoDO algorithmInfoDO);

    List<IndustryAlgorithmDTO> queryByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryByTenantIdAndDevice(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryConfigByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryGivenByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryByTenantIdWithExtendJson(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<AlgorithmAreaVO> getAlgAndSceList(AlgAndSceVO algAndSceVO);

    List<AlgAndSceSceVO> getSceneInfoList(List<String> sceneList);

    List<AlgAndSceSceVO> getSceneInfoListByTenant(List<AlgorithmConfigDO> list);

    List<AlgorithmConfigDO> getHisConfigList(AlgorithmConfigDTO vo);

    int updateAlgorithmHisBatch(List<AlgorithmConfigDO> list);

    String getAreaNameByCode(String areaCode);
}
