package com.chinatower.platform.mapper;

import com.chinatower.platform.entity.admin.SysDictType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: 字典表 数据层
 * @Author: luo_ji
 * @CreateDate: 2022/12/12 14:53
 * @Version: 1.0
 */
@Mapper
public interface SysDictTypeMapper {

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeList(SysDictType dictType);


}
