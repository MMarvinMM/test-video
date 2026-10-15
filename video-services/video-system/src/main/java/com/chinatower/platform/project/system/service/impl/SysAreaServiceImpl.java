package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTiledArea;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.custom.entity.SatelliteInfoVO;
import com.chinatower.platform.project.system.constants.CacheConstants;
import com.chinatower.platform.project.system.domain.vo.SysDeptDeviceTreeVO;
import com.chinatower.platform.project.system.mapper.SysAreaMapper;
import com.chinatower.platform.project.system.mapper.SysAreaStreetMapper;
import com.chinatower.platform.project.system.service.ISysAreaService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.StreetTreeVO;
import com.chinatower.system.api.model.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.resp.SysAreaResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 行政区域Service业务层处理
 *
 * @author wangzic
 * @date 2019-12-17
 */
@Slf4j
@Service
public class SysAreaServiceImpl implements ISysAreaService {

    /**
     * 使用bean代理才能在同包类使用缓存
     */
    @Autowired
    private ISysAreaService sysAreaService;

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Autowired
    private SysAreaStreetMapper sysAreaStreetMapper;

    private static final int PARTITION_NUM = 2000;

    private static final String CHINA_CODE= "100000";

    private static final String OTHER_CODE = "99999";

    /**
     * 查询行政区域
     *
     * @param areaCode 行政区域ID
     * @return 行政区域
     */
    @Override
    public SysArea selectSysAreaById(String areaCode) {
        return sysAreaMapper.selectSysAreaById(areaCode);
    }

    @Override
    public Map<String, SysArea> getInfos(String[] codes) {
        if (codes == null || codes.length == 0){
            throw new CustomException("参数不能为空");
        }
        List<SysArea> sysAreas = sysAreaMapper.selectSysAreaCodes(codes);
        return StreamUtils.toMap(sysAreas, SysArea::getAreaCode, d -> d);
    }

    /**
     * 查询行政区域列表
     *
     * @param sysArea 行政区域
     * @return 行政区域
     */
    @Override
    public List<SysArea> selectSysAreaList(SysArea sysArea) {
        return sysAreaMapper.selectSysAreaList(sysArea);
    }

    /**
     * 新增行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    @Override
    public int insertSysArea(SysArea sysArea) {
        sysArea.setCreateTime(new Date());
        sysArea.setCreator(SecurityUtils.getUsername());
        sysArea.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        sysArea.setId(sysAreaMapper.selectMaxId() + 1L);
        sysArea.setCreateTime(new Date());
        sysArea.setCreator("interface");
        return sysAreaMapper.insertSysArea(sysArea);
    }

    /**
     * 修改行政区域
     *
     * @param sysArea 行政区域
     * @return 结果
     */
    @Override
    public int updateSysArea(SysArea sysArea) {
        sysArea.setUpdateTime(new Date());
        sysArea.setModifier(SecurityUtils.getUsername());

        return sysAreaMapper.updateSysArea(sysArea);
    }

    /**
     * 批量删除行政区域
     *
     * @param areaCodes 需要删除的行政区域ID
     * @return 结果
     */
    @Override
    public int deleteSysAreaByIds(String[] areaCodes) {
        return sysAreaMapper.deleteSysAreaByIds(areaCodes);
    }

    /**
     * 删除行政区域信息
     *
     * @param areaCode 行政区域ID
     * @return 结果
     */
    @Override
    @Deprecated
    public int deleteSysAreaById(String areaCode) {
        return sysAreaMapper.deleteSysAreaById(areaCode);
    }

