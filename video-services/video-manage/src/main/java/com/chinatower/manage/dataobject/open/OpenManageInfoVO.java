package com.chinatower.manage.dataobject.open;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * tianhao
 */
@Data
public class OpenManageInfoVO {

    //接口编码
    private String openIntfCode;

    //开放平台接口id
    private String openIntfId;

    //开放平台接口名称
    private String openIntfName;

    //调用路径
    private String callRoute;

    //创建人
    private String creator;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    //修改人
    private String modifier;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
