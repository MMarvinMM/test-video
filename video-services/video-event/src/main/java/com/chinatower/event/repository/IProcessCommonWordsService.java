package com.chinatower.event.repository;

import com.chinatower.event.entity.dto.ProcessCommonWordsDTO;

import java.util.List;

/**
 * 事件处理常用语业务接口
 * @author lyy
 * @version 1.0
 * @date 2023/01/29
 */
public interface IProcessCommonWordsService {

    /**
     * 事件处理常用语查询
     * @return
     */
     List<ProcessCommonWordsDTO> getProcessCommonWords();

    /**
     * 编辑事件处理常用语
     * @param commonWordsDTOList
     */
     void updateProcessCommonWords(List<ProcessCommonWordsDTO> commonWordsDTOList);
}