    /**
     * 查询全部行政区划信息
     *
     * @return 行政区划信息
     */
    @Override
    @Cacheable(cacheNames = "selectAll",cacheManager = "caffeineCacheManager")
    public List<SysArea> selectAll() {
        List<SysArea> list = null;
        try {
            log.info("selectAll req remoteAddr : {}", InetAddress.getLocalHost());
            list = sysAreaMapper.selectSysAreaList(new SysArea());
        } catch (Exception e) {
            log.error("查询所有区域错误:{}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<SysArea> selectList(List<String> areaCodes) {
        return sysAreaMapper.selectSysAreaByIds(areaCodes);
    }

    /**
     * 查询行政区划树结构信息
     *
     * @return 行政区划树结构信息
     */
    @Override
    public List<TreeSelect> selectTreeSelect() {
        return selectTreeSelectNew();
    }

    /**
     * 查询行政区划树结构信息
     *
     * @return 行政区划树结构信息
     */
    @Override
    public List<TreeSelect> selectTreeSelectNew() {
        List<TreeSelect> tree = new ArrayList<>();
        List<SysArea> list = sysAreaService.selectAll();
        if (CollUtil.isEmpty(list)) {
            return tree;
        }
        for (SysArea item : list) {
            if (Objects.isNull(item.getAreaCode()) || Objects.isNull(item.getAreaParentCode())) {
                log.error("sys_area数据异常->{}", JSON.toJSONString(item));
                continue;
            }
            tree.add(new TreeSelect(item.getAreaCode(), item.getAreaName(), item.getAreaParentCode(), item.getDeptId()));
        }
        return TreeUtils.listToTreeSelect(tree);
    }

    @Override
    public String selectDeptById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CustomException("区域编码不能为空");
        }
        SysArea area = sysAreaMapper.selectSysAreaById(id);
        if (ObjectUtil.isNotNull(area)) {
            return area.getDeptId();
        } else {
            throw new CustomException("编码为'" + id + "'的区域信息不存在");
        }
    }

    @Override
    public String selectDeptById(String provinceId, String cityId) {
        String areaId = null;
        if (StrUtil.isNotBlank(provinceId)) {
            areaId = provinceId;
        }
        if (StrUtil.isNotBlank(cityId)) {
            areaId = cityId;
        }
        if (StrUtil.isBlank(areaId)) {
            log.warn("本次通过省份或地市区域编码查询所属部门信息失败：省份和地市编码都为空，默认查询[100000]所属部门");
            areaId = CHINA_CODE;
        }
        SysArea area = selectSysAreaById(areaId);
        if (ObjectUtil.isNotNull(area)) {
            return area.getDeptId();
        } else {
            log.warn("本次通过省份或地市区域编码查询所属部门信息失败：区域编码[{}]信息不存在，默认返回该区域编码为部门编码", areaId);
            return areaId;
        }

    }

    @Override
    public List<String> selectAreaNamesByIds(List<String> list) {
        return sysAreaMapper.selectAreaNamesByIds(list);
    }

    @Override
    public Map<String, String> selectAllAreaWithDeptRel() {
        List<SysArea> areaList = sysAreaService.selectAll();
        Map<String, String> map = new HashMap<>();
        areaList.forEach(sysArea -> {
            map.put(sysArea.getAreaCode(), sysArea.getDeptId());
        });
        return map;
    }

    @Override
    public Map<String, String> selectAreaWithNameRel(List<String> areaCodes) {
        Map<String, String> areaMap = new HashMap<>();
        Stream<SysArea> stream = sysAreaService.selectAll().stream();
        //如果大并发对内存有影响，后续可优化成mysql查询
        if (!CollectionUtils.isEmpty(areaCodes)) {
            HashSet<String> set = new HashSet<>(areaCodes);
            stream = stream.filter(x -> set.contains(x.getAreaCode()));
        }
        stream.forEach(area -> {
            areaMap.put(area.getAreaCode(), area.getAreaName());
        });
        return areaMap;
    }

    @Override
    public Map<String, SysArea> selectAreaMap(List<String> areaCodes) {
        Map<String, SysArea> map = new HashMap<>();
        Stream<SysArea> stream = sysAreaService.selectAll().stream();
        if (!CollectionUtils.isEmpty(areaCodes)) {
            HashSet<String> set = new HashSet<>(areaCodes);
            stream = stream.filter(x -> set.contains(x.getAreaCode()));
        }
        stream.forEach(
                x -> {
                    map.put(x.getAreaCode(), x);
                }
        );
        return map;
    }

    @Override
    public List<SysTiledArea> querySysTiledArea(Map<String, Object> params) {
        return sysAreaMapper.querySysTiledArea(params);
    }

    @Override
    public List<SysArea> getArea(SysArea sysArea) {
        return sysAreaMapper.getArea(sysArea);
    }


    @Override
    public List<SysArea> getAllArea(SysArea sysArea) {
        log.info("查询区域方法");
        Set<String> set = new HashSet<>(Arrays.asList("1", "2", "3", "4"));
        List<SysArea> list = sysAreaService.selectAll().stream().filter(x -> set.contains(x.getAreaLevel())).collect(Collectors.toList());
        return list;
    }

