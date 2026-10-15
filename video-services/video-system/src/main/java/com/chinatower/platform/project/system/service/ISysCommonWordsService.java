package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.system.api.model.VmCommonWordsVO;
import com.chinatower.system.api.request.words.wordsRequest;

import java.util.List;

public interface ISysCommonWordsService {

    /**
     * 常用语查询
     * @param wordsVO
     * @return
     */
     List<VmCommonWordsVO> getCommonWords(wordsRequest wordsVO);
    /**
     * 新增常用语
     * @param vo
     * @return
     */
    Result add(wordsRequest vo);

    /**
     * 删除常用语
     *
     * @param vo
     * @return 结果
     */
    public int deleteCommonWordsByWordsId(wordsRequest vo);
    /**
     * 批量删除常用语
     *
     * @param vo
     * @return 结果
     */
    public int deleteCommonWordsByWordsIdBatch(wordsRequest vo);

    /**
     * 修改常用语
     *
     * @param vo
     * @return 结果
     */
    int updateCommonWords(wordsRequest vo);

    /**
     * 修改常用语
     *
     * @param vo
     * @return 结果
     */
    void updateCommonWordsBatch(wordsRequest vo);

    /**
     * 查询失败描述
     *
     * @param falseCode
     * @return 结果
     */
    String queryFalseWords(String falseCode);

    /**
     * 常用语查询
     * @param wordsVO
     * @return
     */
    List<VmCommonWordsVO> getCommonWordsForInspection(wordsRequest wordsVO);

    /**
     * 编辑巡查任务常用语
     * @param commonWordsDTOList
     */
    void updateCommonWordsForInspection(List<wordsRequest> commonWordsDTOList);

    /**
     * 批量新增常用语
     * @param commonWordsDTOList
     */
    Result addCommonWordsBatch(List<wordsRequest> commonWordsDTOList);
}
