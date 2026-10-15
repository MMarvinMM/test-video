package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应急部抓图任务查询结果。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YjbSnapshotTaskRsp implements Serializable {

    /** 服务端生成的任务编码。 */
    private String taskId;

    /** 任务明细总数。 */
    private Integer total;

    /** 已完成抓图数量。 */
    private Integer completed;

    /** 抓图进度，取值范围0到100。 */
    private Integer progress;

    /** 已完成抓图明细。 */
    private List<YjbSnapshotItemRsp> data;

    /** 抓图下发失败明细，仅在创建任务时返回。 */
    private List<YjbSnapshotFailedItemRsp> failedData;
}
