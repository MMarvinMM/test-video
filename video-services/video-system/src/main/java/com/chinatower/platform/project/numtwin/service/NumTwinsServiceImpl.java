package com.chinatower.platform.project.numtwin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.numtwin.entity.NumTwinsConfigDto;
import com.chinatower.platform.project.numtwin.entity.NumTwinsPriorityDto;
import com.chinatower.platform.project.numtwin.entity.NumTwinsUser;
import com.chinatower.platform.project.numtwin.entity.NumTwinsUserResp;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.NumTwinsSaveDto;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.NumTwinsUserDto;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OfflinUserListReq;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OnlineUserListDto;
import com.chinatower.platform.project.numtwin.mapper.NumTwinsMapper;
import com.chinatower.platform.project.numtwin.util.NumTwinsOkhttp;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysRoleService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chinatower.common.entity.Result;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:39
 */

@Slf4j
@Service
public class NumTwinsServiceImpl implements NumTwinsService{


    @Autowired
    private NumTwinsOkhttp numTwinsOkhttp;

    @Autowired
    private NumTwinsMapper numTwinsMapper;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 封装调用数字孪生保存接口入参对象
     * @param req
     * @param user
     * @return
     */
    public NumTwinsSaveDto getNumTwinsDto(NumTwinsConfigDto req,SysUser user){
        NumTwinsSaveDto numTwinsSaveDto = new NumTwinsSaveDto();
        numTwinsSaveDto.setDigitalTwinAddress(req.getNumTwinsAddress());
        numTwinsSaveDto.setDigitalTwinName(req.getScreenName());
        numTwinsSaveDto.setConcurrencyVolume(req.getConcurrency());
        if(Constants.ONE.equals(req.getEngineType())){
            numTwinsSaveDto.setRenderingEngine("UE");
        }else{
            numTwinsSaveDto.setRenderingEngine(req.getEngineType());
        }

        numTwinsSaveDto.setSignalingAddress(req.getSignalAddress());
        numTwinsSaveDto.setUniqueIdentifier(req.getScreenId());
        numTwinsSaveDto.setProvinceName(req.getProvinceName());
        numTwinsSaveDto.setCityName(req.getCityName());
        numTwinsSaveDto.setDistrictName(req.getCountyName());
        List<NumTwinsUserDto> digitalUserSettingsBOList = new ArrayList<>();
        if(!StringUtils.isEmpty(req.getEngineType()) && Constants.ONE.equals(req.getEngineType())){
            for (NumTwinsPriorityDto numTwinsPriorityDto : req.getPriorityDtoList()) {
                NumTwinsUserDto dto = new NumTwinsUserDto();
                dto.setUserAccount(numTwinsPriorityDto.getUserName());
                dto.setUserPriority(numTwinsPriorityDto.getLevel());
                dto.setUserName(numTwinsPriorityDto.getNickName());
                dto.setCustomerId(user.getTenantId());
                digitalUserSettingsBOList.add(dto);
            }
            numTwinsSaveDto.setDigitalUserSettingsBOList(digitalUserSettingsBOList);
        }
        return numTwinsSaveDto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result addOrUpdate(NumTwinsConfigDto req) {
        log.info("NumTwinsServiceImpl-addOrUpdate-req:{}", JSON.toJSONString(req));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if(req.getId() == null){
            //新增前查询该租户行业是否有已配置数据
            req.setTenantId(user.getTenantId());
            req.setIndustryCode(user.getIndustryCode());
            NumTwinsConfigDto configDto = numTwinsMapper.getInfo(req);
            if(configDto != null){
                return Result.fail("当前租户已配置该行业数字大屏！");
            }
        }
        NumTwinsSaveDto numTwinsSaveDto = this.getNumTwinsDto(req,user);
        if(!Constants.ONE.equals(req.getEngineType())){
            //不是UE，直接入库
            if(req.getId() != null){
                //修改
                req.setUpdateUser(user.getUserName());
                req.setUpdateTime(new Date());
                numTwinsMapper.update(req);
                log.info("addOrUpdate-大屏配置修改成功");
            }else{
                //新增
                req.setCreateUser(user.getUserName());
                req.setUpdateUser(user.getUserName());
                req.setCreateTime(new Date());
                req.setUpdateTime(new Date());
                req.setDeleteFlag(Constants.ZERO);
                req.setTenantId(user.getTenantId());
                req.setIndustryCode(user.getIndustryCode());
                numTwinsMapper.save(req);
                log.info("addOrUpdate-大屏配置新增成功");
            }
            return Result.success("新增成功");
        }
        //封装调用数字孪生接口参数调用数字孪生(待联调结束放开)
        NetConnectUtils.RequestParamter<NumTwinsSaveDto> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(numTwinsSaveDto);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.NUM_TWINS_ADD_CONFIG);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = numTwinsOkhttp.doPostGateWay(paramterEdit, loginUser.getUser().getPlatformCode(), loginUser.getUser().getPlatformCode());
        log.info("addOrUpdate-resultEntity-resp:{}",JSON.toJSONString(resultEntity));
        if(resultEntity !=null && "0000".equals(resultEntity.getReturnCode())){
            if(!"200".equals(resultEntity.getCode())){
                return Result.fail(resultEntity.getMessage());
            }
            //新增、修改标识（false 为修改，update为新增）
            boolean saveOrUpdateFlage = false;
            if(req.getId() != null){
                //修改
                req.setUpdateUser(user.getUserName());
                req.setUpdateTime(new Date());
                numTwinsMapper.update(req);
                log.info("addOrUpdate-大屏配置修改成功");
            }else{
                saveOrUpdateFlage = true;
                //新增
                req.setCreateUser(user.getUserName());
                req.setUpdateUser(user.getUserName());
                req.setCreateTime(new Date());
                req.setUpdateTime(new Date());
                req.setDeleteFlag(Constants.ZERO);
                req.setTenantId(user.getTenantId());
                req.setIndustryCode(user.getIndustryCode());
                numTwinsMapper.save(req);
                log.info("addOrUpdate-大屏配置新增成功");
            }
            //UE引擎需要配置用户优先级
            log.info("NumTwinsServiceImpl-addOrUpdate-开始配置用户优先级:{}",JSON.toJSONString(req));
            if(!StringUtils.isEmpty(req.getEngineType()) && Constants.ONE.equals(req.getEngineType())){
                log.info("NumTwinsServiceImpl-开始配置用户优先级:{}",JSON.toJSONString(req));
                if(!CollectionUtils.isEmpty(req.getPriorityDtoList()) || saveOrUpdateFlage){
                    for (NumTwinsPriorityDto dto:req.getPriorityDtoList()) {
                        dto.setConfigId(req.getId());
                        dto.setCreateUser(user.getUserName());
                        dto.setUpdateUser(user.getUserName());
                    }
                    if(saveOrUpdateFlage){
                        //新增时后端默认给租户下所有用户优先级设为一级插入数据库，再将前端传入的自定义设置的优先级进行数据库修改
                        //查询租户下所有的用户id
                        List<NumTwinsPriorityDto> priorityDtoList = numTwinsMapper.getAllUserId(user.getTenantId(),user.getIndustryCode());
                        log.info("addOrUpdate-查询租户下所有的用户id{}",priorityDtoList.size());
                        if(!CollectionUtils.isEmpty(priorityDtoList)){
                            for (NumTwinsPriorityDto dto:priorityDtoList) {
                                dto.setConfigId(req.getId());
                                dto.setLevel(Constants.ONE);
                                dto.setCreateUser(user.getUserName());
                            }
                            numTwinsMapper.saveList(priorityDtoList);
                        }
                        //如果是新增，前端不会传配置id字段，修改时候需要自己塞进去
                        if(!CollectionUtils.isEmpty(req.getPriorityDtoList())){
                            for (NumTwinsPriorityDto dto:req.getPriorityDtoList()) {
                                dto.setConfigId(req.getId());
                            }
                        }
                    }
                    log.info("NumTwinsServiceImpl-开始修改用户优先级:{}",JSON.toJSONString(req));
                    if(!CollectionUtils.isEmpty(req.getPriorityDtoList())){
                        //修改的时候将传入的人员删除，再新增
                        if(!saveOrUpdateFlage){
                            //修改
                            List<String> userIds = req.getPriorityDtoList().stream().map(NumTwinsPriorityDto::getUserId).collect(Collectors.toList());
                            numTwinsMapper.deleteUserList(userIds,String.valueOf(req.getId()));
                            for (NumTwinsPriorityDto dto:req.getPriorityDtoList()) {
                                dto.setConfigId(req.getId());
                                dto.setUpdateUser(user.getUserName());
                                dto.setCreateUser(user.getUserName());
                            }
                            numTwinsMapper.saveList(req.getPriorityDtoList());
                        }else{
                            //批量修改优先级
                            numTwinsMapper.updateList(req.getPriorityDtoList(),user.getUserName());
                        }
                    }
                }
            }
            return Result.success("新增成功");
        }else{
            return Result.fail("新增失败");
        }



    }

