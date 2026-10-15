package com.chinatower.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.device.dto.AlgorithmAppDTO;
import com.chinatower.device.dto.AlgorithmRankDto;
import com.chinatower.device.dto.AlgorithmRankVo;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.CustomerDTO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName IAlgorithmConfigService
 * @Description
 * @Author chenshaowu
 * @Date 2022/1/27
 **/
public interface IAlgorithmConfigInfoService extends IService<AlgorithmConfigDO> {

    /**
     * 列表分页查询算法配置
     * @param algorithmConfigDTO
     * @return
     */
    List<AlgorithmListVO> getAlgorithmConfigList(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 列表分页查询算法配置
     * @param algorithmConfigDTO
     * @return
     */
    List<AlgorithmListVO> getAlgorithmConfigList2(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述 新增算法配置
     * @param algorithmListVO
     * @author: chenshaowu
     * @date: 2022/1/27
     * @return
     */
    Result<List<String>> save(AlgorithmListVO algorithmListVO);

    /**
     * 功能描述 算法配置查看
     * @param algorithmConfigDTO
     * @author: chenshaowu
     * @date: 2022/2/11
     * @return
     */
    List<AlgorithmConfigVO> queryById(AlgorithmConfigDTO algorithmConfigDTO);

    void update(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 算法配置启用停用接口
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    void uptAlgorithmStatus(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述: 批量删除算法信息
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    Result<String> delete(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 单查通道下算法配置
     * @param algorithmConfigDTO
     * @author: chenshaowu
     * @date: 2022/3/7
     * @return
     */
    List<AlgorithmAreaVO> queryAlgorithm(AlgorithmConfigDTO algorithmConfigDTO);

    List<AlgorithmGroupDeviceVO> queryAlgorithmBatch(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 查询算法状态
     * @param taskId
     * @author: chenshaowu
     * @date: 2022/3/8
     * @return
     */
    Result synchAlgorithmConfigStatus(String taskId);
    Result<AlgorithmAppDTO> queryAlgorithmApp(AlgorithmConfigDTO algorithmConfigDTO);

    void delChannelByCodes(List<String> delCollect);

    List<AlgorithmRankDto> getRank(AlgorithmRankVo algorithmRankVo);

    /**
     * 功能描述: 算法配置--查询算法数量详细信息
     * @param algorithmConfigDTO
     * @return List<AlgorithmListVO>
     * @auther: zhaozj
     * @date: 2022/12/08
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    List<OrderAlgorithmsVO> getAlgoCountInfo(AlgorithmConfigDTO algorithmConfigDTO);

    /**
     * 功能描述 查询订购算法列表
     * @author hued
     * @date 2022/2/11
     * @return
     */
    List<OrderAlgorithmsVO> queryAlgorithmList(CustomerDTO customerDTO);

    List<OrderAlgorithmsVO> queryBaselineAlgorithmList(CustomerDTO customerDTO);

    Map<String, List<OrderAlgorithmsVO>> queryBaselineAlgorithmListBatch(List<String> deviceCodeList);

    Result<String> checkAlgorithmConfig(CheckAlgorithmConfigVO checkAlgorithmConfigVO);

    List<IndustryAlgorithmDTO> queryByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    Map<String, List<IndustryAlgorithmDTO>> queryByTenantIdAndDevices(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryAllByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryConfigByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<IndustryAlgorithmDTO> queryGivenByTenantId(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    Set<OrderAlgorithmsVO> intersection(AlgorithmIntersectionVO intersection);

    Result<String> batchAddOrUpdate(List<AlgorithmListVO> list);

    Result<String> dealHisAlgorithm();

    List<LinkageAlgorithmMappingVO> queryLinkageAlgorithmList(LinkageAlgorithmMappingVO linkageAlgorithmMappingVO);

    Result<List<AlgorithmConfigVO>> insertAlgoConfigMCP(List<AlgorithmConfigAdd> algorithmConfigAdd);

    Boolean checkChannelAttr(DeviceInfoVO deviceInfoVO);

    List<IndustryAlgorithmDTO> querySensingAlgorithm(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<String> getBigModelAlgList();
    List<AlgAndSceResultVO> getAlgAndSceList(AlgAndSceVO algAndSceVO);
    String updateAlgorithmHis(AlgorithmConfigDTO vo);

    String getProvinceByPlatform();

    /**
     * 修复大模型算法model_config中缺少monitorAreaId的问题
     * @return 修复结果
     */
    Result<String> fixModelConfig();
}
