package com.chinatower.platform.project.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.DeptConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.framework.manager.AsyncManager;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.project.system.domain.dto.SwithUserDTO;
import com.chinatower.platform.project.system.domain.vo.TenantUserSelectVO;
import com.chinatower.platform.project.system.mapper.SysTenantInfoMapper;
import com.chinatower.platform.project.system.service.ISysAreaDeptManageService;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.resp.SysIndustryViewRelResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoBaseResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户Service业务层处理
 *
 * @author liuhuanhuan
 * @date 2022-01-19
 */
@Slf4j
@Service
public class SysTenantInfoServiceImpl extends ServiceImpl<SysTenantInfoMapper, SysTenantInfo> implements ISysTenantInfoService {
    @Autowired
    private SysTenantInfoMapper sysTenantInfoMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private ISysAreaDeptManageService sysAreaDeptManageService;

    @Lazy
    @Autowired
    private ISysUserService userService;

    @Autowired
    private AuthBaseMapper authBaseMapper;

    /*@Autowired
    private SysTenantTradeMapper sysTenantTradeMapper;*/

    /*@Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;*/
    /**
     * 查询租户
     *
     * @param id 租户主键
     * @return 租户
     */
    @Override
    public SysTenantInfo selectSysTenantInfoById(Long id) {
        SysTenantInfo sysTenantInfo = sysTenantInfoMapper.selectSysTenantInfoById(id);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        sysTenantInfo.setTenantTrades(sysTenantInfoMapper.selectTenantTradeByTenantId(sysTenantInfo.getTenantId(),user.getIndustryCode(),user.getAppVerCode(),user.getPlatformCode()));
        return sysTenantInfo;
    }

    /**
     * 查询租户列表
     *
     * @param quTenantInfoRequest 租户
     * @return 租户
     */
    @DataScope(tenantColumn = "t.tenant_id",industryCode = "t.trade_info")
    @Override
    public List<SysTenantInfo> selectSysTenantInfoList(QuTenantInfoRequest quTenantInfoRequest) {
        return sysTenantInfoMapper.selectSysTenantInfoList(quTenantInfoRequest);
    }

    /**
     * 查询所有租户
     *
     * @return List<SysTenantInfo>
     */
    @Override
    public List<SysTenantInfoResponse> selectAllSysTenant() {
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectAllSysTenantInfo();
        return BeanUtil.copyToList(sysTenantInfos, SysTenantInfoResponse.class);
    }

