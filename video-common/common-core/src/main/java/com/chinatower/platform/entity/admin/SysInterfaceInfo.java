package com.chinatower.platform.entity.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="sys_interface_info", autoResultMap = true)
@Data
public class SysInterfaceInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String url;

    private String urlDesc;

    private String operateTypeDesc;

    /**
     * 参数解释
     */
//    @TableField(typeHandler = JacksonTypeHandler.class)
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray paramsExplain;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(serialize = true, deserialize = false)
    private Date createTime;

    /**
     * 修改时间
     * */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JSONField(serialize = true, deserialize = false)
    private Date updateTime;

    private Integer deleteFlag;

    private String createBy;

    private String updateBy;
}
