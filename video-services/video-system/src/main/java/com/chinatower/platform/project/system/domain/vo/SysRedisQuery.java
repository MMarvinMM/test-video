package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class SysRedisQuery extends PageDTO {

    private Date createTimeStart;

    private Date createTimeEnd;

    private String createName;

    private String resultType;

}
