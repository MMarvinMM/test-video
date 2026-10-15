package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.SysSso;

import java.util.List;

/**
 * 参数配置 服务层
 *
 * @author iwhalecloud
 */
public interface ISysSsoService
{
    /**
     * 查询参数配置信息
     *
     * @param vo 参数配置ID
     * @return 参数配置信息
     */
    public SysSso selectConfigById(SysSso vo);



    /**
     * 查询API配置列表
     *
     * @param vo API配置信息
     * @return API配置集合
     */
    public List<SysSso> selectSsoList(SysSso vo);

    /**
     * 新增参数配置
     *
     * @param vo
     * @return 结果
     */
    public int insertConfig(SysSso vo);

    /**
     * 修改参数配置
     *
     * @param vo
     * @return 结果
     */
    public int updateConfig(SysSso vo);

    /**
     * 删除参数配置信息
     *
     * @param vo 参数ID
     * @return 结果
     */
    public int deleteConfigById(SysSso vo);




}
