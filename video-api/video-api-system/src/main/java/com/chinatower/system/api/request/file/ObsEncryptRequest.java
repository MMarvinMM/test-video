package com.chinatower.system.api.request.file;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: ObsEncryptRequest
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Data
public class ObsEncryptRequest extends BaseDataScopeEntity implements Serializable {

    /**
    * 原始路径(批量)
    * @required true
    */
    private List<String> fileUrls;

}
