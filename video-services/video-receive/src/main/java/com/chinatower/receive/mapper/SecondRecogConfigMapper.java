package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.SecondRecogReceiveVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 16:50
 */

@Mapper
public interface SecondRecogConfigMapper {


    /**
     * 二次识别配置条件查询
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    List<SecondRecogReceiveVO> selectSecondRecogByOnes(@Param("list")List<String> list);
}
