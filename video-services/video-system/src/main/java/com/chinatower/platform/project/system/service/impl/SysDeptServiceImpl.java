package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.constants.*;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.manager.AsyncManager;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.custom.entity.OrganDeviceVO;
import com.chinatower.platform.project.sysbusiness.common.Constants;
import com.chinatower.platform.project.sysbusiness.mapper.DeptBusinessMapper;
import com.chinatower.platform.project.sysbusiness.service.IDeptBusinessService;
import com.chinatower.platform.project.system.constants.MongoConstants;
import com.chinatower.platform.project.system.dao.SysDeptHisMongoDAO;
import com.chinatower.platform.project.system.domain.dto.SysDeptHisMongoDTO;
import com.chinatower.platform.project.system.domain.SysAreaStreet;
import com.chinatower.platform.project.system.domain.SysDeptDevice;
import com.chinatower.platform.project.system.domain.SysDeptStreetRel;
import com.chinatower.platform.project.system.domain.dto.TreeselectBytenantIdDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.platform.project.system.domain.vo.*;
import com.chinatower.platform.project.system.domain.vo.SysDeptDetailVO;
import com.chinatower.platform.project.system.domain.vo.SysDeptStreetVO;
import com.chinatower.platform.project.system.mapper.*;
import com.chinatower.platform.project.system.service.ISysAreaService;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.utils.TreeUtilsBySystem;
import com.chinatower.platform.project.tool.kafka.KafkaProducer;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.*;
import com.chinatower.system.api.model.SysDeptDeviceTreeVO;
import com.chinatower.system.api.request.dept.QuTenantDeptTopRequest;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.resp.SysDeptResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 组织管理 服务实现
 *
 * @author iwhalecloud
 */
@Slf4j
@Service
public class SysDeptServiceImpl implements ISysDeptService {
    @Autowired
    private KafkaProducer kafkaProducer;

    private static final String TOPIC = "tenantAdministrativeRegionRelationship";
    @Autowired
    DeptBusinessMapper deptBusinessMapper;
    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private SysAreaStreetMapper streetMapper;
    @Autowired
    private SysDeptStreetRelMapper sysDeptStreetRelMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptDeviceMapper deptDeviceMapper;
    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Autowired
    private IDeptBusinessService deptBusinessService;

    @Autowired
    private ISysAreaService sysAreaService;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private AuthBaseMapper authBaseMapper;

    @Resource
    private SysDeptHisMongoDAO sysDeptHisMongoDAO;

    private static final String DEPT_LIST_KEY = "dept:list:all";
    private static final String DEPT_TREE_ROOT_ID = "000000";
    private static final String DEPT_TREE_ROOT_ID_CHAR = "无";
    private static final String DEPT_STREET_KEY = "dept:street:";

    //组织状态0为正常
    private static final String DEPT_STATUS = "0";

    private static final String BEIJING_PREFIX = "110";
    private static final String TIANJIN_PREFIX = "120";
    private static final String SHANGHAI_PREFIX = "310";
    private static final String CHONGQING_PREFIX = "500";
    private static final String PARENT_ID = "parentId";
    private static final String DEPT_ID = "deptId";


