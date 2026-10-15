package com.chinatower.platform.project.system.mapper;

import com.chinatower.system.api.model.VmCommonWordsVO;
import com.chinatower.system.api.request.words.wordsRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 常用语
 */
public interface SysCommonWordsMapper {


    /**
     * 查询常用语
     *
     * @param vo
     * @return 结果
     */
    List<VmCommonWordsVO> getCommonWords(wordsRequest vo);
    /**
     * 增加常用语
     *
     * @param vo
     * @return 结果
     */

    int add(wordsRequest vo);


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
     * 查询失败描述
     *
     * @param falseCode
     * @return 结果
     */
    String queryFalseWords(String falseCode);

    /**
     * 查询巡查任务常用语
     * @param tenantId
     * @param industryCode
     * @return 结果
     */
    List<VmCommonWordsVO> getCommonWordsForInspection(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("typeId") String typeId);


    /**
     * 批量新增巡查任务常用语
     * @param commonWordsDTOList
     */
    void addCommonWordsBatchForInspection(@Param("list") List<wordsRequest> commonWordsDTOList);


    /**
     * 批量删除巡查任务常用语
     *
     * @param vo
     * @return 结果
     */
    public int deleteCommonWordsBatchForInspection(wordsRequest vo);

    /**
     * 批量新增常用语
     * @param commonWordsDTOList
     */
    int addCommonWordsBatch(@Param("list") List<wordsRequest> commonWordsDTOList);

    void updateCommonWordsBatch(List<wordsRequest> list);
}
