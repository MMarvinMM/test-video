package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.SysAdcodeLocationDO;
import com.chinatower.order.mapper.SysAdcodeLocationMapper;
import com.chinatower.order.repository.ISysAdcodeLocationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName SysAdcodeLocationRepositoryImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 15:43
 * @Version 1.0
 **/
@Repository
public class SysAdcodeLocationRepositoryImpl extends ServiceImpl<SysAdcodeLocationMapper, SysAdcodeLocationDO> implements ISysAdcodeLocationRepository {


    /**
     * 查询全部数据
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    @Override
    public List<SysAdcodeLocationDO> getAllList() {
        return this.list();
    }
}
