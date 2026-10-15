package com.chinatower.event.mapper;

import com.chinatower.event.dto.SysLinkViewUrlModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 行业应用配置Mapper
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 15:12
 */
@Mapper
public interface SysIndustryViewUrlMapper {

    List<SysLinkViewUrlModel> querySysLinkViewUrlAll();
}