    /**
     * 根据租户Id查询租户信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<SysTenantInfoResponse> selectTenantInfoByTenantId(String tenantId) {
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectTenantInfoByTenantId(tenantId);
        return BeanUtil.copyToList(sysTenantInfos, SysTenantInfoResponse.class);
    }

    /**
     * 新增租户管理
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysTenantInfo(SysTenantInfo sysTenantInfo) {
        /*if(StrUtil.isEmpty(sysTenantInfo.getDeptId())) {
            sysTenantInfo.setDeptId(this.getDeptId(sysTenantInfo));
        }*/
        sysTenantInfo.setCreateTime(new Date());
        sysTenantInfo.setUpdateTime(new Date());
        sysTenantInfo.setCreateBy(SecurityUtils.getUsername());
        sysTenantInfo.setUpdateBy(SecurityUtils.getUsername());
        int count = sysTenantInfoMapper.insert(sysTenantInfo);
        if(count > 0) {
            // 新增租户组织树根节点
            CreateTenantRootDeptRequest request = new CreateTenantRootDeptRequest();
            request.setTenantId(sysTenantInfo.getTenantId());
            request.setTenantName(sysTenantInfo.getTenantName());
            request.setTenantTowerDeptId(sysTenantInfo.getDeptId());
            sysDeptService.insertTenantRootDept(request);

            //TODO 7-26 表结构修改这边，已经没有这张表
            // 新增租户与管理部门区域关系表
            //sysTenantDeptMapper.insertTenantDept(new SysTenantDept(sysTenantInfo.getTenantId(), sysTenantInfo.getDeptId()));
            // 新增租户和行业及行业应用版本关系表
            //this.batchInsertTenantTrade(sysTenantInfo.getTenantId(), sysTenantInfo.getTenantTrades());
        }
        //新增部门,修改父节点的下属节点
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncInsertSysTen2Redis(sysTenantInfo);
            }
        });
        return count;
    }

    /**
     * 新增租户管理 视联专用
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysTenantInfo4Video(SysTenantInfo sysTenantInfo) {
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectTenantInfoByTenantId(sysTenantInfo.getTenantId());
        if(CollUtil.isEmpty(sysTenantInfos)) {
            return this.insertSysTenantInfo(sysTenantInfo);
        } else {
            // 根据租户id和deptId查询sys_tenant_dept关系表，如果有数据，则不做任何操作，如果无数据，插入一条新数据

            //TODO 7-26 表结构修改这边已经没有这张表
            /*SysTenantDept sysTenantDept = new SysTenantDept(sysTenantInfo.getTenantId(),sysTenantInfo.getDeptId());
            if(sysTenantDeptMapper.countByTenantDept(sysTenantDept) == 0) {
                sysTenantDeptMapper.insertTenantDept(sysTenantDept);
            }
            // 根据租户id和industryCode和appVerCode查询sys_tenant_trade，如果有数据，则不做操作
            List<SysTenantTrade> sysTenantTrades = sysTenantInfo.getTenantTrades();
            sysTenantTrades = sysTenantTrades.stream()
                    .filter(trade -> sysTenantTradeMapper.countTenantTrade(sysTenantInfo.getTenantId(), trade.getIndustryCode(), trade.getAppVerCode()) == 0)
                    .collect(Collectors.toList());
            // 如果无数据，则插入一条数据
            if(CollUtil.isNotEmpty(sysTenantTrades)) {
                this.batchInsertTenantTrade(sysTenantInfo.getTenantId(), sysTenantTrades);
                // 再查看租户表中trade_info字段中是否包含这个industryCode，如果不包含，则在该字段数据结尾进行添加
                SysTenantInfo sysTenantInfo1 = sysTenantInfos.get(0);
                String tradeInfo = StrUtil.emptyToDefault(sysTenantInfo1.getTradeInfo(),"");
                List<String> industryCodes = sysTenantTrades.stream()
                        .filter(trade -> !tradeInfo.contains(trade.getIndustryCode()))
                        .map(SysTenantTrade::getIndustryCode).collect(Collectors.toList());
                if(CollUtil.isEmpty(industryCodes)) {
                    return 0;
                }
                String newTradeInfo = StrUtil.isEmpty(tradeInfo)? String.join(",", industryCodes) : tradeInfo + "," + String.join(",", industryCodes);
                sysTenantInfoMapper.updateTradeInfoByTenantId(sysTenantInfo1.getTenantId(), newTradeInfo);
            }*/
        }
        return 0;
    }

    /**
     * 批量写入租户行业版本关系表
     * @param tenantId
     * @param tenantTrades
     */
    /*@Transactional(rollbackFor = Exception.class)
    public void batchInsertTenantTrade(String tenantId, List<SysTenantTrade> tenantTrades) {
        if(CollUtil.isNotEmpty(tenantTrades)) {
            sysTenantTradeMapper.batchInsertTenantTrade(tenantId, tenantTrades);
        }
    }*/

    /**
     * 获取deptId,根据行政区域编码转换对应为部门编码
     *
     * @param sysTenantInfo
     * @return
     */
    private String getDeptId(SysTenantInfo sysTenantInfo) {
        if (StrUtil.isNotEmpty(sysTenantInfo.getCountyId())) {
            return sysAreaDeptManageService.getDeptCodeByAreaCode(sysTenantInfo.getCountyId(), VideoSystemEnums.AreaLevel.COUNTY.getIndex());
        }
        if (StrUtil.isNotEmpty(sysTenantInfo.getCityId())) {
            return sysAreaDeptManageService.getDeptCodeByAreaCode(sysTenantInfo.getCityId(), VideoSystemEnums.AreaLevel.CITY.getIndex());
        }
        if (StrUtil.isNotEmpty(sysTenantInfo.getProvinceId())) {
            return sysAreaDeptManageService.getDeptCodeByAreaCode(sysTenantInfo.getProvinceId(), VideoSystemEnums.AreaLevel.PROVINCE.getIndex());
        }
        //省,市,县都不传情况,返回空
        return StrUtil.isNotEmpty(sysTenantInfo.getCityId()) ? sysTenantInfo.getCityId() : sysTenantInfo.getProvinceId();
    }

    /**
     * 修改租户管理
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysTenantInfo(SysTenantInfo sysTenantInfo) {
        SysTenantInfo sysTenantInfoOld = this.selectSysTenantInfoById(sysTenantInfo.getId());
        if(!StrUtil.equals(sysTenantInfoOld.getTenantName(),sysTenantInfo.getTenantName())) {
            // 更新租户名称时，同时根据租户id更新sys_dept表中的租户名称
            SysDept dept = new SysDept();
            dept.setTenantId(sysTenantInfo.getTenantId());
            dept.setTenantName(sysTenantInfo.getTenantName());
            sysDeptService.updateByTenantId(dept);
        }
        sysTenantInfo.setDeptId(this.getDeptId(sysTenantInfo));
        sysTenantInfo.setUpdateTime(new Date());
        sysTenantInfo.setUpdateBy(SecurityUtils.getUsername());
        int i = sysTenantInfoMapper.updateById(sysTenantInfo);
        if(i > 0) {
            //TODO 表结构修改这边不知道怎么改
            // 更新租户与管理部门区域关系表
            //this.updateTenantDept(sysTenantInfo.getTenantId(), sysTenantInfo.getDeptId());
            // 更新租户和行业及行业应用版本关系表
            //this.updateTenantTrade(sysTenantInfo.getTenantId(), sysTenantInfo.getTenantTrades());
        }
        //更新时异步更新缓存中数据
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncUpdateSysTen2Redis(sysTenantInfo,sysTenantInfoOld);
            }
        });
        return i;
    }

    /**
     * 更新租户和行业及行业应用版本关系表 先删后插
     * @param tenantId
     * @param tenantTrades
     */
