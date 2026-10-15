package com.chinatower.platform.project.sysbusiness.service;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.custom.entity.OrganDeviceVO;
import com.chinatower.platform.project.sysbusiness.entity.LayerUrlVo;
import com.chinatower.system.api.model.SysAreaVO;
import com.chinatower.system.api.model.AuthObjDataVO;
import com.chinatower.system.api.model.UserAuthData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDeptBusinessService {

    Result saveDeptDeviceRef(OrganDeviceVO organDeviceVO);


    Result getDeviceTree(OrganDeviceVO organDeviceVO);

    public Result getDeptDeviceRefList(OrganDeviceVO organDeviceVO);

    public Result checkProcessByUser(String[] userIds);

    Result<List<LayerUrlVo>> getLayerUrl();


    AuthObjDataVO getDataScopeObj(SysUser sysUser);

    public AuthObjDataVO getDataScopeObj(String tenantId,String industryCode);

    /**
     * 功能说明:根据当前用户权限获取行政区划树
     *
     * @param
     * @return List<TreeSelect>
     */
    List<TreeSelect> getAreaTreeSelect();

    List<SysAreaVO> getAreaStreet(String regions);

    AuthObjDataVO getDataPermissionsObj(SysUser sysUser);

    public AuthObjDataVO getDataPermissionsObj(String tenantId,String industryCode);

//    UserAuthData getUserAuthData(SysUser sysUser);


    Map<String, Set<String>>  getAlarmTypeMap();

    void  addChildDeptAlarmType();

}
