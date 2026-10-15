package com.chinatower.device.mapper;


import com.chinatower.device.dto.ResourceConfVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceConfMapper {


    List<ResourceConfVO> selectResourceList(ResourceConfVO record);

}
