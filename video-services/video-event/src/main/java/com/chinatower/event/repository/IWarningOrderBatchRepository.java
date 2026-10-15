package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.BatchDelWarningOrderDTO;
import com.chinatower.event.dto.BatchEndWarningOrderDTO;
import com.chinatower.event.dto.BatchJudgeWarningOrderDTO;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IWarningOrderBatchRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/29 16:07
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IWarningOrderBatchRepository {


    /**
     * 批量删除告警数据
     * @param dto
     * @return
     */
    Result batchDelWarningOrder(BatchDelWarningOrderDTO dto);

    /**
     * 批量关闭告警
     * @param dto
     * @return
     */
    Result batchEndWarningOrder(BatchEndWarningOrderDTO dto);

    /**
     * 批量研判
     * @param dto
     * @return
     */
    Result batchJudgeWarningOrder(BatchJudgeWarningOrderDTO dto);
}
