package com.chinatower.platform.project.system.domain;

import com.chinatower.platform.model.resp.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppTokenResponse extends LanXinBaseResponse<AppTokenData>  {

    AppTokenData data;


}