package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.project.system.domain.SysSso;
import com.chinatower.platform.project.system.domain.SysSsoUser;
import com.chinatower.platform.project.system.mapper.SysSsoMapper;
import com.chinatower.platform.project.system.service.ISysSsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author chinatower
 */
@Service
public class SysSsoServiceImpl implements ISysSsoService
{
    @Autowired
    private SysSsoMapper ssoMapper;


    /**
     * 查询API配置信息
     *
     * @param vo
     * @return 参数配置信息
     */
    @Override
    public SysSso selectConfigById(SysSso vo)
    {
        SysSso sysSso = ssoMapper.selectConfig(vo);

        String ssoId = vo.getSsoId();
        //获取请求头参数配置表
        List<SysSsoUser> ssoUserList = ssoMapper.selectSsoUserList(ssoId);
        sysSso.setSysSsoUser(ssoUserList);


        return sysSso;
    }




    /**
     * 查询参数配置列表
     *
     * @param vo 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysSso> selectSsoList(SysSso vo)
    {
        List<SysSso> apiList = ssoMapper.selectSsoList(vo);
        for (SysSso sysSso : apiList) {
            sysSso.setAppkey(AESUtil.encryptBase64(sysSso.getAppkey()));
            sysSso.setSecretkey(AESUtil.encryptBase64(sysSso.getSecretkey()));
        }

        return apiList;
    }

    /**
     * 新增参数配置
     *
     * @param vo
     * @return 结果
     */
    @Override
    @Transactional
    public int insertConfig(SysSso vo) {
        vo.setCreateTime(new Date());
        String ssoId = IdUtil.simpleUUID();
        //插入请求头参数配置表
        List<SysSsoUser> userList = vo.getSysSsoUser();
        for(SysSsoUser sysSsoUser:userList) {
            sysSsoUser.setSsoId(ssoId);
            sysSsoUser.setCreateTime(new Date());
        }
        ssoMapper.insertSsoUserBatch(userList);

        vo.setSsoId(ssoId);
        vo.setAppkey(AESUtil.decryptBase64(vo.getAppkey()));
        vo.setSecretkey(AESUtil.decryptBase64(vo.getSecretkey()));
        return ssoMapper.insertSso(vo);
    }

    /**
     * 修改参数配置
     *
     * @param vo
     * @return 结果
     */
    @Override
    @Transactional
    public int updateConfig(SysSso vo) {
        vo.setUpdateTime(new Date());
        String ssoId = vo.getSsoId();
        //先删除子表数据
        ssoMapper.deleteSsoUserById(ssoId);
        //在写入新的子表数据

        //插入请求头参数配置表
        List<SysSsoUser> userList = vo.getSysSsoUser();
        for(SysSsoUser sysSsoUser:userList) {
            sysSsoUser.setSsoId(ssoId);
            sysSsoUser.setCreateTime(new Date());
//            ssoMapper.insertSsoUser(sysSsoUser);
        }
        ssoMapper.insertSsoUserBatch(userList);

        return ssoMapper.updateConfig(vo);
    }

    /**
     * 删除参数配置信息
     *
     * @param vo
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteConfigById(SysSso vo) {
        String ssoId = vo.getSsoId();
        ssoMapper.deleteSsoUserById(ssoId);
        int res = ssoMapper.deleteSsoById(vo);
        return res;
    }


}
