package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PlatformInfoVo implements Serializable {


    private String platformCode;

    private String platformName;

    private String keyWord;

    private String platformParentCode;

    private List<PlatformInfoVo> parentPlatform;

}
