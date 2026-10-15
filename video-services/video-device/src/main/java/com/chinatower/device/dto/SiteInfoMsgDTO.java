package com.chinatower.device.dto;

import com.chinatower.common.core.dto.SiteInfoDto;
import lombok.Data;

import java.util.List;

/**
 * Created by zheng.yan on 2022/1/18.
 */
@Data
public class SiteInfoMsgDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;

    private List<SiteInfoDto> data;

}
