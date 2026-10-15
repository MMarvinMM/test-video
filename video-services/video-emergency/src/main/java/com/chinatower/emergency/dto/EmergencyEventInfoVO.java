package com.chinatower.emergency.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: EmergencyEventInfoVOVO
 * @description: 事件实体类
 * @author: fengty
 * @create: 2024-10-28 14:12
 */

@Data
public class EmergencyEventInfoVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 告警编码
     */
    private String warningOrderId;

    /**
     * 事件编号 yyyyMMdd+时间毫秒
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件等级，1：特别重大，2：重大，3：较大，4：一般
     */
    private Integer eventGradeDict;

    /**
     * 事发时间
     */
    private String eventTime;

    private String eventTimeStart;

    private String eventTimeEnd;

    /**
     * 事件类型大类
     */
    private Integer eventTypeLargeCategory;

    /**
     * 事件类型中类
     */
    private Integer eventTypeMiddleCategory;

    /**
     * 事件类型小类
     */
    private Integer eventTypeSmallCategory;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区编码
     */
    private String countryCode;

    /**
     * 具体地点
     */
    private String specificLocation;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 事件描述
     */
    private String eventDescribe;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标记，0：正常，1：删除
     */
    private Integer deleteFlag;

    /**
     * 事件状态，1：未处置，2：处置中，3：已完成，4：已归档
     */
    private Integer status;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 图片列表
     */
    private List<EmergencyAttachVO> imgUrlList;
    /**
     * 视频列表
     */
    private List<EmergencyAttachVO> videoUrlList;
    /**
     * 文件列表
     */
    private List<EmergencyAttachVO> fileUrlList;
    /**
     * 音频列表
     */
    private List<EmergencyAttachVO> audioUrlList;

    /**
     * 关键字
     */
    private String keyWord;

    /**
     *  前端查询使用，多选大类
     */
    private List<Integer> largeCategoryList;


    /**
     *  前端查询使用，多选中类
     */
    private List<Integer> middleCategoryList;

    /**
     *  前端查询使用，多选小类
     */
    private List<Integer> smallCategoryList;

    /**
     *  前端查询使用，多选等级
     */
    private List<Integer> eventGradeDictList;
    /**
     *  前端查询使用，多选状态
     */
    private List<Integer> statusList;

    private List<String> eventCodes;

    private String province;
    private String city;
    private String country;

    /**
     *  查询列表使用，查询必填 asc升序 desc倒序
     */
    private String sortType;

    private Integer pageSize;

    private Integer pageNum;
}
