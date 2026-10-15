package com.chinatower.auth.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class SwitchLoginFeignVo extends BaseDataScopeEntity {


    @NotBlank(message = "uuid不能为空!")
    private String uuid;

    private String token;

    private String json;

    private String refreshToken;

    private String extend;

    private List tradeVersions = new ArrayList();
}