    @Override
    public SysCapitalVO getSysCapital(SysCapitalVO vo) {
        return sysAreaMapper.getSysCapital(vo);
    }

    @Override
    public SysArea getCenterLonLat(String code) {
        return sysAreaMapper.getCenterLonLat(code);
    }

    @Override
    public List<SysDeptDeviceTreeVO> getManagementAreaTreeByTenantId(String tenantId) {
        List<SysDeptDeviceTreeVO> appTreeList = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<SysDeptDeviceVO> list = sysAreaMapper.getManagementAreaByTenantId(tenantId,user.getIndustryCode());
        if(!CollectionUtils.isEmpty(list)){
            appTreeList = buildNecessaryDeviceTree(list, com.chinatower.platform.project.sysbusiness.common.Constants.ONE_STR);//flag为1 代表不返回devices数据
        }
        return appTreeList;
    }

    @Override
    public List<StreetTreeVO> getAllStreetList(StreetTreeVO vo) {
        List<StreetTreeVO> list = sysAreaStreetMapper.getAllStreetList(vo);
        return list;
    }

    /**
     * @param areaNames 入参
     * @return com.chinatower.common.entity.Result<java.util.List < SysArea>>
     * @Description: 根据区划名称查询信息
     * @Author: TangJinBo
     * @Date: 2022/12/14 11:34
     */
    @Override
    public List<SysArea> selectByAreaName(List<String> areaNames) {
        return sysAreaMapper.selectByAreaName(areaNames);
    }

    /**
     * @return com.chinatower.common.entity.Result<java.util.List < com.chinatower.common.entity.TreeSelect>>
     * @Description: 获取部门下拉树列表（只取省市区3级）
     * @Author: TangJinBo
     * @Date: 2022/12/19 16:43
     */
    @Override
    public List<TreeSelect> treeArea() {
        // 查询省市区3级区域信息
        Set<String> set = new HashSet<>(Arrays.asList("2", "3", "4"));
        List<SysArea> list = sysAreaService.selectAll().stream().filter(x -> set.contains(x.getAreaLevel())).collect(Collectors.toList());

        List<TreeSelect> tree = list.stream().filter(Objects::nonNull)
                .map(e -> new TreeSelect(e.getAreaCode(), e.getAreaName(), e.getAreaParentCode())).collect(Collectors.toList());

        List<TreeSelect> treeSelectList = TreeUtils.listToTreeSelect(tree);
        TreeSelect china = new TreeSelect(CHINA_CODE, "全国", "1");
        treeSelectList.add(0, china);
        return treeSelectList;
    }

    /**
     * caching注解是单节点的，多实例存在问题，使用redis发布订阅配合删除。
     */
    @Override
    @Caching(evict={
            @CacheEvict(cacheNames = "selectAll",cacheManager = "caffeineCacheManager"),
    })
    public void removeCaffeineCache() {
        log.info("更新缓存数据");
    }

