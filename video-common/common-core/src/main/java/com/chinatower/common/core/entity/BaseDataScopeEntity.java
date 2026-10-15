package com.chinatower.common.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 业务查询数据权限基类
 * @Author guyuefei
 * @Date 2022/7/15
 * @Version release_v1.5
 **/
@Data
public class BaseDataScopeEntity extends BaseIndustryEntity {
    /** 数据权限 */
    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataScope;

    /** 数据权限mongo条件 */
    @TableField(exist = false)
    @ApiModelProperty(value = "数据权限mongo条件",hidden = true)
    private List<Criteria> criteriaList;
    /**
     * 大屏id
     */
    @ApiModelProperty("大屏id")
    private String screenId;
    /**
     * 大屏类型
     */
    @ApiModelProperty("大屏类型")
    private String screenType;

    /** 数据权限 */
    @ApiModelProperty(value = "数据权限",hidden = true)
    private String dataPermissions;

    /** 国产化标识 0-否、1-是 */
    @ApiModelProperty("国产化标识")
    private String localizationFlag;
}
