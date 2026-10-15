package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDownloadVO extends BaseDataScopeEntity implements Serializable {


    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 页长
     */
    private Integer pageSize;
}
