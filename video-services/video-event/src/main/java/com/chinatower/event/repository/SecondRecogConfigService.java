package com.chinatower.event.repository;

import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.SecondRecogConfigVO;
import com.chinatower.event.vo.SecondRecogConfigPageVo;

/**
 * 二次识别配置相关接口
 * @author cuisj
 * @date 2023-12-26
 * @requestID 二次识别配置相关接口
 */
public interface SecondRecogConfigService {

    /**
     * 二次识别配置列表查询
     * @param vo
     * @return
     */
    TableDataInfo findByCondition(SecondRecogConfigPageVo vo);

    /**
     * 二次识别配置新增
     * @param vo
     * @return
     */
    Result<Object> saveSecondRecogConfig(SecondRecogConfigVO vo);

    /**
     * 二次识别配置修改
     * @param vo
     * @return
     */
    Integer updateSecondRecogConfig(SecondRecogConfigVO vo);

    /**
     * 二次识别配置逻辑删除
     * @param vo
     * @return
     */
    Integer deleteSecondRecogConfig(SecondRecogConfigVO vo);

    /**
     * 默认名称：“二次识别配置名称+数字”（数字累加即可）
     * @return
     */
    String getAutoConfigName(String tenantId,String industryCode);

    /**
     * 根据id查找对应的二次识别配置
     * @param id
     * @return
     */
    SecondRecogConfigVO findSecondRecogConfigById(Integer id);

    /**
     * 判断二次识别配置中告警来源X告警类型是否已有配置
     * @param vo
     * @return
     */
    boolean promptInformation(SecondRecogConfigVO vo);

    /**
     * 判断二次识别配置名称是否重复
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param secondRecogName 二次识别配置名称
     * @param id id
     * @return
     */
    Result<Object> secondRecogNameIsRepeat(String tenantId, String industryCode, String secondRecogName,Integer id);
}
