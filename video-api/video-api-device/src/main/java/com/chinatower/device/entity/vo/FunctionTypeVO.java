package com.chinatower.device.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.platform.entity.device.AlgorithmVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName FunctionTypeVO
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/3
 **/

@Data
public class FunctionTypeVO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 功能大类
     */
    private String functionType;

    /**
     * 功能大类名称
     */
    private String functionTypeName;

    /**
     * 功能中类
     */
    private String subFunctionType;

    /**
     * 功能中类名称
     */
    private String subFunctionTypeName;

    /**
     * 功能小类
     */
    private String functionCode;

    /**
     * 功能小类名称
     */
    private String functionCodeName;

    /**
     * 功能描述
     */
    private String functionDetail;

    /**
     * 图标名称
     */
    private String iconCode;

    /**
     * 图标url
     */
    private String iconUrl;

    /**
     * 图标颜色
     */
    private String iconColour;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 变更时间
     */
    private Date updateDate;

    /**
     * url
     */
    private String url;

    /**
     * 0正常1删除
     */
    private String deleteFlag;

    /**
     * 上下架 1-上架；2-下架
     */
    private String status;

    /**
     * 功能介绍图片
     */
    private String desImageUrl;

    /**
     * 功能介绍视频
     */
    private String desVideoUrl;

    /**
     * 功能标签
     */
    private String functionLabel;

    private List<String> labelList;

    List<String> desImageUrlList;
    List<String> desVideoUrlList;

    private List<AlgorithmVO> algorithmList;
}
