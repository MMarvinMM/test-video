package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 二次识别配置参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogBackParentVO extends BaseDataScopeEntity implements Serializable {

    private List<SecondRecogBackVO> data;

    private String tenantId;

    private String sign;

    private String timestamp;
}
