package com.chinatower.event.repository;

import com.chinatower.common.core.vo.CommunicationVo;
import com.chinatower.common.entity.Result;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/10/21 9:53
 */
public interface SysComMessageService {
    /**
     * 查询融合通信列表
     *
     * @param communicationVo
     * @return
     */
    List<CommunicationVo> queryList(CommunicationVo communicationVo);

    /**
     * 编辑融合通信信息
     *
     * @param communicationVo
     * @return
     */
    Result<String> editCommunicationInfo(CommunicationVo communicationVo);

    /**
     * 通过号码查询用户信息
     *
     * @param communicationVo
     * @return
     */
    CommunicationVo queryInfoByTel(CommunicationVo communicationVo);
}
