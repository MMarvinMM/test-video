package com.chinatower.bdm.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BmAttendanceUserVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nickName;
    private String avatar;
    private String userName;
    private String keeperId;
    private String keeperName;
    private String gridId;
    private String gridName;
    private String industryCode;
    private String tenantId;
    private List<BmAttendanceUserVO> gridList;

}
