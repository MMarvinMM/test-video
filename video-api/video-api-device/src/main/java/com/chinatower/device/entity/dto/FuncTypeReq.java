package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class FuncTypeReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 1-新增；2-修改； 3-删除
     */
    private int optType;
    /**
     * 决定功能是否允许订购；1-上架；2-下架；
     */
    private int status;

    /**
     * 功能大类
     */
    String funcTypeCode;

    /**
     * 功能大类名称
     */
    String funcTypeName;

    /**
     * 功能中类
     */
    String funcSubTypeCode;

    /**
     * 功能中类名称
     */
    String funcSubTypeName;

    /**
     * 功能小类
     */
    String funcMinTypeCode;

    /**
     * 功能小类名称
     */
    String funcMinTypeName;

    /**
     * 算法小类编码列表
     */
    List<AlgoMinTypeReq> algoMinTypeCodeList;

    /**
     * 功能图标URL
     */
    String logoUrl;

    /**
     * Logo图标名称
     */
    String logoImageName;

    /**
     * 功能描述
     */
    String functionDiscribtion;

    /**
     * 非算法类功能链接
     */
    String unAlgoFunctionUrl;

    /**
     * 功能介绍图片
     */
    List<FuncImageReq> desImageUrlList;

    /**
     * 功能介绍视频
     */
    List<FuncVideoReq> desVideoUrlList;
}
