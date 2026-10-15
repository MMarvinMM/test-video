package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 10:03
 */
@Mapper
public interface SysComMessageMapper {
    /**
     * 查询融合通信列表
     *
     * @param communicationVo
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CommunicationVo> queryList(CommunicationVo communicationVo);

    /**
     * 编辑融合通信信息
     *
     * @param communicationVo
     */
    @DS(EbikeDataBaseConstant.BASE)
    @Transactional
    void editCommunicationInfo(CommunicationVo communicationVo);

    /**
     * 新增融合通信信息
     *
     * @param communicationVo
     */
    @DS(EbikeDataBaseConstant.BASE)
    @Transactional
    void insertCommunicationInfo(CommunicationVo communicationVo);

    /**
     * 新增融合通信信息
     *
     * @param checkTelNumber
     */
    @DS(EbikeDataBaseConstant.BASE)
    CommunicationVo queryInfoByTel(@Param("checkTelNumber") String checkTelNumber,@Param("communicationId") String communicationId);
}
