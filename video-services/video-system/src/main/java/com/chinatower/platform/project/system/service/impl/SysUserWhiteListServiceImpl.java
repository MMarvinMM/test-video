package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.constant.RespConstants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.mapper.SysTenantInfoMapper;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.mapper.SysUserWhiteListMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.project.system.service.ISysUserWhiteListService;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.SysUserWhiteListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SysUserWhiteListServiceImpl implements ISysUserWhiteListService {
    @Resource
    private SysUserWhiteListMapper sysUserWhiteListMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysTenantInfoMapper tenantInfoMapper;

    @Autowired
    private ISysConfigService configService;

    private static final String NO_USER = "账户不存在";

    /**
     * 根据登录名获取用户详细信息
     *
     * @param userName
     * @return
     */
    @Override
    public Result<SysUserWhiteListVO> getUser(String userName) {
        SysUserWhiteListVO vo = sysUserWhiteListMapper.getUser(userName);
        if(vo == null){
            return Result.success("");
        }
        if(!StringUtils.isEmpty(vo.getTenantId())){
            vo.setTenantName(sysUserWhiteListMapper.getTenantName(vo.getTenantId()));
        }
        return Result.data(vo);
    }

    /**
     * 新增白名单
     *
     * @param vo
     * @return
     */
    @Override
    public Result add(SysUserWhiteListVO vo) {
        if(vo == null || StringUtils.isEmpty(vo.getUserName())){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        if(sysUserWhiteListMapper.checkUserId(vo.getUserName()) > 0){
            return Result.fail(RespConstants.HAS_USER);
        }
        SysUser sysUser = userMapper.selectUserOnlyByUserName(vo.getUserName());
        vo.setSource(sysUser.getUserType().equalsIgnoreCase("0")?"0":"1");
        vo.setTenantId(sysUser.getTenantId());
        vo.setPhoneNumber(sysUser.getPhonenumber());
        vo.setNickName(sysUser.getNickName());
        if(sysUser.getTenantId() != null){
            SysTenantInfo sysTenantInfo = tenantInfoMapper.selectByTenantId(sysUser.getTenantId());
            vo.setTenantName(sysTenantInfo != null?sysTenantInfo.getTenantName():"");
        }
        return Result.condition(sysUserWhiteListMapper.add(vo) > 0);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @Override
    public Result delete(List<String> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return Result.condition(sysUserWhiteListMapper.delete(ids,userId) > 0);
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @Override
    public List<SysUserWhiteListVO> list(SysUserWhiteListVO vo) {
        return sysUserWhiteListMapper.list(vo);
    }

    /**
     * 导入
     *
     * @param params
     * @return
     */
    @Override
    public List<SysImportDetail> importData(ImportParams<SysUserWhiteListVO> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        SysUser loginUser = MapUtil.get(params.getExtraParams(),"loginUser", SysUser.class);

        for(SysUserWhiteListVO sysUserWhiteListVO : params.getList()){
            SysImportDetail detail = new SysImportDetail(params.getImportId(),rowNum);
            try{
                if(sysUserWhiteListVO == null || StringUtils.isEmpty(sysUserWhiteListVO.getUserName())){
                    detail.setFailReason(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
                    continue;
                }

                Result<SysUserWhiteListVO> result = getUser(sysUserWhiteListVO.getUserName());
                if (result.getData() == null) {
                    SysUserWhiteListVO vo = new SysUserWhiteListVO();
                    vo.setCreateBy(loginUser.getUserId());
                    vo.setUserName(sysUserWhiteListVO.getUserName());
                    Result r = add(vo);
                    if (r.getCode() == ResultCode.SUCCESS.getCode()) {
                        detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);
                    }else{
                        detail.setFailReason(r.getMsg());
                    }
                }else{
                    detail.setFailReason("账号["+sysUserWhiteListVO.getUserName()+"]已经被添加到白名单");
                }
            }catch (Exception e) {
                detail.setFailReason("处理异常："+ ExceptionUtil.stacktraceToString(e,1000));
                log.error(e.getMessage(), e);
            }finally {
                rowNum++;
                details.add(detail);
            }
        }

        return details;
    }

    /**
     * 获取用户账号
     *
     * @param
     * @return
     */
    @Override
    public Result<List<String>> getUserList() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<String> tenantIdlist = sysUserWhiteListMapper.getTenantId(user.getPlatformCode());
        List<String> list = sysUserWhiteListMapper.getUserList(tenantIdlist);
        return Result.data(list);
    }



}
