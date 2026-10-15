package com.chinatower.platform.project.system.service.impl;

import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.domain.WhiteIp;
import com.chinatower.platform.project.system.domain.vo.WhiteIpVO;
import com.chinatower.platform.project.system.mapper.WhiteIpMapper;
import com.chinatower.platform.project.system.service.IWhiteIpService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WhiteIpServiceImpl
 * @description: 用户登录IP白名单接口实现
 * @author: fengty
 * @create: 2024-04-29 10:41
 */

@Slf4j
@Service
public class WhiteIpServiceImpl implements IWhiteIpService {

    @Resource
    WhiteIpMapper whiteIpMapper;

    @Override
    public Integer saveUserIps(List<WhiteIp> whiteIpList, SysUser user) throws Exception {
        try {
            whiteIpList.forEach( ip -> ip.setCreator(user.getUserName()));
            Integer insertCount = whiteIpMapper.insertIps(whiteIpList);
            return insertCount;
        } catch (Exception e) {
            log.error("批量新增IP白名单", e);
            throw e;
        }
    }

    @Override
    public Integer logicDeleteIps(List<WhiteIp> whiteIpList, SysUser user) throws Exception {
        try {
            whiteIpList.forEach( whiteIp -> whiteIp.setModifier(user.getUserName()));
            return whiteIpMapper.updateIps(whiteIpList);
        } catch (Exception e) {
            log.error("批量逻辑删除白名单", e.getMessage());
            throw e;
        }
    }
    @Override
    public Integer updateIps(List<WhiteIp> whiteIpList, SysUser user) throws Exception {
        try {
            whiteIpList.forEach( whiteIp -> whiteIp.setModifier(user.getUserName()));
            return whiteIpMapper.updateIps(whiteIpList);
        } catch (Exception e) {
            log.error("批量修改白名单", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void opreatorWhiteIps(WhiteIpVO whiteIpVO) throws Exception {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser sysUser = loginUser.getUser();

            if(!CollectionUtils.isEmpty(whiteIpVO.getInsertWhiteIps())) {
                saveUserIps(whiteIpVO.getInsertWhiteIps(), sysUser);
            }

            if(!CollectionUtils.isEmpty(whiteIpVO.getUpdateWhiteIps())) {
                updateIps(whiteIpVO.getUpdateWhiteIps(), sysUser);
            }

            if(!CollectionUtils.isEmpty(whiteIpVO.getDeleteWhiteIps())) {
                logicDeleteIps(whiteIpVO.getDeleteWhiteIps(), sysUser);
            }
        } catch (Exception e) {
            throw new Exception(Constants.OPREATOR_ERROR);
        }
    }

    @Override
    public Page<WhiteIp> pageWhiteIps(WhiteIp whiteIp) {
        PageHelper.startPage(whiteIp.getPageNum(), whiteIp.getPageSize());
        return whiteIpMapper.selectIps(whiteIp);
    }
}
