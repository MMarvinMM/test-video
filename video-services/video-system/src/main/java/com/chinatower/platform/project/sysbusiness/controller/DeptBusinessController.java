package com.chinatower.platform.project.sysbusiness.controller;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.custom.entity.OrganDeviceVO;
import com.chinatower.platform.project.sysbusiness.entity.LayerUrlVo;
import com.chinatower.system.api.model.AuthObjDataNewVO;
import com.chinatower.system.api.model.SysAreaVO;
import com.chinatower.platform.project.sysbusiness.service.IDeptBusinessService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.AuthObjDataVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * video-system
 * 系统管理 组织相关业务
 * @author zhaol
 * @since 2022-02-14 12:03:37
 */
@RestController
@RequestMapping("/system/business")
public class DeptBusinessController extends TowerBaseController {

    @Autowired
    private IDeptBusinessService deptBusinessService;

    /**
     * 组织设备关系保存
     */
    @PostMapping(value = "/saveDeptDeviceRef")
    public Result saveDeptDeviceRef(@RequestBody OrganDeviceVO organDeviceVO){
        return deptBusinessService.saveDeptDeviceRef(organDeviceVO);
    }


    /**
     * 租户设备树查询
     */
    @PostMapping(value = "/getDeviceTree")
    public Result getDeviceTree(@RequestBody OrganDeviceVO organDeviceVO){
        return deptBusinessService.getDeviceTree(organDeviceVO);
    }


    /**
     * 组织设备关系集合查询
     */
    @PostMapping(value = "/getDeptDeviceRefList")
    public Result getDeptDeviceRefList(@RequestBody OrganDeviceVO organDeviceVO){
        return deptBusinessService.getDeptDeviceRefList(organDeviceVO);
    }


    /**
     * 判断人员是否在流程处置中
     */
    @GetMapping("/checkProcessByUser/{userIds}")
    public Result checkProcessByUser(@PathVariable String[] userIds){
        return deptBusinessService.checkProcessByUser(userIds);
    }

    /**
     * 判断人员是否在流程处置中
     */
    @PostMapping("/getLayerUrl")
    public Result<List<LayerUrlVo>> getLayerUrl(){
        return deptBusinessService.getLayerUrl();
    }



    /**
     * 获取切面数据接口
     */
    @PostMapping("/getDataScopeObj")
    public Result<AuthObjDataVO> getDataScopeObj(){
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        return Result.data(deptBusinessService.getDataScopeObj(sysUser));
    }

    /**
     * 获取切面数据接口（只返回areaList）
     */
    @PostMapping("/getDataScopeObjNew")
    public Result<AuthObjDataNewVO> getDataScopeObjNew(){
        AuthObjDataNewVO result = new AuthObjDataNewVO();
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        AuthObjDataVO dataScopeObj = deptBusinessService.getDataScopeObj(sysUser);
        result.setAreaList(dataScopeObj.getAreaList());
        return Result.data(result);
    }

    /**
     * 获取登录用户切面数据接口
     */
    @PostMapping("/getDataScopeObjBySysUser")
    public Result<AuthObjDataVO> getDataScopeObjBySysUser(@RequestBody SysUser sysUser){
        logger.info("接收到参数："+ JSON.toJSONString(sysUser));
        return Result.data(deptBusinessService.getDataScopeObj(sysUser.getTenantId(),sysUser.getIndustryCode()));
    }

    /**
     * 获取切面数据接口
     */
    @PostMapping("/getDataPermissionsObj")
    public Result<AuthObjDataVO> getDataPermissionsObj(){
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        return Result.data(deptBusinessService.getDataPermissionsObj(sysUser));
    }

    /**
     * 获取登录用户租户下切面数据接口
     */
    @PostMapping("/getDataPermissionsObjBySysUser")
    public Result<AuthObjDataVO> getDataPermissionsObjBySysUser(@RequestBody SysUser sysUser){
        logger.info("接收到参数："+ JSON.toJSONString(sysUser));
        return Result.data(deptBusinessService.getDataPermissionsObj(sysUser.getTenantId(),sysUser.getIndustryCode()));
    }

//    @PostMapping("/getUserAuthData")
//    public Result<UserAuthData> getUserAuthData(){
//        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
//
//        return Result.data(deptBusinessService.getUserAuthData(sysUser));
//    }


    /**
     * 功能说明:根据当前用户权限获取行政区划下拉树列表
     *
     * @param
     * @return AjaxResult
     * @throws
     */
    @ApiOperation(value = "获取部门下拉树列表")
    @GetMapping("/getAreaTreeSelect")
    public AjaxResult getAreaTreeSelect() {
        return AjaxResult.success(deptBusinessService.getAreaTreeSelect());
    }

    /**
     * 根据区县获取省市区县街道
     * @return
     */
    @ApiOperation(value = "获取省市区县街道")
    @PostMapping("/getAreaStreet")
    public Result<List<SysAreaVO>> getAreaStreet(@RequestBody String regions){
        List<SysAreaVO> list = deptBusinessService.getAreaStreet(regions);
        return Result.data(list);
    }

}
