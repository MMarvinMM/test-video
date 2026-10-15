package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.UavMountModelVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UavMountModelMapper {

    /**
     * 根据型号编码查询
     *
     * @param modelCode
     * @return
     */
    UavMountModelVO selectByCode(String modelCode);

    int deleteByModelCode(String modelCode);

    /**
     * 更新数据
     *
     * @param mountModelVO
     * @return
     */
    int updateByModelCode(UavMountModelVO mountModelVO);

    List<UavMountModelVO> getAllUavMountModel();

    int batchInsert(List<UavMountModelVO> list);

    int batchDelete(List<Long> Ygids);

    void batchUpdate(List<UavMountModelVO> list);
}
