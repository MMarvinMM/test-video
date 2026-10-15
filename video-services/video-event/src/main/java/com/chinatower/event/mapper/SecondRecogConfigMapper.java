package com.chinatower.event.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.CombinationVo;
import com.chinatower.event.entity.vo.SecondRecogConfigVO;
import com.chinatower.event.entity.vo.SecondRecogReceiveVO;
import com.chinatower.event.vo.SecondRecogConfigPageVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 二次识别配置相关接口
 * @author cuisj
 * @date 2023-12-26
 * @requestID 二次识别配置相关接口
 */
@Mapper
public interface SecondRecogConfigMapper{

    /**
     * 二次识别配置列表查询
     * @param vo
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SecondRecogConfigVO> findByCondition(@Param("vo") SecondRecogConfigPageVo vo);

    /**
     * 根据id查询二次识别配置
     * @param id
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SecondRecogConfigVO findSecondRecogConfigById(Integer id);

    /**
     * 默认名称：“二次识别配置名称+数字”（数字累加即可）
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    String getAutoConfigName(String tenantId,String industryCode);

    /**
     * 二次识别配置修改
     * @param vo
     * @return
     */
    Integer findUpdateById(SecondRecogConfigVO vo);

    /**
     * 二次识别配置添加
     * @param vo
     * @return
     */

    Integer SaveSecondRecogConfig(SecondRecogConfigVO vo);

    /**
     * 二次识别配置逻辑删除
     * @param vo
     */
    Integer findUpdateDeleteFlagById(SecondRecogConfigVO vo);

    /**
     *根据二次识别配置名称查询id
     * @param name 二次识别配置名称
     * @return
     */
    Integer findIdBySecondRecogName(String name);


    /**
     * 去vm_alarm_type_second_recog表中查出 包含前端传来的  告警来源值-告警类型编码   8-1979
     * @param list 告警来源值-告警类型编码集合
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CombinationVo> getCombinedResults(List<String> list,String tenantId,String industryCode);

    /**
     * 批量删除vm_second_recog_config_t表数据
     * @param list
     * @return
     */
    Integer updateDeleteFlag (List<CombinationVo> list);

    /**
     * 二次识别配置条件查询
     * @param vo
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SecondRecogReceiveVO> selectSecondRecogByOne(SecondRecogReceiveVO vo);

    /**
     * 二次识别配置条件查询
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    List<SecondRecogReceiveVO> selectSecondRecogByOnes(@Param("list")List<String> list);

    /**
     * 根据租户id、行业编码以及二次识别配置名称查询
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param secondRecogName 二次识别配置名称
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SecondRecogConfigVO secondRecogNameIsRepeat(
            @Param("tenantId") String tenantId,
            @Param("industryCode") String industryCode,
            @Param("secondRecogName") String secondRecogName
            );
}
