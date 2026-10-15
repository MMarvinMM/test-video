package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.WarningInterceptDO;
import com.chinatower.event.dto.InterceptDTO;
import com.chinatower.event.entity.dto.IntercepiResultVo;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.InterceptVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 告警拦截信息表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
public interface IWarningInterceptRepository extends IService<WarningInterceptDO> {

    /**
     *
     * 功能描述:
     * 回收站列表
     */
    IntercepiResultVo getInterceptList(WarningInterceptDTO dto);


    /**
     * 详情
     * @return
     */
    List<Map> getInterceptInfo(WarningInterceptDTO dto);

    /**
     * 真警处理
     * @param dto
     */
    Result updateIntercept(InterceptDTO dto);

    /**
     * 真警处理(还原告警数据)
     * @param dto
     */
    Result restoreWarningOrder(InterceptDTO dto);
}
