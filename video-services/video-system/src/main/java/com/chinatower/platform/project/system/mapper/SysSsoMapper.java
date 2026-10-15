package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.*;

import java.util.List;
import java.util.Map;

/**
 * 参数配置 数据层
 *
 * @author chinatower
 */
public interface SysSsoMapper
{
    /**
     * 查询参数配置信息
     *
     * @param vo 参数配置信息
     * @return 参数配置信息
     */
    public SysSso selectConfig(SysSso vo);



    /**
     * 查询SSO配置列表
     *
     * @param vo
     * @return 参数配置集合
     */
    public List<SysSso> selectSsoList(SysSso vo);


    /**
     * 查询请求头参数配置表
     *
     * @param ssoId
     * @return 参数配置集合
     */
    public List<SysSsoUser> selectSsoUserList(String ssoId);



    /**
     * 新增参数配置
     *
     * @param vo
     * @return 结果
     */
    public int insertSso(SysSso vo);


    public int insertSsoUser(SysSsoUser vo);



    /**
     * 修改参数配置
     *
     * @param vo
     * @return 结果
     */
    public int updateConfig(SysSso vo);

    /**
     * 删除参数配置
     *
     * @param vo 参数ID
     * @return 结果
     */
    public int deleteSsoById(SysSso vo);

    public int deleteSsoUserById(String ssoId);

    Map getSecretKeyNew(Map map);

    /**
     * 查询vm_sso_user表
     *
     * @param vo
     * @return List<SysSsoUser>
     */
    public List<SysSsoUser> selectSsoUserInfo(SysSsoUser vo);

    int insertTokenConfig(Map map);


    void insertSsoUserBatch(List<SysSsoUser> list);
}
