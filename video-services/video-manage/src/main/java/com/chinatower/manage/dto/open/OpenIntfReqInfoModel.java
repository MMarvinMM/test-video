package com.chinatower.manage.dto.open;

import com.chinatower.manage.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tianhao
 */
@Data
public class OpenIntfReqInfoModel extends BaseModel implements Serializable {

    //sdk接口id
    private String sdkIntfId;

    //字段描述/参数描述
    private String fieldDesc;

    //字段名称/参数名称
    private String fieldName;

    //类型长度
    private String fieldType;

    //字段说明
    private String fieldRemark;

    //内容示例
    private String contentExample;

    //是否必填
    private String required;

    //排序
    private Integer sort;

    //类型: 1:请求头参数，2：请求路径入参，3：请求参数，4：请求内容，5：响应内容
    private String type;
}
