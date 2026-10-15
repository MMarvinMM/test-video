package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 收藏夹实体类
 */
@Data
public class UserCollectionFolderDTO extends BaseDataScopeEntity {

    @ApiModelProperty(value = "收藏夹id")
    private Long id;

    private Long collectFolderId;

    @ApiModelProperty(value = "收藏夹名称")
    private String folderName;

    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    @ApiModelProperty(value = "收藏列表")
    private List<UserDeviceCollectionQueryVO> collList;
}