    @Override
    public Result getInfo(NumTwinsConfigDto req) {
        return Result.data(numTwinsMapper.getInfo(req));
    }

    @Override
    public Result getUserLevelList(NumTwinsUser req) {
        log.info("NumTwinsServiceImpl-getUserLevelList-req:{}",JSON.toJSONString(req));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        req.setTenantId(sysUser.getTenantId());
        req.setIndustryCode(sysUser.getIndustryCode());
        List<SysRole> roleList = roleService.selectRoleByTenantId(sysUser.getTenantId());
        List<SysDept> depts = numTwinsMapper.selectDeptList(sysUser.getTenantId(),sysUser.getIndustryCode());
        //log.info("NumTwinsServiceImpl-getUserLevelList-depts:{}",JSON.toJSONString(depts));
        NumTwinsUserResp resp = new NumTwinsUserResp();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<NumTwinsUser> userList = numTwinsMapper.getNumTwinsUserList(req);

        Page<NumTwinsUser> page = (Page<NumTwinsUser>) userList;
        log.info("NumTwinsServiceImpl-getUserLevelList-page:{}",JSON.toJSONString(page));
        for (NumTwinsUser user:userList) {
            if(!StringUtils.isEmpty(user.getRoleId())){
                String roleName = "";
                String[] roles = user.getRoleId().split(Constants.COMMA);
                //获取角色名
                for (String role:roles) {
                    for (SysRole sysRole:roleList) {
                        if(role.equals(String.valueOf(sysRole.getRoleId()))){
                            if(StringUtils.isEmpty(roleName)){
                                roleName = sysRole.getRoleName();
                            }else{
                                roleName += Constants.COMMA+sysRole.getRoleName();
                            }
                        }
                    }
                }
                user.setRoleName(roleName);
            }
            if(!StringUtils.isEmpty(user.getDeptId())){
                String deptName = "";
                String[] deptList = user.getDeptId().split(Constants.COMMA);
                //获取组织名
                for (String deptDto:deptList) {
                    for (SysDept sysDept:depts) {
                        if(deptDto.equals(sysDept.getDeptId())){
                            if(StringUtils.isEmpty(deptName)){
                                deptName = sysDept.getDeptName();
                            }else{
                                deptName += Constants.COMMA+sysDept.getDeptName();
                            }
                        }
                    }
                }
                user.setDeptName(deptName);
            }

        }
        resp.setList(userList);
        resp.setTotal(page.getTotal());
        log.info("NumTwinsServiceImpl-getUserLevelList-resp:{}",JSON.toJSONString(resp));
        return Result.data(resp);
    }

