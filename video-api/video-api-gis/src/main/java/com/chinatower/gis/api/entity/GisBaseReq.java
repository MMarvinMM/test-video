package com.chinatower.gis.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GisBaseReq extends CommonFeignEntity {
    /**
     * 对应地图的key
     * @required false
     * */
    @NotBlank(message = Constants.NOT_EMPTY_KEY)
    private String key;
    /**
     * 调用地图类型
     * @required false
     * */
    @ApiModelProperty(value = "调用地图类型")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String mapType;
    /**
     * 行业编码
     * @required false
     * */
    @ApiModelProperty(value = "行业编码")
    private String industryCode;
    /**
     * 返回坐标
     * 0-84；1-火星（02）
     * */
    @ApiModelProperty(value = "返回坐标 0-84；1-火星（02）")
    private Integer coordType = 0;
    /**
     * 入参坐标
     * 0-84；1-火星（02）
     * */
    @ApiModelProperty(value = "入参坐标 0-84；1-火星（02）")
    private Integer paramType = 0;
    /**
     * 分页参数
     */
    @ApiModelProperty(value = "分页参数")
    private Integer pageSize = 10;
    /**
     * 分页参数
     */
    @ApiModelProperty(value = "分页参数")
    private Integer pageNum = 1;
    /**
     * 是否高德加密请求
     */
    public Boolean encryptRequestFlag = false;

    /**
     * 判断是否需要转84坐标
     */
    public Boolean convert84Flag(){
        //返回需要84 入参是高德
        if (this.coordType != null && this.coordType == 0 && this.paramType != null && this.paramType == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否转换高德坐标
     */
    public Boolean convert02Flag(){
        //返回需要84 入参是高德
        if (this.coordType != null && this.coordType == 1 && this.paramType != null && this.paramType == 0) {
            return true;
        }
        return false;
    }
}
