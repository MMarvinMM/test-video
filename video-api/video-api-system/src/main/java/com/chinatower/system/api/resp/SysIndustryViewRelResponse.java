package com.chinatower.system.api.resp;

import com.chinatower.platform.model.resp.BaseResponse;
import lombok.Data;

import java.util.Date;

/**
 * 行业大屏关联模型
 *
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 16:29
 */
@Data
public class SysIndustryViewRelResponse extends BaseResponse {

    private static final long serialVersionUID = 6234806051815720069L;
    /**
     * 主键
     */
    private Long industryViewId;
    /**
     * 行业配置ID
     */
    private Long industryId;
    /**
     * 视图大屏ID集合
     */
    private String viewList;
    /**
     * 版本号
     */
    private String appVerCode;
    /**
     * 应用版本描述
     */
    private String appVerDesc;
    /**
     * 应用图标地址
     */
    private String imgUrl;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 修改人
     */
    private String modifier;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 1上架 0下架
     */
    private String status;
    /**
     * 1生效 0失效
     */
    private String deleteFlag;
    /**
     * 菜单ID
     */
    private String menuId;
    /**
     * 行业名称
     */
    private String industryName;
    /**
     * 行业CODE
     */
    private String industryCode;
    /**
     * 版本号
     */
    private String appVerName;


    /**
     * 查询开启分页：true-开启，false-关闭
     */
    private boolean enablePage;
}
