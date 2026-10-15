package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.ProcessOrderDO;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import com.chinatower.event.vo.ProcessOrderVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
public interface ProcessOrderMapper extends BaseMapper<ProcessOrderDO> {

    int insertProcessOrderList(List<ProcessOrderDTO> list);

    /**
     * 查询
     * @param a
     * @return
     */
    ProcessOrderVO getProcessOrder(ProcessOrderDTO a);

    /**
     * 查询待处置环节得处置单详情
     * @param a
     * @return
     */
    ProcessOrderVO getProcessOrderPendding(ProcessOrderDTO a);

    /**
     * 更新
     * @param a
     * @return
     */
    int updateProcessOrder(ProcessOrderVO a);

    /**
     * 插入处理照片视频表
     * @param map
     * @return
     */
    int insertPhotoOrVideo(Map map);

    /**
     * 插入处理单表
     * @param a
     * @return
     */
    int insertProcessOrder(ProcessOrderDTO a);

    /**
     * 获取处置单详情
     * @param processOrderId
     * @return
     */
    Map getProcessInfo(String processOrderId);

    /**
     * 查询处置图片
     * @param processOrderId
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<Map> getPhotos(String processOrderId);
}