    @Override
    public Result offLineUserList(OfflinUserListReq req) {
        log.info("NumTwinsServiceImpl-offLineUserList-req:{}",JSON.toJSONString(req));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        //封装调用数字孪生接口参数调用数字孪生(待联调结束放开)
        NetConnectUtils.RequestParamter<OfflinUserListReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(req);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.OFFLINE_USER);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = numTwinsOkhttp.doPostGateWay(paramterEdit, user.getPlatformCode(), user.getPlatformCode());
        log.info("NumTwinsServiceImpl-offLineUserList-resp:{}",JSON.toJSONString(resultEntity));
        return  Result.data("下线成功");
    }

    @Override
    public Result getOnlineUserList(OnlineUserListDto req) {

        log.info("NumTwinsServiceImpl-getOnlineUserList-req:{}",JSON.toJSONString(req));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
       /* req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());*/
        //封装调用数字孪生接口参数调用数字孪生(待联调结束放开)
        NetConnectUtils.RequestParamter<OnlineUserListDto> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(req);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.LOGIN_INFO);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = numTwinsOkhttp.doPostGateWay(paramterEdit, loginUser.getUser().getPlatformCode(), loginUser.getUser().getPlatformCode());
        log.info("NumTwinsServiceImpl-offLineUserList-resp:{}",JSON.toJSONString(resultEntity));
        return Result.data(resultEntity);
    }
}
