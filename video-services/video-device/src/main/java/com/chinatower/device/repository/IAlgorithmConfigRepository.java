package com.chinatower.device.repository;

import com.chinatower.platform.entity.device.AlgorithmConfigVO;
import com.chinatower.platform.entity.device.AlgorithmListVO;
import com.chinatower.platform.entity.device.AlgorithmConfigDTO;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 算法配置 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
public interface IAlgorithmConfigRepository extends IService<AlgorithmConfigDO> {

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
     * 功能描述 
     * 更新算法配置状态
     * @return 
     * @author chenmf
     * @date 2022/2/9
     * @version
     */
//    void updateAlgorithmStatus(String taskId, String onstatus);

    /**
     * 功能描述 查询算法配置
     *
     * @param algorithmConfigDTO
     * @return
     * @author: chenshaowu
     * @date: 2022/2/11
     */
    List<AlgorithmConfigVO> queryalgorithmConfig(AlgorithmConfigDTO algorithmConfigDTO);
}
