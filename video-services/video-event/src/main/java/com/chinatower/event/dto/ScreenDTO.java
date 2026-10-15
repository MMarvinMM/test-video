package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @ClassName ScreenDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/18
 **/

@Data
@ApiModel(description = "图层统计查询入参")
public class ScreenDTO extends BaseDataScopeEntity implements Serializable {

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
    private List<String> areaCodes;

    /** 组织编码 */
    private List<String> orgCodes;

    /** 用户编码 */
    private List<String> userIds;

    /**摄像头编码 */
    private List<String> devices;

    /**区县编码 */
    private List<String> countys;

    /**订单状态 */
    private List<String> warningOrderStatus;

    private Integer pageNum;
    private Integer pageSize;
    private Integer offset;
    private Integer limit;
    private Integer total;
    //private Object list;
    private Date startTime;
    private Date endTime;
    /** 客户编码 */
    private List<String> customerIds;
}
