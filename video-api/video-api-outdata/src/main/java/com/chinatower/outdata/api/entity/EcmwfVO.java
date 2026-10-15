package com.chinatower.outdata.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class EcmwfVO extends BaseDataScopeEntity {

    /**
     * 查询类型，sp:压强;tp:总降水量;10v:东西向风;10u:南北向风;r:相对湿度;2t:2m气温
     * @required true
     */
    private String ecmwfType;
    /**
     * 查询时间，格式yyyy-MM-dd HH:mm:ss
     * @required true
     */
    private String queryDate;


}
