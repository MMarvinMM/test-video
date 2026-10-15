package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;

@DS(EbikeDataBaseConstant.BASE)
public interface FunctionValidationMapper extends BaseMapper<FunctionValidationDTO> {
}
