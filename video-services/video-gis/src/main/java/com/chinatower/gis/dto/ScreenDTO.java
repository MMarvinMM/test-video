package com.chinatower.gis.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ScreenDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/18
 **/

@Data
@ApiModel(description = "图层统计查询入参")
public class ScreenDTO extends BaseDataScopeEntity {

    private String tenantId;
    private String level;
    private List<String> areaList;
    private List<String> provinceList;
    private List<String> areaCode;

    /**
     * 查询几天内的数据
     */
    private String dayNum;

    /**
     * 自定义时间段：开始时间 年月日
     */
    private String webStartDate;
    /**
     * 自定义时间段：结束时间 年月日
     */
    private String webEndDate;

    /** 省编码 */
    protected List<String> areaCodes;

    /** 组织编码 */
    protected List<String> orgCodes;

    /** 用户编码 */
    protected List<String> userIds;

    /**摄像头编码 */
    protected List<String> devices;

    /**区县编码 */
    protected List<String> countys;

    /**订单状态 */
    protected List<String> warningOrderStatus;

    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    protected Object list;
    protected Date startTime;
    protected Date endTime;
}
