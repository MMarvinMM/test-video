package com.chinatower.platform.project.system.domain.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

@Data
public class UserQueryDTO extends BaseDataScopeEntity {

    private String userName;

    private String userId;

    private List<String> deptIds;
}
