package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AnalysisDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/16
 **/

@Data
@ApiModel(description = "重复报警信息查询入参")
public class AnalysisDTO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String creator;
    private String tenantId;
    private String customerCode;
    private String dimensionFlag;
    private String queryStartTime;
    private String queryEndTime;
    private String provinceId;
    private String cityId;
    private String countyId;

    protected String tableName;

    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    protected transient Object list;
    protected Date startTime;
    protected Date endTime;

    /** 行业应用编码 */
    private String appVerCode;

    private List<String> deviceCodeList;

}
