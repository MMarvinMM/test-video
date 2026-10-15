package com.chinatower.platform.modules.video.dao;

import com.chinatower.platform.modules.video.entity.ChannelVideoPlansVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 沉淀录像计划记录任务定时
 */
@Mapper
public interface VideoDAO {

    /**
     * 获取录像计划数据
     * @return
     */
    List<ChannelVideoPlansVO> getList();



    /**
     * 插入沉淀表
     * @return
     */
    int insert();

}
