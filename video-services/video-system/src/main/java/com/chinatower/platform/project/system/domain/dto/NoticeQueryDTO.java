package com.chinatower.platform.project.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class NoticeQueryDTO extends BaseDataScopeEntity {

    private Long noticeId;

    private Long[] noticeIds;

    private String status;

    @TableField(exist = false)
    private String platformCode;
}
