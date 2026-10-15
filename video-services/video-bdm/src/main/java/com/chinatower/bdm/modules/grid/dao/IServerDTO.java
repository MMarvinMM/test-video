package com.chinatower.bdm.modules.grid.dao;

import lombok.Builder;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/3/3 10:59
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class IServerDTO extends BboxDTO{
    private String wmsUrl;
    private String wfsUrl;
    private String wmsName;
    private String typeName;
    private String wfsName;
    private String srs;


}