    @Override
    public List<SysArea> getAreaTreeList() {
        List<SysArea> areaTreeList = sysAreaMapper.getAreaTreeList();
        List<SysArea> sysAreas = streamToTree(areaTreeList,CHINA_CODE);
        return sysAreas;
    }
    public static List<SysArea> streamToTree(List<SysArea> treeList, String parentId) {
        List<SysArea> list = treeList.stream()
                // 过滤父节点
                .filter(parent -> parent.getAreaParentCode().equals(parentId))
                // 把父节点children递归赋值成为子节点
                .peek(child -> child.setChildren(streamToTree(treeList,child.getAreaCode()))).collect(Collectors.toList());
        return list;
    }



//    /**
//     * 耗时太多，放弃此方案，采用caffeine
//     * @param areaCodes 如果是空的查询全量
//     * @return
//     */
//    public Map<String, SysArea> getAreaRedisMap(List<String> areaCodes) {
//        StopWatch stopWatch =new StopWatch("test");
//        stopWatch.start("groupAreaFunc---------");
//        Map<String, SysArea> resultMap = Maps.newHashMap();
//        Map<Integer, List<Object>> groupAreaCodeMap = null;
//        areaCodes = areaCodes.stream().filter(Objects::nonNull).collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(areaCodes)) {
//            //参数分组
//            groupAreaCodeMap = groupAreaFunc.apply(areaCodes).apply(modFunc);
//        }
//        stopWatch.stop();
//        stopWatch.start("initAreaCache---------");
//        //因初始化时查全表，初始化后直接返回map；如果没有走初始化，则返回空，走下面逻辑获取。
//        resultMap.putAll(initAreaCache(groupAreaCodeMap == null ? Sets.newHashSet(0, 1, 2, 3, 4) : groupAreaCodeMap.keySet()));
//        stopWatch.stop();
//        stopWatch.start("find---------");
//        if (MapUtils.isNotEmpty(groupAreaCodeMap)) {
//            if (MapUtils.isEmpty(resultMap)) {
//                //没查db，有参数，走缓存查询
//                groupAreaCodeMap.forEach((x, y) -> {
//                    List<Object> list = RedisUtil.hMultiGet(String.format(CacheConstants.CACHE_AREA_, x), y, RedisDBType.PRIMARY);
//                    if (!CollectionUtils.isEmpty(list)) {
//                        for (int i = 0; i < list.size(); i++) {
//                            resultMap.put((String) y.get(i), JSON.parseObject((String) list.get(i), SysArea.class));
//                        }
//                    }
//                });
//            } else {
//                //查过db，有参数，从db的map中查
//                Map<String, SysArea> newMap = Maps.newHashMap();
//                for (String areaCode : areaCodes) {
//                    newMap.put(areaCode, resultMap.get(areaCode));
//                }
//                return newMap;
//            }
//        } else if (MapUtils.isEmpty(resultMap)) {
//            StopWatch stopWatch1 =new StopWatch();
//            //没查db，没参数，走缓存
//            for (int i = com.chinatower.platform.constants.Constants.ZERO_NUM; i < Constants.FIVE_NUM; i++) {
//                String key = String.format(CacheConstants.CACHE_AREA_, i);
//                stopWatch1.start("hGetAll");
//                Map<Object, Object> objectObjectMap = RedisUtil.hGetAll(key, RedisDBType.PRIMARY);
//                stopWatch1.stop();
//                stopWatch1.start("putAll");
//                resultMap.putAll(objectObjectMap.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> JSON.parseObject((String) e.getValue(), SysArea.class))));
//                stopWatch1.stop();
//            }
//            System.out.println(stopWatch1.getTotalTimeMillis());
//            System.out.println(stopWatch1.prettyPrint());
//            System.out.println("------------------------------------!!!!");
//        }
//
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
////        System.out.println(stopWatch.prettyPrint());
//        //查db,没有参数，直接返回
//        return resultMap;
//    }

    Function<List<String>, Function<Function<Object, Integer>, Map<Integer, List<Object>>>> groupAreaFunc = x -> y -> x.stream().filter(NumberUtils::isParsable).collect(Collectors.groupingBy(y));
    Function<Object, Integer> modFunc = x -> String.valueOf(x.hashCode()).hashCode() % Constants.FIVE_NUM;

