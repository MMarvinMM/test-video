package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.AppUpgradeInfo;
import com.chinatower.platform.project.system.domain.AppUpgradeInfoResp;
import com.chinatower.platform.project.system.mapper.AppUpgradeInfoMapper;
import com.chinatower.platform.project.system.service.IAppUpgradeInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 功能说明: APP版本升级Service业务层处理
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/4/13 17:49
 * @requestID  需求单号及需求名称
 */
@Slf4j
@Service
public class AppUpgradeInfoServiceImpl implements IAppUpgradeInfoService {

    @Autowired
    private AppUpgradeInfoMapper appUpgradeInfoMapper;

    /**
     * 查询APP版本升级
     *
     * @param id APP版本升级ID
     * @return APP版本升级
     */
    @Override
    public AppUpgradeInfo selectAppUpgradeInfoById(Long id) {
        return appUpgradeInfoMapper.selectAppUpgradeInfoById(id);
    }

    /**
     * 查询APP版本升级列表
     *
     * @param appUpgradeInfo APP版本升级
     * @return APP版本升级
     */
    @Override
    public List<AppUpgradeInfo> selectAppUpgradeInfoList(AppUpgradeInfo appUpgradeInfo) {
        return appUpgradeInfoMapper.selectAppUpgradeInfoList(appUpgradeInfo);
    }

    @Override
    public List<AppUpgradeInfo> queryNewVersion() {
        List<AppUpgradeInfo> list = new ArrayList<>();
        try{
            AppUpgradeInfo iosInfo = appUpgradeInfoMapper.selectByAppCode("ios");
            list.add(iosInfo);
            AppUpgradeInfo androidInfo = appUpgradeInfoMapper.selectByAppCode("android");
            list.add(androidInfo);
        }catch (Exception e){
            log.error("queryNewVersion异常:"+e);
        }
        return list;
    }

    /**
     * 新增APP版本升级
     *
     * @param appUpgradeInfo APP版本升级
     * @return 结果
     */

    @Override
    public int insertAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo) {
        appUpgradeInfo.setCreateTime(new Date());
        appUpgradeInfo.setCreateBy(SecurityUtils.getUsername());
        appUpgradeInfo.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        return appUpgradeInfoMapper.insertAppUpgradeInfo(appUpgradeInfo);
    }

    /**
     * 修改APP版本升级
     *
     * @param appUpgradeInfo APP版本升级
     * @return 结果
     */
    @Override
    public int updateAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo) {
        appUpgradeInfo.setUpdateTime(new Date());
        appUpgradeInfo.setUpdateBy(SecurityUtils.getUsername());
        return appUpgradeInfoMapper.updateAppUpgradeInfo(appUpgradeInfo);
    }

    /**
     * 批量删除APP版本升级
     *
     * @param ids 需要删除的APP版本升级ID
     * @return 结果
     */
    @Override
    public int deleteAppUpgradeInfoByIds(Long[] ids) {
        return appUpgradeInfoMapper.deleteAppUpgradeInfoByIds(ids);
    }

    /**
     * 删除APP版本升级信息
     *
     * @param id APP版本升级ID
     * @return 结果
     */
    @Override
    public int deleteAppUpgradeInfoById(Long id) {
        return appUpgradeInfoMapper.deleteAppUpgradeInfoById(id);
    }

    @Override
    public AjaxResult queryUpgrade(AppUpgradeInfo appUpgradeInfo) {
        log.debug("-->-->>queryUpgrade()查询应用程序升级显示信息");
        //应用程序升级显示信息
        try{
            AppUpgradeInfo bsAppUpgradeInfo = new AppUpgradeInfo();
            BeanUtils.copyProperties(appUpgradeInfo,bsAppUpgradeInfo);
            // 根据应用程序标识查询升级信息
            AppUpgradeInfo result = appUpgradeInfoMapper.selectByAppCode(bsAppUpgradeInfo.getAppCode());
            AppUpgradeInfoResp resultResp = null;
            if(result!=null){
                result.setCreateBy(null);
                result.setUpdateBy(null);
                resultResp = new AppUpgradeInfoResp();
                BeanUtils.copyProperties(result,resultResp);
                resultResp.setAppVersionNo(this.convertVersionNo(result.getAppVersion()));
                return AjaxResult.success(resultResp);
            }
            return AjaxResult.error("查询应用程序升级显示信息失败,没有升级数据");
        }catch (Exception e){
            log.error("应用程序升级显示信息查询异常"+e.getMessage());
        }
        return AjaxResult.error();
    }

    /**
     * 版本号转换
     * 目的：首位不变，以.分割位数一位前面补零位数两位不变   4.10.2变成41002
     * @param versionNo
     * @return
     */
    private String convertVersionNo(String versionNo){
        if(StringUtils.isEmpty(versionNo)){
            return "";
        }
        String[] tempArray = versionNo.split("\\.");
        String realVersionNo=tempArray[0];
        for(int i=1;i<tempArray.length;i++){
            realVersionNo += tempArray[i].length()>1 ?tempArray[i]:("0"+tempArray[i]);
        }
        return realVersionNo;
    }
}
