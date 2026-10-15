package com.chinatower.manage.repository;


import com.chinatower.common.entity.Result;
import com.chinatower.manage.dto.open.OpenIntfWholeInfoModel;
import com.chinatower.manage.dto.open.OpenManageInfoModel;


/**
 * @author lxf
 * @version 1.0
 * @date 2022年6月30日19:30:26
 */
public interface IOpenIntfMangeService {






    /**
     * 根据条件查询API接口管理列表
     *
     * @param model
     * @return
     */
    Result<Object> queryOpenPlatformInfo(OpenManageInfoModel model);



    /**
     * 查询API接口全部信息
     *
     * @param openIntfId
     * @return
     */
    Result<OpenIntfWholeInfoModel> queryOpenIntfWholeInfoByApiIntfId(Long openIntfId, String type);
}
