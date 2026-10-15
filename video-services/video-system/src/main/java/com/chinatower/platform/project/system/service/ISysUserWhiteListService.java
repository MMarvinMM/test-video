package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.system.api.model.SysUserWhiteListVO;

import java.util.List;

public interface ISysUserWhiteListService {

    /**
     * 根据登录名获取用户详细信息
     * @param userName
     * @return
     */
    Result<SysUserWhiteListVO> getUser(String userName);

    /**
     * 新增白名单
     * @param vo
     * @return
     */
    Result add(SysUserWhiteListVO vo);

    /**
     * 删除
     * @param ids
     * @return
     */
    Result delete(List<String> ids);

    /**
     * 列表
     * @param vo
     * @return
     */
    List<SysUserWhiteListVO> list(SysUserWhiteListVO vo);

    /**
     * 导入
     * @param params
     * @return
     */
    List<SysImportDetail> importData(ImportParams<SysUserWhiteListVO> params);


    /**
     * 获取用户账号
     * @param
     * @return
     */
    Result<List<String>> getUserList();
}
