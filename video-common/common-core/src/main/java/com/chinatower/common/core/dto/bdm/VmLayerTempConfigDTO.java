package com.chinatower.common.core.dto.bdm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lyy
 * @version v1.6.60
 * @date 2024/04/29
 */
@Data
@ApiModel(description = "图层模板配置查询入参")
public class VmLayerTempConfigDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /**
     *  主键ID
     */
    private String id;

    /**
     * 图层模板名称
     */
    private String layerTempName;

    /**
     * 告警来源
     */
    private String warningSource;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 图层模板类别（0-默认初始化、1-自定义）
     */
    private Integer category;

    /**
     * 简称（用作外键）
     */
    private String abbreviation;

    /**
     * 父类类型名称
     */
    private String superTypeName;

    /**
     * 祖类类型名称
     */
    private String initialTypeName;

    /**
     * 业务面积字段名
     */
    private String areaRef;

    /**
     * 属性显示名称
     */
    private String tbbhRef;

    /**
     * 图层模版属性配置列表
     */
    private List<AddOrModifyLayerTypeFieldsVO> layerTempFields;

    /**
     * 需要删除的字段ID数据
     */
    private List<String> fieldIds = new ArrayList<>();

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeEnd;
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeStart;
    /** 修改结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeEnd;
    /** 修改开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeStart;

    /** sql排序 */
    private String sort;
    private String sortName;

    /**
     * 分页参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;
}