    private Map<String, SysArea> initAreaCache(Set<Integer> keys) {
        Map<String, SysArea> map = new HashMap();
        Set<Integer> needQuerySet = Sets.newHashSet();
        keys.forEach(index -> {
            String key = String.format(CacheConstants.CACHE_AREA_, index);
//            RedisUtil.delete(key,RedisDBType.PRIMARY);
            if (!RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                needQuerySet.add(index);
            }
        });

        if (!CollectionUtils.isEmpty(needQuerySet)) {
            long s=System.currentTimeMillis();
            List<SysArea> treeVOList = sysAreaMapper.getAllAreaInfo();
            System.out.println("sysAreaMapper.getAllAreaInfo cost:"+(System.currentTimeMillis()-s));
            if (!CollectionUtils.isEmpty(treeVOList)) {
                List<List<SysArea>> partition = ListUtils.partition(treeVOList, PARTITION_NUM);
                partition.forEach(list -> {
                            insertRedis(list, map, needQuerySet);
                        }
                );
            }
            for (Integer index : needQuerySet) {
                String key = String.format(CacheConstants.CACHE_AREA_, index);
                RedisUtil.expire(key, com.chinatower.platform.constants.CacheConstants.MONTH_3, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
        return map;
    }

    private void insertRedis(List<SysArea> list, Map<String, SysArea> map, Set<Integer> needQuerySet) {
        Map<Integer, Map<String, Object>> collect = list.stream()
                .filter(
                        x -> StringUtils.isNotEmpty(x.getAreaCode()) && NumberUtils.isParsable(x.getAreaCode()) && needQuerySet.contains(modFunc.apply(x.getAreaCode()))
                ).collect(
                        Collectors.groupingBy((x) -> modFunc.apply(x.getAreaCode()), Collectors.toMap(SysArea::getAreaCode, x -> JSON.toJSONString(x)))
                );
        collect.forEach(
                (x, y) -> {
                    String key = String.format(CacheConstants.CACHE_AREA_, x);
                    RedisUtil.hPutAll(key, y, RedisDBType.PRIMARY);
                    map.putAll(y.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> JSON.parseObject((String) e.getValue(), SysArea.class))));
                }
        );
    }

    @Override
    public List<TreeSelect> getAreaTreeWithTownList(boolean otherFlag) {
        // 查询省市区3级区域信息
        Set<String> set = new HashSet<>(Arrays.asList("2", "3", "4", "5"));
        List<SysArea> list = sysAreaService.selectAll().stream().filter(x -> set.contains(x.getAreaLevel())).collect(Collectors.toList());
        List<TreeSelect> tree = list.stream().filter(Objects::nonNull)
                .map(e -> new TreeSelect(e.getAreaCode(), e.getAreaName(), e.getAreaParentCode())).collect(Collectors.toList());
        List<TreeSelect> treeSelectList = TreeUtils.listToTreeSelect(tree);
        if (otherFlag && !CollectionUtils.isEmpty(treeSelectList)) {
            treeSelectList.forEach(province -> {
                if (!CollectionUtils.isEmpty(province.getChildren())) {
                    province.getChildren().forEach(city -> {
                        if (!CollectionUtils.isEmpty(city.getChildren())) {
                            city.getChildren().forEach(county -> {
                                TreeSelect treeSelect = new TreeSelect(OTHER_CODE, "其他", county.getId());
                                if (CollectionUtils.isEmpty(county.getChildren())) {
                                    List<TreeSelect> childrenList = Lists.newArrayList();
                                    childrenList.add(treeSelect);
                                    county.setChildren(childrenList);
                                }
                                else {
                                    county.getChildren().add(treeSelect);
                                }
                            });
                        }
                    });
                }
            });
        }
        TreeSelect china = new TreeSelect(CHINA_CODE, "全国", "1");
        treeSelectList.add(0, china);
        return treeSelectList;
    }

    @Override
    public List<SysArea> selectSysAreas(List<SatelliteInfoVO> list) {
        return sysAreaMapper.selectSysAreas(list);
    }


    private List<SysDeptDeviceTreeVO> buildNecessaryDeviceTree(List<SysDeptDeviceVO> devices,String flag) {
        List<String> areaCodes = devices.stream().filter(Objects::nonNull)
                .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountyId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getTownCode(), org.apache.commons.lang3.StringUtils.EMPTY)))
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
            if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(townCode) && areaMap.containsKey(townCode)) {
                device.setPid(townCode);
                if(!com.chinatower.platform.project.sysbusiness.common.Constants.ONE_STR.equals(flag)){
                    areaMap.get(townCode).getDevices().add(device);
                }
            } else if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(countyId) && areaMap.containsKey(countyId)) {
                device.setPid(countyId);
                if(!com.chinatower.platform.project.sysbusiness.common.Constants.ONE_STR.equals(flag)){
                    areaMap.get(countyId).getDevices().add(device);
                }
            } else if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(cityId) && areaMap.containsKey(cityId)) {
                device.setPid(cityId);
                if(!com.chinatower.platform.project.sysbusiness.common.Constants.ONE_STR.equals(flag)){
                    areaMap.get(cityId).getDevices().add(device);
                }
            } else if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(provinceId) && areaMap.containsKey(provinceId)) {
                device.setPid(provinceId);
                if(!com.chinatower.platform.project.sysbusiness.common.Constants.ONE_STR.equals(flag)){
                    areaMap.get(provinceId).getDevices().add(device);
                }
            }
        }
        List<SysDeptDeviceTreeVO> list = new ArrayList<>(areaMap.values());
        list.sort(Comparator.comparing(SysDeptDeviceTreeVO::getId,String::compareTo));
        JSONArray jsonArray = TreeUtils.listToTree(list, "id", "pid", "children");
        List<SysDeptDeviceTreeVO> children = JSON.parseArray(jsonArray.toJSONString(), SysDeptDeviceTreeVO.class);
        //补全全国
        return SysDeptDeviceTreeVO.completeTop(children);
    }


}
