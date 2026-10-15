package com.chinatower.receive.vo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Name DeviceInfoReq
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/26 16 : 17
 */
@Data
@Accessors(chain = true)
public class DeviceInfoReq {

    private String dataScope;

    private String modelId;

    private List<String> deviceCodes;
}
