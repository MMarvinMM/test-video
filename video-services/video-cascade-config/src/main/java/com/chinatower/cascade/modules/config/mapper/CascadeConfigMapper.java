package com.chinatower.cascade.modules.config.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.cascade.api.entity.PlatformDataVO;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 功能说明：平台配置信息mapper
 *
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Mapper
public interface CascadeConfigMapper {
    
    int insertBatch(List<CascadeConfigSyncVO> list);

    int updateBatch(List<CascadeConfigSyncVO> list);

    int deleteBatch(List<CascadeConfigSyncVO> list);


    @InterceptorIgnore(blockAttack = "true")
    int deleteAll();

    List<PlatformDataConfigVO> getInfo(List<String> list);

    List<CascadeConfigSyncVO> getAllConfigInfo();

    List<PlatformDataVO> queryListByKey(String key);

    List<String> findWgtByColonyCodeList(List<String> list);

    void addBatchWgtVersion(Set<String> list);

    int existAlias(String platformCode);

    void deleteByUpgradeIsNull(List<String> list);

    List<String> findNoneWgtVersion();

    List<String> findNoneMiniProgramVersion();

    void deleteByMiniProgramUpgradeIsNull(List<String> list);

    List<String> findMiniProgramByColonyCodeList(List<String> list);

    void addBatchMiniProgramVersion(Set<String> list);

}