    /**
     * 铁塔视角
     * 查询组织管理数据
     *
     * @param dept 组织信息
     * @return 组织信息集合
     */
    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "d.tenant_id")
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 客户视角
     * 查询租户组织数据
     *
     * @param dept 组织信息
     * @return 组织信息集合
     */
    @Override
    public List<SysDept> selectTenantDeptList(SysDept dept) {
        return deptMapper.selectTenantDeptList(dept);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 组织列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> selectDeptTree(List<SysDept> depts) {
        JSONArray treeAry = TreeUtils.listToTree(depts, DEPT_ID, PARENT_ID);
        return JSON.parseArray(JSON.toJSONString(treeAry), SysDept.class);
    }

    /**
     * 根据角色ID查询组织树信息
     *
     * @param roleId 角色ID
     * @return 选中组织列表
     */
    @Override
    public List<Integer> selectDeptListByRoleId(String roleId) {
        return deptMapper.selectDeptListByRoleId(roleId);
    }

    @Override
    public List<String> selectTennatListByRoleId(String roleId) {
        return deptMapper.selectTennatListByRoleId(roleId);
    }


    /**
     * 根据组织ID查询信息
     *
     * @param deptId 组织ID
     * @return 组织信息
     */
    @Override
    public SysDept selectDeptById(String deptId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysDeptStreetRelVO vo = new SysDeptStreetRelVO();
        vo.setDeptId(deptId);
        //List<String> applyScope = sysDeptStreetRelMapper.selectDeptStreetAreaCodeByDeptId(vo);
        SysDept sysDept = null;
        if (user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            sysDept = deptMapper.selectDeptById(deptId, null, null);
        } else {
            sysDept = deptMapper.selectDeptById(deptId, user.getIndustryCode(), user.getAppVerCode());
        }
        //sysDept.setApplyScope(applyScope);
        return sysDept;
    }

    /**
     * 根据组织ID查询信息 for 铁塔
     *
     * @param deptId 组织ID
     * @return 组织信息
     */
    @Override
    public SysDept selectDeptByIdTower(String deptId) {
        SysDeptStreetRelVO vo = new SysDeptStreetRelVO();
        vo.setDeptId(deptId);
        return deptMapper.selectDeptById(deptId, null, null);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 组织ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(String deptId) {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询组织是否存在用户
     *
     * @param deptId 组织ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(String deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验组织名称是否唯一
     *
     * @param dept 组织信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        if(!DEPT_TREE_ROOT_ID.equals(dept.getParentId())){//根节点组织不校验名称
            Long c = deptMapper.checkDeptNameUnique(dept);
            if (c.intValue() > 0) {
                return UserConstants.NOT_UNIQUE;
            }
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存组织信息
     *
     * @param dept 组织信息
     * @return 结果
     */
    @Transactional
    @Override
    public SysDeptResponse insertDept(SaveDeptParam dept) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //查询父节点
        SysDept parentDept = deptMapper.selectDeptOnlyById(dept.getParentId());
        dept.setTenantId(parentDept.getTenantId());
        dept.setTenantName(parentDept.getTenantName());
        dept.setTenantTowerDeptId(parentDept.getTenantTowerDeptId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(parentDept.getStatus())) {
            throw new CustomException("组织停用，不允许新增");
        }
        dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
        dept.setCreateTime(new Date());
        dept.setDeptLevel((Integer.parseInt(parentDept.getDeptLevel()) + 1) + "");
        // 根据租户组织父级ID 查询下级的所有组织  例如TEN100000-01   下级存在TEN100000-0101 截取后两位取最大值加1，若是不存在则直接拼接01
        List<SysDept> deptList = deptMapper.selectByParentId(dept.getParentId());
        if (null != deptList && !deptList.isEmpty()) {
            List<String> items = new ArrayList<>();
            deptList.forEach(sysDept -> {
                items.add(StrUtil.subSuf(sysDept.getDeptId(), -2));
            });
            String pre = StrUtil.subPre(deptList.get(0).getDeptId(), -2);
            String suf = addOne(Collections.max(items));
            dept.setDeptId(org.apache.commons.lang3.StringUtils.join(pre, suf));
        } else {
            dept.setDeptId(org.apache.commons.lang3.StringUtils.join(dept.getParentId(), "01"));
        }
        dept.setIndustryCode(user.getIndustryCode());
        dept.setAppVerCode(user.getAppVerCode());
        //新增组织
        int i = deptMapper.insertDept(dept);

        //没看懂为什么要删缓存
        if (StringUtils.isNull(dept.getTenantId())) {
            RedisUtil.delete(DEPT_LIST_KEY, RedisDBType.PRIMARY);
        }
        //新增组织,修改父节点的下属节点
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncInsertSysDept(dept);
            }
        });
        SysDeptResponse sysDeptResponse = BeanUtil.copyProperties(dept, SysDeptResponse.class);


        // 保存设备权限
        this.saveDeptDevice(dept);
        return sysDeptResponse;
    }

    /**
     * 保存设备权限
     *
     * @param dept
     */
    private void saveDeptDevice(SaveDeptParam dept) {
        log.info("保存设备权限:{}", dept);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        OrganDeviceVO vo = new OrganDeviceVO();
        vo.setAppVerCode(dept.getAppVerCode());
        vo.setIndustryCode(dept.getIndustryCode());
        vo.setOrgId(dept.getDeptId());
        vo.setRadarList(dept.getRadarList());
        vo.setTenantId(user.getTenantId());
        vo.setSensorCodeList(dept.getIotList());
        vo.setCameraCodeList(dept.getVideoList());
        vo.setLERList(dept.getLERList());
        vo.setSatelliteResultList(dept.getSatelliteIList());
        vo.setCreator(user.getUserName());
        vo.setCreatorDate(new Date());
        vo.setUavList(dept.getUavList());
        vo.setHornList(dept.getHornList());
        vo.setAlarmTypeList(dept.getAlarmTypeList());
        vo.setLayerList(dept.getLayerList());
        vo.setUavNestList(dept.getUavNestList());
        deptBusinessService.saveDeptDeviceRef(vo);

    }

    private List<SysDeptDevice> codeToDeptDevice(SysDept dept, List<String> deviceList) {

        List<SysDeptDevice> devices = new ArrayList<>();

        if (CollectionUtils.isEmpty(deviceList)) {
            return devices;
        }

        deviceList.forEach(code -> {
            SysDeptDevice device = new SysDeptDevice();
            device.setTenantId(dept.getTenantId());
            device.setDeptId(dept.getDeptId());
            device.setAppVerCode(dept.getAppVerCode());
            device.setIndustryCode(dept.getIndustryCode());
            devices.add(device);
        });

        return devices;
    }

    private void saveDeptStreetRel(SysDept dept) {
        sysDeptStreetRelMapper.deleteDeptStreetRelByOrgId(dept.getDeptId());
        List<SysDeptStreetRel> deptStreetRelList = new ArrayList<>();
        List<String> applyScope = dept.getApplyScope();
        if (null != applyScope && !applyScope.isEmpty()) {
            applyScope.forEach(item -> {
                deptStreetRelList.add(new SysDeptStreetRel(dept.getDeptId(), item));
            });
        }
        List<List<SysDeptStreetRel>> partitionList = Lists.partition(deptStreetRelList, 100);  //分组批量插入
        partitionList.forEach(item -> {
            sysDeptStreetRelMapper.insertBatchDeptStreetRel(item);
        });
    }

    /**
     * 字符串+1方法，该方法将其结尾的整数+1,适用于任何以整数结尾的字符串,不限格式，不限分隔符。
     *
     * @param testStr 要+1的字符串
     * @return +1后的字符串
     * @throws NumberFormatException
     * @author zxcvbnmzb
     */
    public static String addOne(String testStr) {
        String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
        String numStr = strs[strs.length - 1];//取出最后一组数字
        if (numStr != null && numStr.length() > 0) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int n = numStr.length();//取出字符串的长度
            int num = Integer.parseInt(numStr) + 1;//将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return testStr.subSequence(0, testStr.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }

    /**
     * 修改保存组织信息
     *
     * @param dept 组织信息
     * @return 结果
     */
    @Override
    public int updateDept(SaveDeptParam dept) {
        log.info("修改保存组织信息:{}", dept);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysDept newParentDept = deptMapper.selectDeptOnlyById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptOnlyById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
            dept.setDeptLevel((Integer.parseInt(newParentDept.getDeptLevel()) + 1) + "");
        }
        if (deptMapper.countNormalDept(dept.getDeptId()) > 0 && "1".equals(dept.getStatus())) {
            Result.fail("下级未全部停用，该组织不允许停用！");
        }
        dept.setUpdateTime(new Date());
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该组织是启用状态，则启用该组织的所有上级组织
            updateParentDeptStatus(dept);
        }
        if (StringUtils.isNull(dept.getTenantId())) {
            RedisUtil.delete(DEPT_LIST_KEY, RedisDBType.PRIMARY);
        }
        //修改组织时,修改dept权限
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncUpdateSysDept(dept);
            }
        });
        saveDeptDevice(dept);
        //saveDeptStreetRel(dept);
        return result;
    }

    /**
     * 修改该组织的父级组织状态
     *
     * @param dept 当前组织
     */
    private void updateParentDeptStatus(SysDept dept) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptOnlyById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        dept.setUpdateTime(new Date());
        RedisUtil.delete(DEPT_LIST_KEY, RedisDBType.PRIMARY);
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的组织ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (!children.isEmpty()) {
            deptMapper.updateDeptChildren(children);
        }
        RedisUtil.delete(DEPT_LIST_KEY, RedisDBType.PRIMARY);
    }

    /**
     * 删除组织管理信息
     *
     * @param deptId 组织ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(String deptId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        RedisUtil.delete(DEPT_LIST_KEY, RedisDBType.PRIMARY);
        SysDept sysDept = deptMapper.selectDeptOnlyById(deptId);
//        int i = deptMapper.deleteDeptById(deptId);
        int i =  saveSysDeptToMongoHis(deptId);
        //删除组织时,删除该节点及下属节点
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncRemoveDeptAndChild(sysDept);
            }
        });
        return i;
    }

    /**
     * 删除租户组织管理信息
     *
     * @param deptId 组织ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTenantDeptById(String deptId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        OrganDeviceVO organDeviceVO = new OrganDeviceVO();
        organDeviceVO.setOrgId(deptId);
        SysDept sysDept = deptMapper.selectDeptById(deptId, loginUser.getUser().getIndustryCode(), loginUser.getUser().getAppVerCode());
        if (!sysDept.getTenantId().equalsIgnoreCase(loginUser.getUser().getTenantId())) {
            return 0;
        }
//        int i = deptMapper.deleteDeptById(deptId);
        int i =  saveSysDeptToMongoHis(deptId);
        //删除组织时,删除该节点及下属节点
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncRemoveDeptAndChild(sysDept);
            }
        });
        deptBusinessMapper.delOrganDevice(organDeviceVO);
        deptBusinessMapper.delOrganSatellite(organDeviceVO);
        sysDeptStreetRelMapper.deleteDeptStreetRelByOrgId(deptId);
        deptBusinessMapper.delOrganAlarmType(organDeviceVO);
        try {
            JSONObject json = new JSONObject();
            json.put("tenantId", loginUser.getUser().getTenantId());
            kafkaProducer.sendChannelMessage(TOPIC, json.toJSONString());
        } catch (Exception e) {
            log.error("send to tenantAdministrativeRegionRelationship error", e);
        }
        return i;
    }

    /**
     * 查询所有的组织信息
     *
     * @return 组织信息
     */
    @Override
    public List<SysDept> selectAll() {
        log.info("查询组织方法");
        // TODO
        //  第一：解决大key问题
        //  第二：验证接口返回数据前端已不使用
        List<SysDept> list = new ArrayList<>();
//        try {
//            String deptStr = RedisUtil.get(DEPT_LIST_KEY, RedisDBType.PRIMARY);
//            if (BooleanUtil.isFalse(RedisUtil.hasKey(DEPT_LIST_KEY, RedisDBType.PRIMARY)) || StrUtil.isEmpty(deptStr)) {
//                list = deptMapper.selectDeptList(new SysDept());
//                RedisUtil.set(DEPT_LIST_KEY, JSON.toJSONString(list), RedisDBType.PRIMARY);
//            } else {
//                list = JSON.parseArray(JSON.parse(deptStr).toString(), SysDept.class);
//            }
//        } catch (Exception e) {
//            log.error("查询组织方法异常:{}", e.getMessage());
//            e.printStackTrace();
//        }
        return list;
    }

    /**
     * 查询组织的树结构信息
     *
     * @return 组织的树结构信息
     */
    @Override
    public List<TreeSelect> selectTreeSelect() {
        List<TreeSelect> tree = new ArrayList<>();
        List<SysDept> list = this.selectAll();
        for (SysDept dept : list) {
            tree.add(new TreeSelect(dept.getDeptId(), dept.getDeptName(), dept.getParentId()));
        }
        return TreeUtils.listToTreeSelect(tree);
    }


    /**
     * 查询用户本组织的树结构信息
     *
     * @return 组织的树结构信息
     */
    @Override
    public List<TreeSelect> userDeptTreeselect() {
        List<TreeSelect> tree = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        Set<SysDept> list = new HashSet<>();
        // 根据当前人直属单位进行组织信息查询
        if (user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            String deptId = user.getDeptId().substring(0, 6);
            SysDept sysDept = deptMapper.selectDeptOnlyById(deptId);
            if (sysDept == null) {
                return tree;
            }
            if (Integer.valueOf(sysDept.getDeptLevel()) < 3) {
                list.add(sysDept);
                //查询铁塔下组织
                list.addAll(deptMapper.selectTowerDeptByLevel(deptId, Integer.valueOf(sysDept.getDeptLevel())));
            } else {
                list.add(sysDept);
            }

            //添加省分公司处理
            SysDept deptById = new SysDept();
            int currentDeptLevel = Integer.parseInt(sysDept.getDeptLevel());
            deptById.setDeptLevelInt(currentDeptLevel);
            String subDeptId = sysDept.getDeptId().substring(0,6);
            //省分公司的话要加上省公司
            if (currentDeptLevel == 2 && subDeptId.endsWith("001")) {
                deptById.setDeptId(subDeptId.substring(0,5) + "0");
                List<SysDept> listAdd = authBaseMapper.getDeptByCurrentDeptId(deptById);
                list.addAll(listAdd);
            }

        } else {
            //查询租户下组织
            list.addAll(getTenantDept(user));
        }

        for (SysDept dept : list) {
            tree.add(new TreeSelect(dept.getDeptId(), dept.getDeptName(), dept.getParentId()));
        }
        if (tree.size() > 0) {
            tree = tree.stream().sorted(Comparator.comparing(TreeSelect::getId)).collect(Collectors.toList());
        }
        return TreeUtils.listToTreeSelect(tree);
    }

    private Set<SysDept> getTenantDept(SysUser user) {
        Set<SysDept> list = new HashSet<>();
        List<String> dataScops = deptMapper.selectTanentDataScop(user);
        if (selectAllTanentDept(dataScops)) {
            list.addAll(deptMapper.selectAllTanentDept(user));
            return list;
        }
        for (String ds : dataScops) {
            if(!StringUtils.isEmpty(ds)){
                switch (ds) {
                    case "2":
                        //自定数据权限
                        List<SysDept> customDepts = deptMapper.selectCustomTanentDept(user);
                        if (customDepts != null && customDepts.size() > 0) {
                            list.addAll(customDepts);
                        }
                        break;
                    case "3":
                        //本组织数据权限
                        List<SysDept> ownerDepts = deptMapper.selectTenantOwnerDept(user);
                        if (ownerDepts != null && ownerDepts.size() > 0) {
                            list.addAll(ownerDepts);
                        }
                        break;
                    case "4":
                        //本组织及以下数据权限
                        List<SysDept> tenantOwerAndChildDept = getTenantOwerAndChildDept(user);
                        if (tenantOwerAndChildDept != null && tenantOwerAndChildDept.size() > 0) {
                            list.addAll(tenantOwerAndChildDept);
                        }
                        break;
                    case "5":
                        //仅本人
                        List<SysDept> ownerDepts2 = deptMapper.selectTenantOwnerDept(user);
                        if (ownerDepts2 != null && ownerDepts2.size() > 0) {
                            list.addAll(ownerDepts2);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return list;
    }

    private List<SysDept> getTenantOwerAndChildDept(SysUser user) {
        List<SysDept> list = new ArrayList<>();
        String[] split = user.getDeptId().split(",");
        user.setDeptIds(Arrays.asList(split));
        List<SysDept> ownerDepts = deptMapper.selectTenantOwnerDept(user);
        if (ownerDepts != null && ownerDepts.size() > 0) {
            list.addAll(ownerDepts);
            for (SysDept d : ownerDepts) {
                List<SysDept> sysDepts = deptMapper.selectTenantChildDept(d.getDeptId());
                if (sysDepts != null && sysDepts.size() > 0) {
                    list.addAll(sysDepts);
                }
            }
        }

        return list;
    }

    private boolean selectAllTanentDept(List<String> dataScop) {
        if (dataScop.contains("1")) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> selectDeptWithNameRel() {
        Map<String, String> deptMap = new HashMap<>();
        this.selectAll().forEach(dept -> {
            deptMap.put(dept.getDeptId(), dept.getDeptName());
        });
        return deptMap;
    }

    /**
     * 查询组织管理数据,返回树结构信息,定时任务调用
     *
     * @param dept 组织信息
     * @return 返回树结构信息
     * @author 李峥
     */
    @Override
    public List<SysDeptResponse> treeList(SysDept dept) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if ((loginUser != null) && (loginUser.getUser() != null) && loginUser.getUser().getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            String deptId = loginUser.getUser().getDeptId().substring(0, 6);
            SysDept sysDept = deptMapper.selectDeptOnlyById(deptId);
            List<SysDept> sysDeptList = deptMapper.selectTowerDeptByLevel(sysDept.getDeptId(), Integer.valueOf(sysDept.getDeptLevel()));
            JSONArray treeAry = TreeUtils.listToTree(sysDeptList, DEPT_ID, PARENT_ID);
            return JSON.parseArray(JSON.toJSONString(treeAry), SysDeptResponse.class);
        }
        List<SysDept> sysDeptList = deptMapper.selectChildrenByDeptId(dept);
        JSONArray treeAry = TreeUtils.listToTree(sysDeptList, DEPT_ID, PARENT_ID);
        return JSON.parseArray(JSON.toJSONString(treeAry), SysDeptResponse.class);
    }

    /**
     * 查询组织管理数据,返回平级结构信息,定时任务调用
     *
     * @param dept 组织信息
     * @return 返回平级结构信息
     * @author 李峥
     */
    @Override
    public List<SysDeptResponse> nonTreeList(SysDept dept) {
        List<SysDept> sysDeptList = deptMapper.selectChildrenByDeptId(dept);
        return JSON.parseArray(JSON.toJSONString(sysDeptList), SysDeptResponse.class);
    }

    /**
     * 插入机构时,异步更新父节点集合,新增子节点集合
     *
     * @param sysDept
     * @author 李峥
     */
    private void asyncInsertSysDept(SysDept sysDept) {
        try {
            String ancestors = sysDept.getAncestors();
            String deptId = sysDept.getDeptId();
            String tenantId = sysDept.getTenantId();
            String deptKey = null;
            //更新铁塔组织缓存
            if (StringUtils.isEmpty(tenantId)) {
                deptKey = CacheConstants.DEPT_SCOPE_LIST_PREFIX;
            }
            //更新租户组织缓存
            if (!StringUtils.isEmpty(tenantId)) {
                deptKey = CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + tenantId + CommonConstants.COLON;
            }
            if (StringUtils.isNotEmpty(deptKey)) {
                //更新所有父节点的子节点集合
                addRedisDeptList(deptId, ancestors, deptKey);
                //新增节点集合
                List<String> deptList = new ArrayList<>();
                deptList.add(deptId);
                RedisUtil.set(deptKey + deptId, deptList, RedisDBType.PRIMARY);
            }
        } catch (Exception e) {
            log.error("插入机构时,异步更新父节点集合失败:{}", e.getMessage());
        }
    }

    /**
     * 更新机构时,异步更新父节点集合,新增子节点集合
     *
     * @param sysDept
     * @author 李峥
     */
    private void asyncUpdateSysDept(SysDept sysDept) {
        Long id = sysDept.getId();
        if (null == id) {
            return;
        }
        SysDept oldSysDept = this.deptMapper.selectById(id);
        String oldSysDeptId = oldSysDept.getDeptId();
        String oldSysDeptParentId = oldSysDept.getParentId();
        String oldSysDeptParentIdAncestors = oldSysDept.getAncestors();
        String newSysDeptId = sysDept.getDeptId();
        String newSysDeptParentId = sysDept.getParentId();
        String newSysDeptParentIdAncestors = sysDept.getAncestors();
        String tenantId = sysDept.getTenantId();
        String redisDeptKey = null;
        //更新铁塔组织缓存
        if (StringUtils.isEmpty(tenantId)) {
            redisDeptKey = CacheConstants.DEPT_SCOPE_LIST_PREFIX;
        }
        //更新租户组织缓存
        if (!StringUtils.isEmpty(tenantId)) {
            redisDeptKey = CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + tenantId + CommonConstants.COLON;
        }
        if (StringUtils.isNotEmpty(oldSysDeptId) && StringUtils.isNotEmpty(oldSysDeptParentId)) {
            //如果组织编码及层级结构没变,则返回
            if (oldSysDeptId.equals(newSysDeptId) && oldSysDeptParentId.equals(newSysDeptParentId)) {
                return;
            }
            //如果组织编码没变,层级结构变化,则更新原有父节点集合和新父节点集合
            if (oldSysDeptId.equals(newSysDeptId) && !oldSysDeptParentId.equals(newSysDeptParentId)) {
                removeRedisDeptList(oldSysDeptId, oldSysDeptParentIdAncestors, redisDeptKey);
                addRedisDeptList(newSysDeptId, newSysDeptParentIdAncestors, redisDeptKey);
            }
            //如果组织编码变化,层级结构未变化,则更新原有父节点集合集合
            if (!oldSysDeptId.equals(newSysDeptId) && oldSysDeptParentId.equals(newSysDeptParentId)) {
                removeRedisDeptList(oldSysDeptId, oldSysDeptParentIdAncestors, redisDeptKey);
                addRedisDeptList(newSysDeptId, oldSysDeptParentIdAncestors, redisDeptKey);
                renameKey(oldSysDeptId, newSysDeptId, redisDeptKey);
            }
            //如果组织编码变化,层级结构也变化,则更新原有父节点集合集合
            if (!oldSysDeptId.equals(newSysDeptId) && !oldSysDeptParentId.equals(newSysDeptParentId)) {
                removeRedisDeptList(oldSysDeptId, oldSysDeptParentIdAncestors, redisDeptKey);
                addRedisDeptList(newSysDeptId, newSysDeptParentIdAncestors, redisDeptKey);
                renameKey(oldSysDeptId, newSysDeptId, redisDeptKey);
            }
        }
    }

    /**
     * 移除redis集合中某元素
     *
     * @param removeDeptId
     * @param updateKey
     * @param redisDeptKey
     * @author 李峥
     */
    private void removeRedisDeptList(String removeDeptId, String updateKey, String redisDeptKey) {
        //更新旧父节点的子节点集合
        List<String> ancestorsList = Arrays.asList(updateKey.split(","));
        if (CollUtil.isNotEmpty(ancestorsList)) {
            Map<String, Object> multiMap = new HashMap<>();
            for (String anDeptId : ancestorsList) {
                if (!DeptConstants.ROOT_DEPT.equals(anDeptId)) {
                    List<String> deptIdList = RedisUtil.getList(redisDeptKey + anDeptId, String.class, RedisDBType.PRIMARY);
                    Iterator<String> iterator = deptIdList.iterator();
                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        if (next.equals(removeDeptId)) {
                            iterator.remove();
                        }
                    }
                    Set<String> deptIdSet = new HashSet<>(deptIdList);
                    multiMap.put(redisDeptKey + anDeptId, deptIdSet);
//                    RedisUtil.set(redisDeptKey + anDeptId, deptIdSet, RedisDBType.PRIMARY);
                }
            }
            RedisUtil.multiSet(multiMap, RedisDBType.PRIMARY);
        }
    }

    /**
     * redis集合中新增元素
     *
     * @param addDeptId
     * @param ancestors
     * @param deptKey
     * @author 李峥
     */
    private void addRedisDeptList(String addDeptId, String ancestors, String deptKey) {
        //更新父节点的子节点集合
        List<String> ancestorsList = Arrays.asList(ancestors.split(","));
        if (CollUtil.isNotEmpty(ancestorsList)) {
            Map<String, Object> multiSetMap = new HashMap<>();
            for (String anDeptId : ancestorsList) {
                if (!DeptConstants.ROOT_DEPT.equals(anDeptId)) {
                    List<String> deptIdList = RedisUtil.getList(deptKey + anDeptId, String.class, RedisDBType.PRIMARY);
                    deptIdList.add(addDeptId);
                    Set<String> deptIdSet = new HashSet<>(deptIdList);
                    multiSetMap.put(deptKey + anDeptId, deptIdSet);
//                    RedisUtil.set(deptKey + anDeptId, deptIdSet, RedisDBType.PRIMARY);
                }
            }
            RedisUtil.multiSet(multiSetMap, RedisDBType.PRIMARY);
        }
    }

    /**
     * redis集合中删除某key,替换为新key
     *
     * @param oldKey
     * @param updateKey
     * @author 李峥
     */
    private void renameKey(String oldKey, String updateKey, String redisDeptKey) {
        RedisUtil.rename(redisDeptKey + oldKey, redisDeptKey + updateKey, RedisDBType.PRIMARY);
    }

    /**
     * 移除铁塔,租户组织时,其父节点redis中移除该组织id,其子节点redis全部移除
     *
     * @param sysDept
     * @author 李峥
     */
    private void asyncRemoveDeptAndChild(SysDept sysDept) {
        String deptId = sysDept.getDeptId();
        String tenantId = sysDept.getTenantId();
        String redisDeptKey = null;
        //更新铁塔组织缓存
        if (StringUtils.isEmpty(tenantId)) {
            redisDeptKey = CacheConstants.DEPT_SCOPE_LIST_PREFIX;
        }
        //更新租户组织缓存
        if (!StringUtils.isEmpty(tenantId)) {
            redisDeptKey = CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + tenantId + CommonConstants.COLON;
        }
        if (StringUtils.isNotEmpty(redisDeptKey)) {
            //父节点集合
            String ancestors = sysDept.getAncestors();
            List<String> ancestorsList = Arrays.asList(ancestors.split(","));
            if (CollUtil.isNotEmpty(ancestorsList)) {
                for (String anDeptId : ancestorsList) {
                    if (!DeptConstants.ROOT_DEPT.equals(anDeptId)) {
                        List<String> deptIdList = RedisUtil.getList(redisDeptKey + anDeptId, String.class, RedisDBType.PRIMARY);
                        Iterator<String> iterator = deptIdList.iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            if (next.equals(deptId)) {
                                iterator.remove();
                            }
                        }
                        RedisUtil.set(redisDeptKey + anDeptId, deptIdList, RedisDBType.PRIMARY);
                    }
                }
            }
            //移除子节点redis
            List<String> list = RedisUtil.getList(redisDeptKey + deptId, String.class, RedisDBType.PRIMARY);
            if (CollUtil.isNotEmpty(list)) {
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    RedisUtil.delete(redisDeptKey + next, RedisDBType.PRIMARY);
                }
                RedisUtil.delete(redisDeptKey + deptId, RedisDBType.PRIMARY);
            }
        }
    }

    /**
     * 查询租户组织的树结构信息
     *
     * @return 组织的树结构信息
     */
    @Override
    public List<TreeSelect> getTennatTreeselect(SysDept sysDept) {
        List<TreeSelect> tree = new ArrayList<>();
        if (StrUtil.isEmpty(sysDept.getTenantId())) {
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            sysDept.setTenantId(tenantId);
        }
        List<SysDept> list = deptMapper.selectTenantTop(sysDept);
        for (SysDept dept : list) {
            tree.add(new TreeSelect(dept.getDeptId(), dept.getDeptName(), dept.getParentId()));
        }
        return TreeUtils.listToTreeSelect(tree);
    }

    @Override
    public List<TreeSelectBySystem> getTreeselectByTenantId(TreeselectBytenantIdDTO dto) {
        List<TreeSelectBySystem> tree = new ArrayList<>();
        List<TreeSelectBySystem> treeTmp = new ArrayList<>();
        List<SysDept> list = deptMapper.getTreeselectByTenantId(dto);
        if(!CollectionUtils.isEmpty(list)){
            //获取头像集合
            List<String> avatarList = list.stream().filter(e -> !StringUtils.isEmpty(e.getAvatar())).map(SysDept::getAvatar).collect(Collectors.toList());
            //头像批量加密返回
            Map<String, String> avatarMap = fileUrlEncryptUtil.dealObsFileUrlBatch(avatarList);
            for (SysDept dept : list) {
                tree.add(new TreeSelectBySystem(dept.getDeptId(), dept.getDeptName(), dept.getParentId(), "1", "", "", ""));  //组织
                if (null != dept.getUserId()) {
                    tree.add(new TreeSelectBySystem(dept.getUserId(), dept.getNickName(), dept.getDeptId(), "0", dept.getLeader(), dept.getPhone(), dept.getSex(), !StringUtils.isEmpty(avatarMap.get(dept.getAvatar())) ? avatarMap.get(dept.getAvatar()) : dept.getAvatar() )); //用户
                }
            }
        }
        treeTmp = tree.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getDept() + ";" + o.getId() + ";" + o.getAccount() + ";" + o.getLabel() + ";" + o.getPid() + ";" + o.getPhone() + ";" + o.getSex()))), ArrayList::new)
        );
        return TreeUtilsBySystem.listToTreeSelect(treeTmp);
    }

    /**
     * 根据人员查租户的组织人员树
     *
     * @param sysUser
     * @return
     */
    @Override
    @DataScope(industryCode = "d.INDUSTRY_CODE", appVerCode = "d.app_ver_code")
    public List<TreeSelectBySystem> getTennatTree(SysUser sysUser) {
        List<TreeSelectBySystem> tree = new ArrayList<>();

        //查询用户的租户id
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(sysUser.getUserName());
        dto.setDataScope(sysUser.getDataScope());
        SysUser user = userMapper.selectUserByUserName(dto);

        //存在租户时
        if (null != user && StringUtils.isNotEmpty(user.getTenantId())) {
            SysDept sysDept = deptMapper.selectDeptByIdAndIndustryCode(user.getDeptId(), sysUser.getIndustryCode(), sysUser.getAppVerCode());
            if (sysDept == null) {
                return tree;
            }
            //获取组织与人员信息
            List<SysDept> list = deptMapper.selectDeptAndUserTree(user.getTenantId(), sysDept.getDeptId(), sysUser.getRoleId(), sysUser.getIndustryCode(), sysUser.getAppVerCode());
            //树结构
            for (SysDept dept : list) {
                tree.add(new TreeSelectBySystem(dept.getDeptId(), dept.getDeptName(), dept.getParentId(), "1", "", "", ""));  //组织
                if (null != dept.getUserId()) {
                    tree.add(new TreeSelectBySystem(dept.getUserId(), dept.getNickName(), dept.getDeptId(), "0", dept.getLeader(), dept.getPhone(), dept.getSex())); //用户
                }
            }
        }
        //去除list中重复的数据
        List<TreeSelectBySystem> treeTmp = new ArrayList<>();
        treeTmp = tree.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getDept() + ";" + o.getId() + ";" + o.getAccount() + ";" + o.getLabel() + ";" + o.getPid() + ";" + o.getPhone() + ";" + o.getSex()))), ArrayList::new)
        );
        return TreeUtilsBySystem.listToTreeSelect(treeTmp);
    }

    @Override
    public SysDeptDetailVO deptDeviceDetailBatch(List<String> list){
        return this.getDeptDevices(null,list);
    }
    @Override
    public SysDeptDetailVO getDeptDevices(String deptId, List<String> deptIdList) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysDeptDetailVO vo = new SysDeptDetailVO();
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setDeptIdList(deptIdList);
        dept.setTenantId(user.getTenantId());
        dept.setIndustryCode(user.getIndustryCode());
        List<SysDeptDeviceVO> cameras = deptDeviceMapper.getCameras(dept);
        List<SysDeptDeviceVO> raders = deptDeviceMapper.getRaders(dept);
        List<SysDeptDeviceVO> sensors = deptDeviceMapper.getSensors(dept);
        List<SysDeptDeviceVO> satellites = deptDeviceMapper.getSatellites(dept);
        //add by liuxt 组织管理优化需求 start 20230612
        List<SysDeptDeviceVO> uavs = deptDeviceMapper.getUavs(dept);
        // 无人机机场查询
        List<SysDeptDeviceVO> uavNest = deptDeviceMapper.getUavNest(dept);
        List<SysDeptDeviceVO> horns = deptDeviceMapper.getHorns(dept);
        //add by liuxt 组织管理优化需求 end 20230612
        List<OrganAlarmTypeVO> alarmTypes = deptDeviceMapper.getAlarmType(dept);
        List<SysDeptDeviceVO> LERs = deptDeviceMapper.getLERs(dept);
        //add by LQ 组织管理优化需求 start 202503
        List<SysDeptDeviceVO> layers = deptDeviceMapper.getLayers(dept);
        //add by LQ 组织管理优化需求 end 202503
        Set<String> deviceAreas = parseDeviceArea(cameras);
        deviceAreas.addAll(parseDeviceArea(raders));
        deviceAreas.addAll(parseDeviceArea(sensors));
        deviceAreas.addAll(parseDeviceArea(satellites));
        //add by liuxt 组织管理优化需求 start 20230612
        deviceAreas.addAll(parseDeviceArea(uavs));
        deviceAreas.addAll(parseDeviceArea(uavNest));
        deviceAreas.addAll(parseDeviceArea(horns));
        //add by liuxt 组织管理优化需求 end 20230612
        deviceAreas.addAll(parseDeviceArea(LERs));
        if (!deviceAreas.isEmpty()) {
            List<SysArea> areaList = sysAreaService.selectList(new ArrayList<>(deviceAreas));
            Set<String> codeAvailable = areaList.stream().map(SysArea::getAreaCode).collect(Collectors.toSet());
            for (SysDeptDeviceVO camera : cameras) {
                setDevicePid(codeAvailable, camera);
            }
            for (SysDeptDeviceVO radar : raders) {
                setDevicePid(codeAvailable, radar);
            }
            for (SysDeptDeviceVO sensor : sensors) {
                setDevicePid(codeAvailable, sensor);
            }
            for (SysDeptDeviceVO satellite : satellites) {
                setDevicePid(codeAvailable, satellite);
            }
            //add by liuxt 组织管理优化需求 start 20230612
            for (SysDeptDeviceVO uav : uavs) {
                setDevicePid(codeAvailable, uav);
            }
            for (SysDeptDeviceVO nest : uavNest) {
                setDevicePid(codeAvailable, nest);
            }
            for (SysDeptDeviceVO horn : horns) {
                setDevicePid(codeAvailable, horn);
            }
            //add by liuxt 组织管理优化需求 end 20230612
            for (SysDeptDeviceVO LER : LERs) {
                setDevicePid(codeAvailable, LER);
            }
        }

        List<SysDeptStreetVO> sysDeptStreetVOS = sysDeptStreetRelMapper.selectStreet(dept);
        vo.setCameras(cameras);
        vo.setRadars(raders);
        vo.setSensors(sensors);
        vo.setSatellites(satellites);
        //add by liuxt 组织管理优化需求 start 20230612
        vo.setUavs(uavs);
        vo.setUavNest(uavNest);
        vo.setHorns(horns);
        //add by liuxt 组织管理优化需求 end 20230612
        vo.setAlarmTypeList(alarmTypes);
        vo.setLERs(LERs);
        vo.setLayers(layers);
        vo.setDeptStreets(sysDeptStreetVOS);

        return vo;
    }


    @Override
    public List<SysDeptDeviceTreeVO> getDeptStreetTree(String deptId) {
        List<SysDeptDeviceTreeVO> appTreeList = new ArrayList<>();//app端返回的list
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setTenantId(user.getTenantId());
        dept.setIndustryCode(user.getIndustryCode());
        List<SysDeptDeviceVO> list = sysDeptStreetRelMapper.selectDeptStreet(dept);
        if(!CollectionUtils.isEmpty(list)){
            appTreeList = buildNecessaryDeviceTree(list,Constants.ONE_STR);//flag为1 代表不返回devices数据
        }
        return appTreeList;
    }

    private Set<String> parseDeviceArea(List<SysDeptDeviceVO> list){
        return list.stream().flatMap(x -> Stream.of(StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY),
                StringUtils.nvl(x.getTownCode(), org.apache.commons.lang3.StringUtils.EMPTY))).collect(Collectors.toSet());
    }

    private void setDevicePid(Set<String> codeAvailable,SysDeptDeviceVO vo){
        String code;
        if (codeAvailable.contains(code = vo.getTownCode())) {
            vo.setPid(code);
        } else if (codeAvailable.contains(code = vo.getCountyId())) {
            vo.setPid(code);
        } else if (codeAvailable.contains(code = vo.getCityId())) {
            vo.setPid(code);
        } else if (codeAvailable.contains(code = vo.getProvinceId())) {
            vo.setPid(code);
        }
    }

    private List<SysDeptStreetVO> getDeptStreet(List<SysDeptDeviceVO> deviceScop) {
        if (deviceScop == null || deviceScop.size() == 0) {
            return null;
        }
        List<SysDeptStreetVO> list = new ArrayList<>();

        deviceScop.stream().forEach(d -> {

            if ((d == null) || (StringUtils.isBlank(d.getCityId()) && StringUtils.isBlank(d.getProvinceId()) && StringUtils.isBlank(d.getCountyId()) && StringUtils.isBlank(d.getTownCode()))) {
                return;
            }
            SysDeptStreetVO vo = new SysDeptStreetVO();
            vo.setCityCode(d.getCityId());
            vo.setProvinceCode(d.getProvinceId());
            vo.setCountyCode(d.getCountyId());
            vo.setStreetCode(d.getTownCode());
            list.add(vo);
        });
        return list;
    }

    private List<SysDeptStreetVO> getDeptStreetScop(SysDeptDetailVO vo) {
        List<SysDeptStreetVO> list = new ArrayList<>();
        filterAddStreet(list, getDeptStreet(vo.getCameras()));
        filterAddStreet(list, getDeptStreet(vo.getRadars()));
        filterAddStreet(list, getDeptStreet(vo.getSensors()));
        filterAddStreet(list, getDeptStreet(vo.getSatellites()));
        //add by liuxt 组织管理优化需求 start
        filterAddStreet(list, getDeptStreet(vo.getUavs()));
        filterAddStreet(list, getDeptStreet(vo.getHorns()));
        //add by liuxt 组织管理优化需求 end
        filterAddStreet(list, getDeptStreet(vo.getLERs()));
        return list;
    }

    private void filterAddStreet(List<SysDeptStreetVO> list, List<SysDeptStreetVO> add) {
        if (add != null && add.size() > 0) {
            list.addAll(add);
        }
    }

    private boolean isMgr() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<String> dataScop = deptMapper.selectTanentDataScop(user);
        if (CollUtil.isEmpty(dataScop)) {
            return false;
        }
        return selectAllTanentDept(dataScop);
    }

    private boolean isMgr(SysUser user) {
        List<String> dataScope = deptMapper.selectTanentDataScop(user);
        if (CollUtil.isEmpty(dataScope)) {
            return false;
        }
        return selectAllTanentDept(dataScope);
    }

    @Override
    public SysDeptDetailVO getDeptDeviceScop() {
        SysDeptDetailVO vo;
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user.getIsTenantAdmin().equalsIgnoreCase("1") || isMgr()) {
            vo = getDeptDeviceScopMgr(user);
        } else {
            vo = getDeptDevices(null,null);//add by liuxt 20240129
        }
        return vo;
    }

    @Override
    public Set<DeptVO> getDeptListByUserDataScope(SysUser sysUser) {
        Set<DeptVO> deptList = new HashSet<>();
        SysUser user = userMapper.selectUserOnlyByUserName(sysUser.getUserName());
        sysUser.setUserId(user.getUserId());

        Set<String> dataScopes = sysUser.getRoles().stream().map(BaseDataScopeEntity::getDataScope).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (dataScopes.contains(Constants.DATA_SCOPE_ALL)) {
            TreeselectBytenantIdDTO dto = new TreeselectBytenantIdDTO();
            dto.setTenantId(sysUser.getTenantId());
            dto.setIndustryCode(sysUser.getIndustryCode());
            List<SysDept> treeselectByTenantId = deptMapper.selectTreeselectByTenantId(dto);
            if (CollUtil.isNotEmpty(treeselectByTenantId)) {
                treeselectByTenantId.forEach(d -> {
                    deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                });
            }
        } else {
            for (String roleDataScope : dataScopes) {
                switch (roleDataScope) {
                    case Constants.DATA_SCOPE_CUSTOM:
                        List<SysDept> sysDepts = deptMapper.selectCustomTanentDept(sysUser);
                        if (CollUtil.isNotEmpty(sysDepts)) {
                            sysDepts.forEach(d -> {
                                deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                            });
                        }
                        break;
                    case Constants.DATA_SCOPE_DEPT:
                        deptList.add(new DeptVO(sysUser.getDept().getDeptId(), sysUser.getDept().getParentId(), sysUser.getDept().getParentName()));
                        break;
                    case Constants.DATA_SCOPE_SELF:
                        deptList.add(new DeptVO(sysUser.getDept().getDeptId(), sysUser.getDept().getParentId(), sysUser.getDept().getParentName()));
                        break;
                    case Constants.DATA_SCOPE_DEPT_AND_CHILD:
                        //如果不是根节点,加deptId数据过滤
                        List<SysDept> sysDepts1 = deptMapper.selectTenantChildDept(sysUser.getDept().getDeptId());
                        if (CollUtil.isNotEmpty(sysDepts1)) {
                            sysDepts1.forEach(d -> {
                                deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return deptList;
    }


    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "d.tenant_id")
    public Map<String, Object> selectTennatTop(SysDept dept) {
        Map<String, Object> result = new HashMap<>();
        if (StrUtil.isEmpty(dept.getTenantId())) {
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            String dataScope = dept.getDataScope();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tenantId)) {
                dept.setTenantId(tenantId);
            }
            if (StringUtils.isEmpty(tenantId) && StringUtils.isNotEmpty(dataScope)) {
                dataScope.replace("dept_id", "tenant_tower_dept_id");
                dept.setDataScope(dataScope);
            }
        }
        List<SysDept> sysDepts = deptMapper.selectTenantTop(dept);
        List<SysDept> returnDepts = sysDepts.stream().filter(item -> StringUtils.equals(item.getParentId(), DEPT_TREE_ROOT_ID)).collect(Collectors.toList());
        returnDepts.forEach(item -> {
            List<SysDept> childrenDepts = sysDepts.stream().filter(sysDept -> StringUtils.equals(sysDept.getParentId(), item.getDeptId())).collect(Collectors.toList());
            if (CollUtil.isEmpty(childrenDepts)) {
                item.setHasChildren(false);
            } else {
                item.setHasChildren(true);
            }
        });
        result.put("deptList", returnDepts);
        return result;
    }

    @Override
    @DataScope(tenantColumn = "d.tenant_id", industryCode = "d.industry_code", deptColumn = "d.dept_id")
    public List<SysDept> listSubsetDeptTree(SysDept dept) {
        return deptMapper.selectTenantTop(dept);
    }

    /**
     * 新增租户组织树根节点
     *
     * @param createTenantRootDeptRequest 创建租户组织树根节点入参
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTenantRootDept(CreateTenantRootDeptRequest createTenantRootDeptRequest) {
        SysDept dept = new SysDept();
        dept.setDeptId(createTenantRootDeptRequest.getTenantId() + "-01");
        dept.setDeptName(createTenantRootDeptRequest.getTenantName());
        dept.setParentId(DEPT_TREE_ROOT_ID);
        dept.setParentName(createTenantRootDeptRequest.getTenantName());
        dept.setAncestors(DEPT_TREE_ROOT_ID);
        dept.setDeptLevel("1");
        dept.setTenantId(createTenantRootDeptRequest.getTenantId());
        dept.setTenantName(createTenantRootDeptRequest.getTenantName());
        dept.setTenantTowerDeptId(createTenantRootDeptRequest.getTenantTowerDeptId());
        dept.setCreateTime(new Date());
        dept.setCreateBy(SecurityUtils.getUsername());
        int i = deptMapper.insertDept(dept);
        //新增组织,修改父节点的下属节点
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncInsertSysDept(dept);
            }
        });
        return i;
    }

    /**
     * 根据租户id更新sys_dept表中的租户名称
     *
     * @param dept
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByTenantId(SysDept dept) {
        dept.setUpdateBy(SecurityUtils.getUsername());
        dept.setUpdateTime(new Date());
        deptMapper.updateByTenantId(dept);
    }

    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "d.tenant_id", industryCode = "d.INDUSTRY_CODE")
    public List<TreeSelect> getVideoTreeselect(SysDept sysDept) {
        List<TreeSelect> tree = new ArrayList<>();
        //兼容铁塔管理员,租户管理员及租户普通用户权限,租户普通用户只可查看
        if (StrUtil.isEmpty(sysDept.getTenantId())) {
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            String dataScope = sysDept.getDataScope();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tenantId)) {
                sysDept.setTenantId(tenantId);
            }
            //铁塔管理员数据权限过滤
            if (StringUtils.isEmpty(tenantId) && StringUtils.isNotEmpty(dataScope)) {
                dataScope.replace("dept_id", "tenant_tower_dept_id");
                sysDept.setDataScope(dataScope);
            }
        }
        List<SysDept> list = deptMapper.selectTenantDeptList(sysDept);
        for (SysDept dept : list) {
            tree.add(new TreeSelect(dept.getDeptId(), dept.getDeptName(), dept.getParentId(), null, dept.getTenantId()));
        }
        return TreeUtils.listToTreeSelect(tree);
    }

    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "d.tenant_id", industryCode = "d.INDUSTRY_CODE")
    public List<SysDept> getDeptListAll(SysDept sysDept) {
        if (StrUtil.isEmpty(sysDept.getTenantId())) {
            String tenantId = SecurityUtils.getLoginUser().getUser().getTenantId();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tenantId)) {
                sysDept.setTenantId(tenantId);
            }
        }
        return deptMapper.selectTenantDeptList(sysDept);
    }

    @Resource
    private SysTenantInfoMapper tenantInfoMapper;

    @Override
    public List<TreeSelect> getStreetTreeselect(SysDept dept) {
        List<TreeSelect> tree = new ArrayList<>();
        List<SysTenantInfo> sysTenantInfos = tenantInfoMapper.selectTenantInfoByTenantId(dept.getTenantId());
        List<SysAreaStreet> list = new ArrayList<>();
        if (null != sysTenantInfos && !sysTenantInfos.isEmpty()) {
            String area = String.format(StringUtils.join(sysTenantInfos.get(0).getDeptId(), DEPT_TREE_ROOT_ID));
            if (org.apache.commons.lang3.StringUtils.isBlank(RedisUtil.get(StringUtils.join(DEPT_STREET_KEY, area), RedisDBType.PRIMARY))) {
                list = streetMapper.selectSysAreaStreetListByParentCode(area);
                RedisUtil.set(StringUtils.join(DEPT_STREET_KEY, area), list, RedisDBType.PRIMARY);
            } else {
                String deptStreetListStr = RedisUtil.get(StringUtils.join(DEPT_STREET_KEY, area), RedisDBType.PRIMARY);
                list = JSONObject.parseArray(deptStreetListStr, SysAreaStreet.class);

            }
            for (SysAreaStreet SysAreaStreet : list) {
                tree.add(new TreeSelect(SysAreaStreet.getAreaCode(), SysAreaStreet.getAreaName(), SysAreaStreet.getAreaParentCode(), null, null));
            }
        }
        return TreeUtils.listToTreeSelect(tree);
    }

    /**
     * 查询租户组织管理数据,返回树结构信息,视联调用
     *
     * @param quTenantDeptTopRequest 请求实体
     * @return 返回树结构信息
     * @author 李峥
     */
    @Override
    public List<SysDeptResponse> tenantTreeList2Video(QuTenantDeptTopRequest quTenantDeptTopRequest) {
        quTenantDeptTopRequest.setStatus(DEPT_STATUS);
        SysDept sysDept = BeanUtil.copyProperties(quTenantDeptTopRequest, SysDept.class);
        List<SysDept> sysDeptList = deptMapper.selectChildrenByDeptId(sysDept);
        JSONArray treeAry = TreeUtils.listToTree(sysDeptList, DEPT_ID, PARENT_ID);
        return JSON.parseArray(JSON.toJSONString(treeAry), SysDeptResponse.class);
    }

    /**
     * 查询租户组织顶级结构信息
     *
     * @param dept 组织信息
     * @return 返回平级结构信息集合
     * @author 李峥
     */
    @Override
    public Map<String, Object> selectTenantTop2Video(SysDept dept) {
        Map<String, Object> result = new HashMap<>();
        List<SysDept> sysDepts = deptMapper.selectTenantTop2Video(dept);
        List<SysDept> returnDepts = sysDepts.stream().filter(item ->
                StringUtils.equals(item.getParentId(), DEPT_TREE_ROOT_ID)
                        || StringUtils.equals(item.getParentId(), DEPT_TREE_ROOT_ID_CHAR)).collect(Collectors.toList());
        returnDepts.forEach(item -> {
            List<SysDept> childrenDepts = sysDepts.stream().filter(sysDept -> StringUtils.equals(sysDept.getParentId(), item.getDeptId())).collect(Collectors.toList());
            if (CollUtil.isEmpty(childrenDepts)) {
                item.setHasChildren(false);
            } else {
                item.setHasChildren(true);
            }
        });
        result.put("deptList", returnDepts);
        return result;
    }

    @Override
    public List<SysDeptAreaVO> getDeptAndAreaTree() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (Objects.isNull(user) || !Objects.equals(UserTypeEnum.TOWER.getType(), user.getUserType())) {
            return Collections.emptyList();
        }
        // 根据当前人直属单位进行组织信息查询
        String deptId = user.getDeptId().substring(0, 6);
        SysDeptAreaVO sysDept = deptMapper.selectDeptAreaOnlyById(deptId);
        if (Objects.isNull(sysDept)) {
            return Collections.emptyList();
        }
        List<SysDeptAreaVO> areaList = Lists.newArrayList();
        // 判断是全国范围
        if (sysDept.getDeptLevel().equals(1)) {
            List<SysArea> allArea = sysAreaMapper.getAllArea(null);
            for (SysArea sysArea : allArea) {
                // 只获取省市区数据
                if (sysArea.getAreaLevel().equals("1")) {
                    continue;
                }
                SysDeptAreaVO vo = new SysDeptAreaVO(sysArea.getAreaCode(), sysArea.getAreaName(), sysArea.getAreaParentCode());
                areaList.add(vo);
            }
        } else {
            areaList = deptMapper.selectAreaByDeptAndLevel(deptId, sysDept.getDeptLevel());
        }
        if (CollectionUtils.isEmpty(areaList)) {
            return Collections.emptyList();
        }
        // 过滤掉顶级区域
        List<SysDeptAreaVO> sysAreaList = areaList.stream().filter(e -> !Objects.equals("1", e.getPid()))
                .distinct().sorted(Comparator.comparing(SysDeptAreaVO::getId)).collect(Collectors.toList());

        JSONArray jsonArray = TreeUtils.listToTree(sysAreaList, "id", "pid", "children");

        return JSON.parseArray(JSON.toJSONString(jsonArray), SysDeptAreaVO.class);

//        List<SysDeptAreaVO> tree = new ArrayList<>();
//        SysUser user = SecurityUtils.getLoginUser().getUser();
//        List<SysDeptAreaVO> result = new ArrayList<>();
//        // 根据当前人直属单位进行组织信息查询
//        if (user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
//            String deptId = user.getDeptId().substring(0, 6);
//            SysDeptAreaVO sysDept = deptMapper.selectDeptAreaOnlyById(deptId);
//            // 判断是全国范围
//            if (sysDept.getDeptLevel().equals(Integer.valueOf(1))) {
//                List<SysArea> allArea = sysAreaMapper.getAllArea(null);
//                for (SysArea sysArea : allArea) {
//                    // 只获取省市区数据
//                    if (sysArea.getAreaLevel().equals("1")) {
//                        continue;
//                    }
//                    SysDeptAreaVO vo = new SysDeptAreaVO(sysArea.getAreaCode(), sysArea.getAreaName(), sysArea.getAreaParentCode());
//                    tree.add(vo);
//                }
//            } else {
//                List<String> areaCodeList = Lists.newArrayList();
//                if (sysDept.getDeptLevel().equals(2)) {
//                    if (isDirectCity(deptId)) {
//                        areaCodeList = deptMapper.selectDirectCityAreaCodeByProvince(deptId);
//                    } else {
//                        areaCodeList = deptMapper.selectDeptAreaCodeByProvince(deptId);
//                    }
//                }
//                if (sysDept.getDeptLevel().equals(3)) {
//                    if (isDirectCity(deptId)) {
//                        areaCodeList = deptMapper.selectDirectCityAreaCodeByCity(deptId);
//                    } else
//                        areaCodeList = deptMapper.selectDeptAreaCodeByCity(deptId);
//                }
//
//                if (sysDept.getDeptLevel().equals(4)) {
//                    areaCodeList = deptMapper.selectDeptAreaCodeByCounty(deptId);
//                }
//
//                if (CollUtil.isEmpty(areaCodeList)) {
//                    return Lists.newArrayList();
//                }
//                List<String> deptAreaCodes = areaCodeList.stream().map(k -> StringUtils.remove(k, " ")).collect(Collectors.toList());
//                tree = deptMapper.selectAreaByDeptManage(deptAreaCodes);
//                // 添加省级行政区
//                SysArea sysArea = new SysArea();
//                sysArea.setAreaCodeList(areaCodeList);
//                sysArea.setAreaLevel(VideoSystemEnums.AreaLevel.PROVINCE.getIndex());
//                List<SysArea> provinceList = sysAreaMapper.getArea(sysArea);
//                Map<String, SysArea> provinceMap = provinceList.stream().collect(Collectors.toMap(SysArea::getAreaCode, Function.identity()));
//                for (SysDeptAreaVO sysDeptAreaVO : tree) {
//                    if (provinceMap.containsKey(sysDeptAreaVO.getPid())) {
//                        SysArea area = provinceMap.get(sysDeptAreaVO.getPid());
//                        SysDeptAreaVO vo = new SysDeptAreaVO();
//                        vo.setId(area.getAreaCode());
//                        vo.setPid(area.getAreaParentCode());
//                        vo.setAreaName(area.getAreaName());
//                        result.add(vo);
//                    }
//                }
//            }
//
//            // 直辖市处理
//            List<String> areaCodeList = Lists.newArrayList();
//            areaCodeList.add(VideoSystemEnums.DirectCityArea2Dept.BEIJING.getName());
//            areaCodeList.add(VideoSystemEnums.DirectCityArea2Dept.TIANJIN.getName());
//            areaCodeList.add(VideoSystemEnums.DirectCityArea2Dept.SHANGHAI.getName());
//            areaCodeList.add(VideoSystemEnums.DirectCityArea2Dept.CHONGQING.getName());
//            SysArea sysArea = new SysArea();
//            sysArea.setAreaCodeList(areaCodeList);
//            sysArea.setAreaLevel(VideoSystemEnums.AreaLevel.PROVINCE.getIndex());
//            List<SysArea> directCityList = sysAreaMapper.getArea(sysArea);
//            Map<String, SysArea> directCityMap = directCityList.stream().collect(Collectors.toMap(SysArea::getAreaCode, Function.identity()));
//            for (SysDeptAreaVO sysDeptAreaVO : tree) {
//                if (directCityMap.containsKey(sysDeptAreaVO.getPid())) {
//                    SysArea area = directCityMap.get(sysDeptAreaVO.getPid());
//                    SysDeptAreaVO vo = new SysDeptAreaVO();
//                    vo.setId(area.getAreaCode());
//                    vo.setPid(area.getAreaParentCode());
//                    vo.setAreaName(area.getAreaName());
//                    result.add(vo);
//                    break;
//                }
//            }
//        }
//
//        result.addAll(tree);
//        if (result.size() > 0) {
//            result = result.stream().distinct().sorted(Comparator.comparing(SysDeptAreaVO::getId)).collect(Collectors.toList());
//        }
//        JSONArray jsonArray = TreeUtils.listToTree(result, "id", "pid", "children");
//        List<SysDeptAreaVO> treeList = JSON.parseArray(JSON.toJSONString(jsonArray), SysDeptAreaVO.class);
//        return treeList;
    }

    @Override
    public List<SysDeptDeviceTreeVO> getCameraTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.CAMERA_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceTreeVO> getRadarTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.RADAR_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceTreeVO> getSensorTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.SENSOR_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceTreeVO> getSatellitesTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.SATELLITES_TREE);
        return list;
    }

    /**
     * 组织管理优化需求
     * 查询无人机设备行政区域树
     * add by liuxt 20230612
     * @return
     */
    @Override
    public List<SysDeptDeviceTreeVO> getUavTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.UAV_TREE);
        return list;
    }


    /**
     * 组织管理优化需求
     * 查询喇叭设备行政区域树
     * add by liuxt 20230612
     * @return
     */
    @Override
    public List<SysDeptDeviceTreeVO> getHornTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.HORN_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceTreeVO> getLERTree() {
        List<SysDeptDeviceTreeVO> list = getTreeCommon(Constants.APP,Constants.LER_TREE);
        return list;
    }


    @Override
    public List<SysDeptDeviceVO> getCameraTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.CAMERA_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceVO> getRadarTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.RADAR_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceVO> getSensorTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.SENSOR_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceVO> getSatellitesTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.SATELLITES_TREE);
        return list;
    }

    /**
     * 组织管理优化需求
     * 查询无人机设备行政区域树
     * add by liuxt 20230612
     * @return
     */
    @Override
    public List<SysDeptDeviceVO> getUavTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.UAV_TREE);
        return list;
    }

    /**
     * 组织管理优化需求
     * 查询无人机机场行政区域树
     * @return
     */
    @Override
    public List<SysDeptDeviceVO> getUavNestTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.UAV_NEST_TREE);
        return list;
    }


    /**
     * 组织管理优化需求
     * 查询喇叭设备行政区域树
     * add by liuxt 20230612
     * @return
     */
    @Override
    public List<SysDeptDeviceVO> getHornTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.HORN_TREE);
        return list;
    }

    @Override
    public List<SysDeptDeviceVO> getLERTreeForPC() {
        List<SysDeptDeviceVO> list = getTreeCommon(Constants.PC,Constants.LER_TREE);
        return list;
    }

    /**
     * 组织管理优化需求
     * app和pc 获取树共通方法
     * add by liuxt 20230612
     * @param platform  pc,app
     * @param treeName  cameraTree，radarTree，sensorTree，satellitesTree，uavTree，hornTree
     * @return
     */
    private List getTreeCommon(String platform,String treeName) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<SysDeptDeviceVO> list = null;//pc端返回的list
        List<SysDeptDeviceTreeVO> appTreeList;//app端返回的list

        if (Constants.ONE_STR.equalsIgnoreCase(user.getIsTenantAdmin()) || isMgr(user)) {
            switch (treeName) {
                case Constants.CAMERA_TREE://摄像机权限
                    list = deptDeviceMapper.getCamerasScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.RADAR_TREE://雷达权限
                    list = deptDeviceMapper.getRadersScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.SENSOR_TREE://物联设备权限
                    list = deptDeviceMapper.getSensorsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.SATELLITES_TREE://卫星火点数量权限
                    list = deptDeviceMapper.getSatellitesScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.UAV_TREE://无人机设备权限
                    list = deptDeviceMapper.getUavsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.UAV_NEST_TREE://无人机机场设备权限
                    list = deptDeviceMapper.getUavNestScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.HORN_TREE://大喇叭设备权限
                    list = deptDeviceMapper.getHornsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                case Constants.LER_TREE://执法记录仪设备权限
                    list = deptDeviceMapper.getLERsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
                    break;
                default:
                    break;
            }
        } else {
            String deptId = user.getDeptId();
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            dept.setTenantId(user.getTenantId());
            dept.setIndustryCode(user.getIndustryCode());
            switch (treeName) {
                case Constants.CAMERA_TREE://摄像机权限
                    list = deptDeviceMapper.getCameras(dept);
                    break;
                case Constants.RADAR_TREE://雷达权限
                    list = deptDeviceMapper.getRaders(dept);
                    break;
                case Constants.SENSOR_TREE://物联设备权限
                    list = deptDeviceMapper.getSensors(dept);
                    break;
                case Constants.SATELLITES_TREE://卫星火点数量权限
                    list = deptDeviceMapper.getSatellites(dept);
                    break;
                case Constants.UAV_TREE://无人机设备权限
                    list = deptDeviceMapper.getUavs(dept);
                    break;
                case Constants.HORN_TREE://大喇叭设备权限
                    list = deptDeviceMapper.getHorns(dept);
                    break;
                case Constants.LER_TREE://执法记录仪设备权限
                    list = deptDeviceMapper.getLERs(dept);
                    break;
                case Constants.UAV_NEST_TREE://无人机场备权限
                    list = deptDeviceMapper.getUavNest(dept);
                    break;
                default:
                    break;
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }else{
            if(Constants.APP.equals(platform)){
                appTreeList = buildNecessaryDeviceTree(list,"");
                return appTreeList;
            }else{
                return list;
            }
        }
    }


    private List<SysDeptDeviceTreeVO> buildNecessaryDeviceTree(List<SysDeptDeviceVO> devices,String flag) {
        List<String> areaCodes = devices.stream().filter(Objects::nonNull)
                .flatMap(x -> Stream.of(StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        StringUtils.nvl(x.getTownCode(), org.apache.commons.lang3.StringUtils.EMPTY)))
                .distinct().collect(Collectors.toList());
        List<SysArea> areas = sysAreaService.selectList(areaCodes);
        Map<String, SysDeptDeviceTreeVO> areaMap = areas.stream().filter(x -> Objects.nonNull(x.getAreaParentCode())
                        && !org.apache.commons.lang3.StringUtils.equals(x.getAreaCode(), x.getAreaParentCode())
                        && areaCodes.contains(x.getAreaCode()))
                .map(SysDeptDeviceTreeVO::new).collect(Collectors.toMap(SysDeptDeviceTreeVO::getId, x -> x));
        for (SysDeptDeviceVO device : devices) {
            if (Objects.isNull(device)) {
                continue;
            }
            String townCode = device.getTownCode();
            String countyId = device.getCountyId();
            String cityId = device.getCityId();
            String provinceId = device.getProvinceId();
            if (StringUtils.isNotEmpty(townCode) && areaMap.containsKey(townCode)) {
                device.setPid(townCode);
                if(!Constants.ONE_STR.equals(flag)){
                    areaMap.get(townCode).getDevices().add(device);
                }
            } else if (StringUtils.isNotEmpty(countyId) && areaMap.containsKey(countyId)) {
                device.setPid(countyId);
                if(!Constants.ONE_STR.equals(flag)){
                    areaMap.get(countyId).getDevices().add(device);
                }
            } else if (StringUtils.isNotEmpty(cityId) && areaMap.containsKey(cityId)) {
                device.setPid(cityId);
                if(!Constants.ONE_STR.equals(flag)){
                    areaMap.get(cityId).getDevices().add(device);
                }
            } else if (StringUtils.isNotEmpty(provinceId) && areaMap.containsKey(provinceId)) {
                device.setPid(provinceId);
                if(!Constants.ONE_STR.equals(flag)){
                    areaMap.get(provinceId).getDevices().add(device);
                }
            }
        }
        List<SysDeptDeviceTreeVO> list = new ArrayList<>(areaMap.values());
        list.sort(Comparator.comparing(SysDeptDeviceTreeVO::getId,String::compareTo));
        JSONArray jsonArray = TreeUtils.listToTree(list, "id", "pid", "children");
        List<SysDeptDeviceTreeVO> children = JSON.parseArray(jsonArray.toJSONString(), SysDeptDeviceTreeVO.class);
        //补全全国
        SysArea sysArea = new SysArea();
        sysArea.setAreaLevel(Constants.ONE_STR);
        List<SysArea> area = sysAreaMapper.getArea(sysArea);
        String rootName = DeviceConstants.TOWER_ROOT_AREA_NAME;
        if(!CollectionUtils.isEmpty(area)){
            rootName = area.get(Constants.ZERO).getAreaName();
        }
        return SysDeptDeviceTreeVO.completeTop(children,rootName);
    }

    private boolean isDirectCity(String code) {
        if (StringUtils.isNotEmpty(code) && (code.startsWith(BEIJING_PREFIX) || code.startsWith(TIANJIN_PREFIX)
                || code.startsWith(SHANGHAI_PREFIX) || code.startsWith(CHONGQING_PREFIX))) {
            return true;
        }
        return false;
    }


    /**
     * 登录用户为客户管理员时的权限
     * @param user
     * @return
     */
    public SysDeptDetailVO getDeptDeviceScopMgr(SysUser user) {
        SysDeptDetailVO vo = new SysDeptDetailVO();
        List<SysDeptDeviceVO> cameras = deptDeviceMapper.getCamerasScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        List<SysDeptDeviceVO> raders = deptDeviceMapper.getRadersScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        List<SysDeptDeviceVO> sensors = deptDeviceMapper.getSensorsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        List<SysDeptDeviceVO> satellites = deptDeviceMapper.getSatellitesScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        //add by liuxt 组织管理优化需求 start 20230612
        List<SysDeptDeviceVO> uavs = deptDeviceMapper.getUavsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        List<SysDeptDeviceVO> horns = deptDeviceMapper.getHornsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        //add by liuxt 组织管理优化需求 end 20230612
        List<SysDeptDeviceVO> LERs = deptDeviceMapper.getLERsScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        vo.setCameras(cameras);
        vo.setRadars(raders);
        vo.setSensors(sensors);
        vo.setSatellites(satellites);
        //add by liuxt 组织管理优化需求 start 20230612
        vo.setUavs(uavs);
        vo.setHorns(horns);
        //add by liuxt 组织管理优化需求 end 20230612
        vo.setLERs(LERs);
        List<SysDeptStreetVO> sysDeptStreetVOS = getDeptStreetScop(vo);
        vo.setDeptStreets(sysDeptStreetVOS);
        return vo;
    }


    @Override
    public List<SysDeptDeviceTreeVO> getCameraTreeNew() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<SysDeptDeviceVO> list = null;//pc端返回的list
        List<SysDeptDeviceTreeVO> appTreeList = new ArrayList<>();//app端返回的list
        if (Constants.ONE_STR.equalsIgnoreCase(user.getIsTenantAdmin()) || isMgr(user)) {
            list = deptDeviceMapper.getCamerasScop(user.getTenantId(), user.getIndustryCode(),user.getPlatformCode());
        } else {
            String deptId = user.getDeptId();
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            dept.setTenantId(user.getTenantId());
            dept.setIndustryCode(user.getIndustryCode());
            list = deptDeviceMapper.getCameras(dept);
        }
        if(!CollectionUtils.isEmpty(list)){
            appTreeList = buildNecessaryDeviceTree(list,Constants.ONE_STR);
            return appTreeList;
        }else{
            return new ArrayList<>();
        }
    }


    /**
     * 批量处理历史组织告警类型关系
     *
     * @param organAlarmTypeVO
     * @return 结果
     */
    @Override
    public void dealHisDeptAlarmTypeRel(OrganAlarmTypeVO organAlarmTypeVO) {
        long start = System.currentTimeMillis();
        //查询历史全部组织
        List<OrganAlarmTypeVO> allDeptList = deptMapper.selectAllDeptList();
        log.info(start + "allDeptList整体耗时=" + (System.currentTimeMillis() - start));
        //批量查询客户的告警来源
        findAlarmSource(allDeptList);
        log.info(start + "findAlarmSource整体耗时=" + (System.currentTimeMillis() - start));
        //得到每个客户的算法告警
        List<OrganAlarmTypeVO> alarmTypeVOList = deptMapper.findAlarmTypeByOrder(allDeptList);
        log.info(start + "alarmTypeVOList整体耗时=" + (System.currentTimeMillis() - start));
        //得到非算法告警（铁塔）
        List<OrganAlarmTypeVO> sourceAlarmTypeListTower = deptMapper.findAlarmTypeBySourceTower(allDeptList);
        Set<OrganAlarmTypeVO> sourceAlarmTypeListTowerNew = new HashSet<>();
        for (int i = 0; i < allDeptList.size(); i++) {
            List<String> alarmSources = allDeptList.get(i).getAlarmSources();
            String tenantId = allDeptList.get(i).getTenantId();
            String industryCode = allDeptList.get(i).getIndustryCode();
            for (int j = 0; j < sourceAlarmTypeListTower.size(); j++) {
                if(alarmSources.contains(sourceAlarmTypeListTower.get(j).getWarningSource())){
                    OrganAlarmTypeVO tempVO = new OrganAlarmTypeVO();
                    tempVO.setTenantId(tenantId);
                    tempVO.setIndustryCode(industryCode);
                    tempVO.setWarningSource(sourceAlarmTypeListTower.get(j).getWarningSource());
                    tempVO.setTypeValue(sourceAlarmTypeListTower.get(j).getTypeValue());
                    sourceAlarmTypeListTowerNew.add(tempVO);
                }
            }
        }
        log.info(start + "sourceAlarmTypeListTower整体耗时=" + (System.currentTimeMillis() - start));
        //得到每个客户的非算法告警（客户）
        List<OrganAlarmTypeVO> sourceAlarmTypeListTenant = deptMapper.findAlarmTypeBySourceTenant(allDeptList);
        log.info(start + "sourceAlarmTypeListTenant整体耗时=" + (System.currentTimeMillis() - start));
        //得到每个客户的第三方告警类型
        List<OrganAlarmTypeVO> thirdPartyAlarmTypeVOList = deptMapper.getThirdPartyAlarmTypes(allDeptList);
        log.info(start + "thirdPartyAlarmTypeVOList整体耗时=" + (System.currentTimeMillis() - start));
        //得到新人工上报告警类型(去掉自身的人工上报)和新一键告警告警类型(去掉自身的一键告警)
        List<OrganAlarmTypeVO> renGongAlarmTypeList = new ArrayList<>();
        List<OrganAlarmTypeVO> oneKeyAlarmTypeList = new ArrayList<>();
//        for (int i = 0; i < sourceAlarmTypeListTower.size(); i++) {
//            if(!WarningSourceEnum.ARTIFICIAL.getTypeValue().equals(sourceAlarmTypeListTower.get(i).getWarningSource()) &&
//                    !WarningSourceEnum.UAV_ALARM.getTypeValue().equals(sourceAlarmTypeListTower.get(i).getWarningSource()) &&
//                    !WarningSourceEnum.IOT_ALARM.getTypeValue().equals(sourceAlarmTypeListTower.get(i).getWarningSource()) &&
//                    !WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue().equals(sourceAlarmTypeListTower.get(i).getWarningSource()) ){
//                OrganAlarmTypeVO renGongOrganAlarmTypeVO = new OrganAlarmTypeVO();
//                renGongOrganAlarmTypeVO.setTypeValue(sourceAlarmTypeListTower.get(i).getTypeValue());
//                renGongOrganAlarmTypeVO.setWarningSource(Constants.TWO_STR);
//                renGongAlarmTypeList.add(renGongOrganAlarmTypeVO);
//                OrganAlarmTypeVO oneKeyOrganAlarmTypeVO = new OrganAlarmTypeVO();
//                oneKeyOrganAlarmTypeVO.setTypeValue(sourceAlarmTypeListTower.get(i).getTypeValue());
//                oneKeyOrganAlarmTypeVO.setWarningSource(Constants.THREE_STR);
//                oneKeyAlarmTypeList.add(oneKeyOrganAlarmTypeVO);
//            }
//        }
//
//        for (int i = 0; i < alarmTypeVOList.size(); i++) {
//                OrganAlarmTypeVO renGongOrganAlarmTypeVO = new OrganAlarmTypeVO();
//                renGongOrganAlarmTypeVO.setTenantId(alarmTypeVOList.get(i).getTenantId());
//                renGongOrganAlarmTypeVO.setIndustryCode(alarmTypeVOList.get(i).getIndustryCode());
//                //不取app_ver_code
////                renGongOrganAlarmTypeVO.setAppVerCode(sourceAlarmTypeListTenant.get(i).getAppVerCode());
//                renGongOrganAlarmTypeVO.setTypeValue(alarmTypeVOList.get(i).getTypeValue());
//                renGongOrganAlarmTypeVO.setWarningSource(Constants.TWO_STR);
//                renGongAlarmTypeList.add(renGongOrganAlarmTypeVO);
//
//                OrganAlarmTypeVO oneKeyOrganAlarmTypeVO = new OrganAlarmTypeVO();
//                oneKeyOrganAlarmTypeVO.setTenantId(alarmTypeVOList.get(i).getTenantId());
//                oneKeyOrganAlarmTypeVO.setIndustryCode(alarmTypeVOList.get(i).getIndustryCode());
//                //不取app_ver_code
////                oneKeyOrganAlarmTypeVO.setAppVerCode(sourceAlarmTypeListTenant.get(i).getAppVerCode());
//                oneKeyOrganAlarmTypeVO.setTypeValue(alarmTypeVOList.get(i).getTypeValue());
//                oneKeyOrganAlarmTypeVO.setWarningSource(Constants.THREE_STR);
//                oneKeyAlarmTypeList.add(oneKeyOrganAlarmTypeVO);
//        }
        List<OrganAlarmTypeVO> mergeList = new ArrayList<>();
        mergeList.addAll(alarmTypeVOList);
        mergeList.addAll(sourceAlarmTypeListTowerNew);
        mergeList.addAll(sourceAlarmTypeListTenant);
        mergeList.addAll(thirdPartyAlarmTypeVOList);
        if(!CollectionUtils.isEmpty(mergeList)){//拼一键上报和人工告警
            for (int i = 0; i < mergeList.size(); i++) {
                if(!WarningSourceEnum.ARTIFICIAL.getTypeValue().equals(mergeList.get(i).getWarningSource())) {
                    OrganAlarmTypeVO renGongOrganAlarmTypeVO = new OrganAlarmTypeVO();
                    renGongOrganAlarmTypeVO.setTenantId(mergeList.get(i).getTenantId());
                    renGongOrganAlarmTypeVO.setIndustryCode(mergeList.get(i).getIndustryCode());
                    renGongOrganAlarmTypeVO.setTypeValue(mergeList.get(i).getTypeValue());
                    renGongOrganAlarmTypeVO.setWarningSource(Constants.TWO_STR);
                    renGongAlarmTypeList.add(renGongOrganAlarmTypeVO);
                }
                if(!WarningSourceEnum.UAV_ALARM.getTypeValue().equals(mergeList.get(i).getWarningSource())) {
                    OrganAlarmTypeVO oneKeyOrganAlarmTypeVO = new OrganAlarmTypeVO();
                    oneKeyOrganAlarmTypeVO.setTenantId(mergeList.get(i).getTenantId());
                    oneKeyOrganAlarmTypeVO.setIndustryCode(mergeList.get(i).getIndustryCode());
                    oneKeyOrganAlarmTypeVO.setTypeValue(mergeList.get(i).getTypeValue());
                    oneKeyOrganAlarmTypeVO.setWarningSource(Constants.THREE_STR);
                    oneKeyAlarmTypeList.add(oneKeyOrganAlarmTypeVO);
                }
            }
        }
        mergeList.addAll(renGongAlarmTypeList);
        mergeList.addAll(oneKeyAlarmTypeList);
        mergeList =  mergeList.stream().distinct().collect(Collectors.toList());

        List<OrganAlarmTypeVO> finalList = new ArrayList<>();
        for (int i = 0; i < allDeptList.size(); i++) {
            OrganAlarmTypeVO deptVO = allDeptList.get(i);
            for (int j = 0; j < mergeList.size(); j++) {
                if(StringUtils.isEmpty(mergeList.get(j).getTenantId())
                        || (!StringUtils.isEmpty(mergeList.get(j).getTenantId())
                        && allDeptList.get(i).getTenantId().equals(mergeList.get(j).getTenantId())
                        && allDeptList.get(i).getIndustryCode().equals(mergeList.get(j).getIndustryCode()))){
                    OrganAlarmTypeVO mergeVO = mergeList.get(j);
                    OrganAlarmTypeVO tmpOrganAlarmTypeVO = new OrganAlarmTypeVO();
                    tmpOrganAlarmTypeVO.setTenantId(deptVO.getTenantId());
                    tmpOrganAlarmTypeVO.setDeptId(deptVO.getDeptId());
                    tmpOrganAlarmTypeVO.setWarningSource(mergeVO.getWarningSource());
                    tmpOrganAlarmTypeVO.setTypeValue(mergeVO.getTypeValue());
                    tmpOrganAlarmTypeVO.setCreator("init");
                    tmpOrganAlarmTypeVO.setIndustryCode(deptVO.getIndustryCode());
                    tmpOrganAlarmTypeVO.setAppVerCode(deptVO.getAppVerCode());
                    finalList.add(tmpOrganAlarmTypeVO);
                }
            }
        }
        try {
            log.info(start + "批量新增前整体耗时=" + (System.currentTimeMillis() - start));
            List<List<OrganAlarmTypeVO>> partitionList = Lists.partition(finalList, Constants.FIVE_THOUSAND);
            partitionList.forEach(item ->
                    deptMapper.insertOrganAlarmTypeBatch(item)
            );
        }catch (Exception e){
            log.error(Constants.BATCH_INSERT_FAIL,e);
        }
        log.info(start + "dealHisDeptAlarmTypeRel整体耗时=" + (System.currentTimeMillis() - start));
    }

    @Override
    public List<String> getDeptIdList(SysDept dept) {
        return deptMapper.getDeptIdList(dept);
    }


    public List<OrganAlarmTypeVO> findAlarmSource(List<OrganAlarmTypeVO> list) {

        //是否有摄像机告警来源
        List<OrganAlarmTypeVO> orderDevicesList = deptMapper.devByTenantIdCount(list);
        //是否有无人机告警来源
        List<OrganAlarmTypeVO> orderUavDevicesList = deptMapper.uavByTenantIdCount(list);
        //是否有物联设备告警来源
        List<OrganAlarmTypeVO> orderIotDevicesList = deptMapper.iotDevByTenantIdCount(list,String.valueOf(OrderConstants.ORDER_SENSOR_CODE));
        //是否有雷达设备告警来源
        List<OrganAlarmTypeVO> orderRadarDevicesList = deptMapper.iotDevByTenantIdCount(list,String.valueOf(OrderConstants.ORDER_RADAR_CODE));
        if(!CollectionUtils.isEmpty(list)){
            if (!CollectionUtils.isEmpty(orderDevicesList)){//判断是否有摄像机告警来源
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < orderDevicesList.size(); j++) {
                        if(list.get(i).getTenantId().equals(orderDevicesList.get(j).getTenantId())
                                && list.get(i).getIndustryCode().equals(orderDevicesList.get(j).getIndustryCode())
                                && orderDevicesList.get(j).getNum() > 0){
                            list.get(i).setCameraAlarmFlag(Constants.ONE_STR);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(orderUavDevicesList)){//判断是否有无人机告警来源
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < orderUavDevicesList.size(); j++) {
                        if(list.get(i).getTenantId().equals(orderUavDevicesList.get(j).getTenantId())
                                && list.get(i).getIndustryCode().equals(orderUavDevicesList.get(j).getIndustryCode())
                                && orderUavDevicesList.get(j).getNum() > 0){
                            list.get(i).setUavDeviceAlarmFlag(Constants.ONE_STR);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(orderIotDevicesList)){//判断是否有物联设备告警来源
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < orderIotDevicesList.size(); j++) {
                        if(list.get(i).getTenantId().equals(orderIotDevicesList.get(j).getTenantId())
                                && list.get(i).getIndustryCode().equals(orderIotDevicesList.get(j).getIndustryCode())
                                && orderIotDevicesList.get(j).getNum() > 0){
                            list.get(i).setIotAlarmFlag(Constants.ONE_STR);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(orderRadarDevicesList)){//判断是否有物联设备告警来源
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < orderRadarDevicesList.size(); j++) {
                        if(list.get(i).getTenantId().equals(orderRadarDevicesList.get(j).getTenantId())
                                && list.get(i).getIndustryCode().equals(orderRadarDevicesList.get(j).getIndustryCode())
                                && orderRadarDevicesList.get(j).getNum() > 0){
                            list.get(i).setRadarAlarmFlag(Constants.ONE_STR);
                        }
                    }
                }
            }

            for (int i = 0; i < list.size(); i++) {
                OrganAlarmTypeVO organAlarmTypeVO = list.get(i);
                List<String> alarmSources = new ArrayList<>();
                //人工上报和一件告警为固定告警来源
                alarmSources.add(WarningSourceEnum.ARTIFICIAL.getTypeValue());
                alarmSources.add(WarningSourceEnum.UAV_ALARM.getTypeValue());
                alarmSources.add(WarningSourceEnum.SATELLITE_ALARM.getTypeValue());
                if(Constants.ONE_STR.equals(organAlarmTypeVO.getCameraAlarmFlag())){
                    alarmSources.add(WarningSourceEnum.CAMERA_ALARM.getTypeValue());
                }
                if(Constants.ONE_STR.equals(organAlarmTypeVO.getUavDeviceAlarmFlag())){
                    alarmSources.add(WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue());
                }
                if(Constants.ONE_STR.equals(organAlarmTypeVO.getIotAlarmFlag())){
                    alarmSources.add(WarningSourceEnum.IOT_ALARM.getTypeValue());
                }
                if(Constants.ONE_STR.equals(organAlarmTypeVO.getRadarAlarmFlag())){
                    alarmSources.add(WarningSourceEnum.RADAR_ALARM.getTypeValue());
                }
                organAlarmTypeVO.setAlarmSources(alarmSources);
            }

        }

        return list;
    }
    private int saveSysDeptToMongoHis(String deptId) {
        List<SysDeptHisMongoDTO> infos = deptMapper.selectByDeptId(deptId);
        if (CollectionUtils.isEmpty(infos)) {
            return infos.size();
        }
        sysDeptHisMongoDAO.insertAll(infos, MongoConstants.MONGO_TABLE_NAME_SYS_DEPT_HIS, MongoDBType.PRIMARY);
        return deptMapper.physicalDeleteByDeptId(deptId);
    }

}
