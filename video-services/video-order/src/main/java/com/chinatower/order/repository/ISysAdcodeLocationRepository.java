package com.chinatower.order.repository;


import com.chinatower.order.domain.SysAdcodeLocationDO;

import java.util.List;

/**
 * @ClassName ISysAdcodeLocationRepository
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
public interface ISysAdcodeLocationRepository {
    
    /**
     * 查询全部数据
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    List<SysAdcodeLocationDO> getAllList();
}
