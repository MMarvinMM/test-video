package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.ProcessCommonWordsDO;
import com.chinatower.event.entity.dto.ProcessCommonWordsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  事件处理常用语 Mapper 接口
 * </p>
 *
 * @author lyy
 * @since 2023/01/29
 */
public interface ProcessCommonWordsMapper extends BaseMapper<ProcessCommonWordsDO> {

    /**
     * 查询事件处理常用语
     * @param tenantId
     * @param industryCode
     * @return 结果
     */
    List<ProcessCommonWordsDTO> getProcessCommonWords(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 批量新增事件处理常用语
     * @param commonWordsDTOList
     */
    void addProcessCommonWordsBatch(@Param("list") List<ProcessCommonWordsDTO> commonWordsDTOList);
}
