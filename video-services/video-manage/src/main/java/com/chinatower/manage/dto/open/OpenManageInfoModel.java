package com.chinatower.manage.dto.open;

import com.chinatower.manage.dto.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author tianhao
 */
@Data
public class OpenManageInfoModel extends BaseModel implements Serializable {

    //接口编码
    private String openIntfCode;

    //开放平台接口id
    private String openIntfId;

    //开放平台接口名称
    private String openIntfName;

    //开放平台接口类型
    private String openIntfType;

    //是否打开
    private String isOpen;
    //开放权限
    private String openPrivilege;

    //调用路径
    private String callRoute;

    //创建人
    private String createStaff;

    //创建时间
    private Date createTime;

    //修改人
    private String updateStaff;

    //修改时间
    private Date updateTime;

    //接口信息
    private OpenIntfInfoModel openIntfInfoModel;

    //接口请求参数信息
    private List<OpenIntfReqInfoModel> openIntfReqInfoModels;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeEnd;


    // 1：api 2:sdk
    private String type;
}
