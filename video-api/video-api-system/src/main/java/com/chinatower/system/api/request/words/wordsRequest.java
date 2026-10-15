package com.chinatower.system.api.request.words;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @auther liuhuanhuan
 * @date 2022/2/15
 */
@Data
@ApiModel(value = "wordsRequest", description = "常用语请求参数")
public class wordsRequest extends BaseDataScopeEntity {

    /**
     * 常用语编码
     */
    @ApiModelProperty(value = "常用语编码", example = "6656853376329861068", required = false)
    private String wordsId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "309738", required = false)
    private String userId;

    /**
     * 常用语类型
     */
    @ApiModelProperty(value = "常用语类型", example = "1", required = false)
    private String typeId;


    /**
     * 常用语内容
     */
    @ApiModelProperty(value = "常用语内容", example = "同意", required = false)
    private String content;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", example = "2312312")
    private String tenantId;

    /**
     * 应用版本编码
     */
    @ApiModelProperty(value = "应用版本编码", example = "0", required = true)
    private String appVerCode;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2022-01-01", required = true)
    private String createDate;

    /**
     * 常用语编码串
     */
    @ApiModelProperty(value = "常用语编码串", example = "['1','2']")
    private String[] wordsIds;

    /**
     * 常用语列表
     */
    @ApiModelProperty(value = "常用语列表")
    private List<wordsRequest> wordsList;
    /**
     * 初始化常用语标识
     */
    private String initWordsFlag;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 初始化常用语标识列表
     */
    private List<String> initWordsFlagList;

}