/*    @Transactional(rollbackFor = Exception.class)
    public void updateTenantTrade(String tenantId, List<SysTenantTrade> tenantTrades) {
        sysTenantTradeMapper.delTenantTrade(tenantId);
        this.batchInsertTenantTrade(tenantId, tenantTrades);
    }*/

    /**
     * 更新租户与管理部门区域关系表 先删后插
     * @param tenantId
     * @param deptId
     */
/*    @Transactional(rollbackFor = Exception.class)
    public void updateTenantDept(String tenantId, String deptId) {
        sysTenantDeptMapper.delTenantDept(tenantId);
        sysTenantDeptMapper.insertTenantDept(new SysTenantDept(tenantId, deptId));
    }*/

    /**
     * 删除租户管理信息
     *
     * @param id 租户管理主键
     * @return 结果
     */
    @Override
    public int deleteSysTenantInfoById(Long id) {
//        SysTenantInfo sysTenantInfoOld = this.selectSysTenantInfoById(id);
        int i = sysTenantInfoMapper.deleteById(id);
        // 删除租户和行业及行业应用版本关系表
        //sysTenantTradeMapper.delTenantTrade(sysTenantInfoOld.getTenantId());
        // 删除租户与管理部门区域关系表
        //sysTenantDeptMapper.delTenantDept(sysTenantInfoOld.getTenantId());
        //更新时异步更新缓存中数据
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncDelSysTen2Redis(id);
            }
        });
        return i;
    }

    @Override
    public int selectCountByTenantId(String tenantId) {
        return sysTenantInfoMapper.selectCountByTenantId(tenantId);
    }

    @Override
    public List<SysTenantInfoResponse> selectTenantByDeptIds(List deptIds) {
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectTenantByDeptIds(deptIds);
        return BeanUtil.copyToList(sysTenantInfos, SysTenantInfoResponse.class);
    }

    /**
     * 插入租户时,异步更新该租户部门id对应的租户以及父部门id集合
     * @param sysTenantInfo
     * @author 李峥
     */
    private void asyncInsertSysTen2Redis(SysTenantInfo sysTenantInfo){
        try {
            String deptId = sysTenantInfo.getDeptId();
            String tenantId = sysTenantInfo.getTenantId();
            SysDept sysDept = sysDeptService.selectDeptById(deptId);
            if (null != sysDept){
                //得到所有父节点，父节点中加入租户id
                String ancestors = sysDept.getAncestors();
                if (StringUtils.isNotEmpty(ancestors)){
                    List<String> ancestorsList = Arrays.asList(ancestors.split(","));
                    if (CollUtil.isNotEmpty(ancestorsList)){
                        for (String anDeptId : ancestorsList) {
                            if (!DeptConstants.ROOT_DEPT.equals(anDeptId)){
                                List<String> tenantIdList = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, String.class, RedisDBType.PRIMARY);
                                Set<String> tenantSet = new HashSet<>(tenantIdList);
                                tenantSet.add(tenantId);
                                RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, tenantSet, RedisDBType.PRIMARY);
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            log.error("插入机构时,异步更新父节点集合失败:{}" , e.getMessage());
        }
    }

    /**
     * 更新租户时,异步更新该租户部门id对应的租户以及父部门id集合
     * @param sysTenantInfo
     * @author 李峥
     */
    private void asyncUpdateSysTen2Redis(SysTenantInfo sysTenantInfo,SysTenantInfo sysTenantInfoOld){
        try {
            String deptId = sysTenantInfo.getDeptId();
            String oldDeptId = sysTenantInfoOld.getDeptId();
            //如果部门id没变化,则结束
            if (StringUtils.isNotEmpty(oldDeptId) && StringUtils.isNotEmpty(deptId) && oldDeptId.equals(deptId)){
                return;
            }
            //租户id不会发生变化
            String tenantId = sysTenantInfoOld.getTenantId();
            //根据旧的deptId查询出父节点
            SysDept oldSysDept = sysDeptService.selectDeptById(oldDeptId);
            if (null != oldSysDept){
                //得到所有父节点，父节点部门及本部门中移除租户id
                String oldSysDeptAncestors = oldSysDept.getAncestors();
                if (StringUtils.isNotEmpty(oldSysDeptAncestors)){
                    List<String> ancestorsList = Arrays.asList(oldSysDeptAncestors.split(","));
                    if (CollUtil.isNotEmpty(ancestorsList)){
                        for (String anDeptId : ancestorsList) {
                            if (!DeptConstants.ROOT_DEPT.equals(anDeptId)){
                                List<String> tenantIdList = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, String.class, RedisDBType.PRIMARY);
                                Iterator<String> iterator = tenantIdList.iterator();
                                while (iterator.hasNext()){
                                    String next = iterator.next();
                                    if (next.equals(tenantId)){
                                        iterator.remove();
                                    }
                                }
                                Set<String> tenantSet = new HashSet<>(tenantIdList);
                                RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, tenantSet, RedisDBType.PRIMARY);
                            }
                        }
                    }
                }
            }
            //针对新的部门添加对应的租户
            SysDept sysDept = sysDeptService.selectDeptById(deptId);
            if (null != sysDept){
                //得到所有父节点，父节点中加入租户id
                String ancestors = sysDept.getAncestors();
                if (StringUtils.isNotEmpty(ancestors)){
                    List<String> ancestorsList = Arrays.asList(ancestors.split(","));
                    if (CollUtil.isNotEmpty(ancestorsList)){
                        for (String anDeptId : ancestorsList) {
                            if (!DeptConstants.ROOT_DEPT.equals(anDeptId)){
                                List<String> tenantIdList = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, String.class, RedisDBType.PRIMARY);
                                tenantIdList.add(tenantId);
                                Set<String> tenantSet = new HashSet<>(tenantIdList);
                                RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, tenantSet, RedisDBType.PRIMARY);
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            log.error("插入机构时,异步更新父节点集合失败:{}" , e.getMessage());
        }
    }

    /**
     * 删除租户时,异步删除该租户部门id对应的租户以及父部门id集合
     * @param id
     * @author 李峥
     */
    private void asyncDelSysTen2Redis(Long id){
        try {
            SysTenantInfo sysTenantInfo = sysTenantInfoMapper.selectById(id);
            String tenantId = sysTenantInfo.getTenantId();
            String deptId = sysTenantInfo.getDeptId();
            //根据旧的deptId查询出父节点
            SysDept oldSysDept = sysDeptService.selectDeptById(deptId);
            if (null != oldSysDept){
                //得到所有父节点，父节点部门及本部门中移除租户id
                String oldSysDeptAncestors = oldSysDept.getAncestors();
                if (StringUtils.isNotEmpty(oldSysDeptAncestors)){
                    List<String> ancestorsList = Arrays.asList(oldSysDeptAncestors.split(","));
                    if (CollUtil.isNotEmpty(ancestorsList)){
                        for (String anDeptId : ancestorsList) {
                            if (!DeptConstants.ROOT_DEPT.equals(anDeptId)){
                                List<String> tenantIdList = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, String.class, RedisDBType.PRIMARY);
                                Iterator<String> iterator = tenantIdList.iterator();
                                while (iterator.hasNext()){
                                    String next = iterator.next();
                                    if (next.equals(tenantId)){
                                        iterator.remove();
                                    }
                                }
                                Set<String> tenantSet = new HashSet<>(tenantIdList);
                                RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, tenantSet, RedisDBType.PRIMARY);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("插入机构时,异步删除父节点集合失败:{}" , e.getMessage());
        }
    }


    /**
     * 根据租户模糊查询当前用户可以查询的租户
     * @param sysTenantInfo
     * @return
     */
    @Override
    @DataScope(deptColumn = "a.dept_id", tenantColumn = "a.tenant_id")
    public List<SysTenantInfoResponse> selectByTenantNameAuser(SysTenantInfo sysTenantInfo) {
//        String deptId=SecurityUtils.getLoginUser().getUser().getDeptId();
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectByTenantNameAuser(sysTenantInfo);
        return BeanUtil.copyToList(sysTenantInfos, SysTenantInfoResponse.class);
    }

    /**
     * 根据租户id查询行业及版本信息
     * @param tenantId
     * @return
     */
    @Override
    public List<SysTenantTrade> selectTenantTradeByTenantId(String tenantId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return this.sysTenantInfoMapper.selectTenantTradeByTenantId(tenantId,user.getIndustryCode(),user.getAppVerCode(),user.getPlatformCode());
    }

    @Override
    public List<SysIndustryViewRelResponse> selectAppVerCodeByTenId(String tenantId, String acceptNode) {
        //TODO 7-26 表结构修改这边已经没有这张表
        /*List<SysTenantInfo> list=sysTenantInfoMapper.selectTenantInfoByTenantId(tenantId);

        String tradeIds="";
        if(list!=null&&list.size()>0){
            tradeIds= list.get(0).getTradeInfo();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId",tenantId );
        map.put("tradeId", Arrays.asList(tradeIds.split(",")));*/
        return sysTenantInfoMapper.selectAppVerCodeByTenId(tenantId, acceptNode);
    }

    @Override
    public List<SysIndustryViewRelResponse> selectLocalizationAppVerCodeByTenId(String tenantId, String acceptNode) {
        if (StringUtils.isEmpty(tenantId)) {
            throw new CustomException("客户编码参数不能为空");
        }
        if (StringUtils.isEmpty(acceptNode)) {
            throw new CustomException("受理节点参数不能为空");
        }
        return sysTenantInfoMapper.selectLocalizationAppVerCodeByTenId(tenantId, acceptNode);
    }


    @Override
    @DataScope(tenantColumn = "u.tenant_id")
    public List<TenantUserSelectVO> selectTenantUserByKeyword(SwithUserDTO dto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(SecurityUtils.isSwitch()){
            user = userService.selectUserById(user.getUserId());
        }
        if(user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())){
            SysDept dept = user.getDept();
            if (!dept.getDeptLevel().equalsIgnoreCase("1")
                    && !dept.getDeptLevel().equalsIgnoreCase("01")) {
                String subDeptId = dept.getDeptId().substring(0,6);
                SysDept deptById = sysDeptService.selectDeptByIdTower(subDeptId);

                int currentDeptLevel = Integer.parseInt(dept.getDeptLevel());
                deptById.setDeptLevelInt(currentDeptLevel);
                if (currentDeptLevel >= 4) {
                    //兼容4级以下组织用户，理论上不应该有这种数据
                    deptById.setDeptId(dept.getDeptId());
                }

                List<SysDept> list = authBaseMapper.getDeptByCurrentDeptId(deptById);

                //省分公司的话要加上省公司
                if (currentDeptLevel == 2 && subDeptId.endsWith("001")) {
                    deptById.setDeptId(subDeptId.substring(0,5) + "0");
                    List<SysDept> listAdd = authBaseMapper.getDeptByCurrentDeptId(deptById);
                    list.addAll(listAdd);
                }
                log.info(user.getUserId() + "管理区域：" + JSON.toJSONString(list.stream().map(SysDept::getDeptId).collect(Collectors.toList())));
                List<String> tenantIds = authBaseMapper.getTenantByTowerDeptId(list);
                StringBuffer sb = new StringBuffer(com.chinatower.platform.common.utils.StringUtils.format(" and {} in {} ",  "u.tenant_id", joinParameter(tenantIds)));
                dto.setDataScope(sb.toString());
                log.info(user.getUserId() + "拼接客户编码参数：" + sb);
            }else{
                dto.setDataScope(null);
            }
        }

        
        return sysTenantInfoMapper.selectTenantUserByKeyword(dto);
    }

    @Override
    public int insertSysTenantInfo4Sync(SysTenantInfo sysTenantInfo) {
        return sysTenantInfoMapper.insertSysTenant(sysTenantInfo);
    }

    @Override
    public int updateSysTenantById(SysTenantInfo sysTenantInfo) {
        return sysTenantInfoMapper.updateSysTenantById(sysTenantInfo);
    }

    @Override
    public SysTenantInfo selectByTenantId(String tenantId) {
        return sysTenantInfoMapper.selectByTenantId(tenantId);
    }

    @Override
    public int deleteByTenantId(String tenantId) {
        return sysTenantInfoMapper.deleteByTenantId(tenantId);
    }

    private String joinParameter(List<String> deviceCodes) {
        if (deviceCodes == null || deviceCodes.isEmpty()) {
            return "('')";
        }
        StringBuffer deviceStrBuf = new StringBuffer();
        deviceStrBuf.append("(");
        for (String deviceCode : deviceCodes) {
            deviceStrBuf.append("'" + deviceCode + "',");
        }
        deviceStrBuf.deleteCharAt(deviceStrBuf.length() - 1);
        deviceStrBuf.append(")");
        return deviceStrBuf.toString();
    }


    /**
     * 根据租户模糊查询当前用户可以查询的租户
     * @param sysTenantInfo
     * @return
     */
    @Override
    @DataScope(deptColumn = "a.DEPT_ID", tenantColumn = "a.tenant_id")
    public List<SysTenantInfoBaseResponse> selectByTenantIdAuser(SysTenantInfo sysTenantInfo) {
        String deptId=SecurityUtils.getLoginUser().getUser().getDeptId();
        List<SysTenantInfoBaseResponse> sysTenantInfos = sysTenantInfoMapper.selectByTenantIdAuser(sysTenantInfo);
        return sysTenantInfos;
    }
}
