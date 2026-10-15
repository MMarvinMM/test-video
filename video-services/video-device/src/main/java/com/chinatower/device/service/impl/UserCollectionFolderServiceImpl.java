package com.chinatower.device.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.Constants;
import com.chinatower.device.dto.ResourceConfVO;
import com.chinatower.device.dto.UserCollectionFolderDTO;
import com.chinatower.device.dto.UserCollectionFolderVO;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.UserCollectionFolderMapper;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.service.IIndustryDeviceService;
import com.chinatower.device.service.IRecorderDeviceService;
import com.chinatower.device.service.IResourceConfService;
import com.chinatower.device.service.IUserCollectionFolderService;
import com.chinatower.industry.dto.DeviceByConditionDTO;
import com.chinatower.industry.vo.DevicesByConditionVO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.RecorderDeviceDTO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 用户收藏夹相关
 */
@Service
@Slf4j
public class UserCollectionFolderServiceImpl implements IUserCollectionFolderService {

    @Resource
    private UserCollectionFolderMapper userCollectionFolderMapper;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    @Autowired
    private IHornDeviceRepository hornDeviceRepository;

    @Autowired
    private IRecorderDeviceService recorderDeviceService;

    @Autowired
    private IIndustryDeviceService iIndustryDeviceService;

    @Autowired
    private IResourceConfService resourceConfService;

    /**
     * 查询收藏列表
     * @param userCollectionFolderDTO
     */
    @Override
    public List<UserCollectionFolderVO> getCollectionFolderList(UserCollectionFolderDTO userCollectionFolderDTO) {
        //结果集
        List<UserCollectionFolderVO> resultList = Lists.newArrayList();
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        //获取登录信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(loginUser.getUserName());
        //查询指定的设摄像机、喇叭、无人机、雷达、物联、喇叭、资源
        List<String> collectTypeList = Arrays.asList(Constants.FIVE_FLAG, Constants.SEVEN_FLAG,
                Constants.EIGHT_FLAG, Constants.TEN_FLAG, Constants.ELEVEN_FLAG, Constants.TWELVE_FLAG, Constants.FOURTEEN_FLAG);
        //1.查询该用户收藏的所有设备和资源
        List<UserDeviceCollectionQueryVO> userDeviceCollectionQueryVOList = userCollectionFolderMapper.getCollectList(user.getUserId(), collectTypeList);
        //2.查询该用户的收藏夹
        UserCollectionFolderDTO param = new UserCollectionFolderDTO();
        param.setUserId(user.getUserId());
        //用户已经有的收藏夹
        List<UserCollectionFolderDTO> collectionFolderDTOList = userCollectionFolderMapper.getListByCondition(param);

        //3.根据类型分组，提出对应的设备编码进行查询
        if (!CollectionUtils.isEmpty(userDeviceCollectionQueryVOList)) {
            //摄像机、无人机、雷达、喇叭、物联、执法记录仪、资源七种类型
            Map<String, List<UserDeviceCollectionQueryVO>> userDeviceMap = userDeviceCollectionQueryVOList.stream().collect(Collectors.groupingBy(UserDeviceCollectionQueryVO::getCollObjType));

            Map<Function, List<UserDeviceCollectionQueryVO>> paramMap = new ConcurrentHashMap<>();
            List<Function> listFunc = Lists.newArrayList();
            //根据收藏不同类型的设备各自去查询设备详情信息
            userDeviceMap.forEach((k,v) -> {
                switch (k) {
                    //摄像机
                    case "5":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> deviceFunc = this::queryDevice;
                        paramMap.put(deviceFunc, v);
                        listFunc.add(deviceFunc);
                        break;
                    //物联
                    case "7":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> iotDeviceFunc = this::queryIotDevice;
                        paramMap.put(iotDeviceFunc, v);
                        listFunc.add(iotDeviceFunc);
                        break;
                    //雷达
                    case "8":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> radarDeviceFunc = this::queryRadarDevice;
                        paramMap.put(radarDeviceFunc, v);
                        listFunc.add(radarDeviceFunc);
                        break;
                    //喇叭
                    case "10":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> hornDeviceFunc = this::queryHornDevice;
                        paramMap.put(hornDeviceFunc, v);
                        listFunc.add(hornDeviceFunc);
                        break;
                    //无人机
                    case "11":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> uavDeviceFunc = this::queryUavDevice;
                        paramMap.put(uavDeviceFunc, v);
                        listFunc.add(uavDeviceFunc);
                        break;
                    //执法记录仪
                    case "12":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> recorderDeviceFunc = this::queryRecorderDevice;
                        paramMap.put(recorderDeviceFunc, v);
                        listFunc.add(recorderDeviceFunc);
                        break;
                    //资源
                    case "14":
                        Function<List<UserDeviceCollectionQueryVO>, List<UserCollectionFolderVO>> resourceFunc = this::queryResource;
                        paramMap.put(resourceFunc, v);
                        listFunc.add(resourceFunc);
                        break;
                    default:
                }
            });
            //开启多线程同时查询收藏的设备、资源数据
            List<Object> list = ThreadParallelUtil.run(listFunc, f->{
                List<UserDeviceCollectionQueryVO> vo =  paramMap.get(f);
                return f.apply(vo);
            }, ThreadParallelUtil.ConfigFactory.AROUND_ANALYSIS);
            //处理查询后的资源、设备数据
            if (!CollectionUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    List<UserCollectionFolderVO> collList = (List<UserCollectionFolderVO>) list.get(i);
                    if (!CollectionUtils.isEmpty(collList)) {
                        folderVOList.addAll(collList);
                    }
                }
            }
        }
        //组装返回数据
        collectionFolderDTOList.forEach(item -> {
            UserCollectionFolderVO userCollectionFolderVO = new UserCollectionFolderVO();
            List<UserCollectionFolderVO> childFolderList = Lists.newArrayList();
            userCollectionFolderVO.setCollObjName(item.getFolderName());
            userCollectionFolderVO.setType(Constants.ONE);
            userCollectionFolderVO.setSortNo(item.getSortNo());
            userCollectionFolderVO.setCollectFolderId(item.getId());
            userCollectionFolderVO.setTreeId(item.getId() + "_" + Constants.ONE);
            //获取该收藏夹下所有的设备
            List<UserDeviceCollectionQueryVO> collectionQueryVOList = userDeviceCollectionQueryVOList.stream().filter(queryVO -> Objects.equals(queryVO.getCollectFolderId(), item.getId())).collect(Collectors.toList());
            //排除该行业没有订购的设备信息
            collectionQueryVOList.forEach(queryVO -> {
                Iterator<UserCollectionFolderVO> iterator = folderVOList.iterator();
                //筛选出设备
                while (iterator.hasNext()) {
                    UserCollectionFolderVO folderVO = iterator.next();
                    if (StringUtils.equals(queryVO.getCollObjCode(), folderVO.getCollObjCode()) && StringUtils.equals(queryVO.getCollObjType(), folderVO.getCollObjType())) {
                        UserCollectionFolderVO vo = new UserCollectionFolderVO();
                        BeanUtil.copyProperties(folderVO, vo);
                        vo.setUserDeviceCollId(queryVO.getUserDeviceCollId());
                        vo.setSortNo(queryVO.getSortNo());
                        vo.setType(Constants.TWO_FLAG);
                        vo.setTreeId(queryVO.getUserDeviceCollId() + "_" + Constants.TWO_FLAG);
                        //别名不为空显示别名
                        if (StringUtils.isNotBlank(queryVO.getAliasName())) {
                            vo.setCollObjName(queryVO.getAliasName());
                        }
                        childFolderList.add(vo);
                        iterator.remove();
                    }

                }
            });
            //根据排序序号-id升序排列
            List<UserCollectionFolderVO> sortChildFolderList = childFolderList.stream()
                    .sorted(
                            Comparator.comparing(UserCollectionFolderVO::getSortNo, Comparator.nullsFirst(Integer::compareTo))
                                    .thenComparing(UserCollectionFolderVO::getUserDeviceCollId, Comparator.nullsLast(Long::compareTo))
                    )
                    .collect(Collectors.toList());
            userCollectionFolderVO.setChildren(sortChildFolderList);
            resultList.add(userCollectionFolderVO);
        });
        //将剩下没有收藏的设备平铺展示
        Iterator<UserCollectionFolderVO> deviceIterator = folderVOList.iterator();
        List<UserCollectionFolderVO> collList = Lists.newArrayList();
        while (deviceIterator.hasNext()) {
            UserCollectionFolderVO folderVO = deviceIterator.next();
            UserDeviceCollectionQueryVO collectionQueryVO = userDeviceCollectionQueryVOList.stream().filter(queryVO ->
                    StringUtils.equals(queryVO.getCollObjCode(), folderVO.getCollObjCode())
                        && StringUtils.equals(queryVO.getCollObjType(), folderVO.getCollObjType())).findFirst().get();
            if (StringUtils.isNotBlank(collectionQueryVO.getAliasName())) {
                folderVO.setCollObjName(collectionQueryVO.getAliasName());
            }
            folderVO.setTreeId(collectionQueryVO.getUserDeviceCollId() + "_" + Constants.TWO_FLAG);
            folderVO.setSortNo(collectionQueryVO.getSortNo());
            folderVO.setType(Constants.TWO_FLAG);
            folderVO.setUserDeviceCollId(collectionQueryVO.getUserDeviceCollId());
            collList.add(folderVO);
        }
        resultList.addAll(collList);
        //排序, 根据设备类型-设备编码排序
        return resultList.stream()
                .sorted(
                        Comparator.comparing(UserCollectionFolderVO::getSortNo, Comparator.nullsLast(Integer::compareTo))
                                .thenComparing(UserCollectionFolderVO::getCollObjType, Comparator.nullsFirst(String::compareTo))
                                .thenComparing(UserCollectionFolderVO::getCollObjCode, Comparator.nullsFirst(String::compareTo))
                )
                .collect(Collectors.toList());
    }

    /**
     * 查询摄像机设备
     */
    private List<UserCollectionFolderVO> queryDevice(List<UserDeviceCollectionQueryVO> list) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> deviceCodeList = getCollObjCodeList(list);
        //根据指定的编码查询设备信息
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            DevicesByConditionVO devicesByConditionVO = new DevicesByConditionVO();
            devicesByConditionVO.setDeviceCodeList(deviceCodeList);
            List<DeviceByConditionDTO> deviceByConditionDTOList = iIndustryDeviceService.queryDeviceListByCondition(devicesByConditionVO);
            deviceByConditionDTOList.forEach(item -> {
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getDeviceCode(), Constants.FIVE_FLAG, item.getDeviceName(), item.getLongitude(), item.getLatitude(), String.valueOf(item.getDeviceStatus()));
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;
    }

    /**
     * 查询无人机设备
     */
    private List<UserCollectionFolderVO> queryUavDevice(List<UserDeviceCollectionQueryVO> list) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> uavDeviceCodeList = getCollObjCodeList(list);
        //根据指定的编码查询设备信息
        if (!CollectionUtils.isEmpty(uavDeviceCodeList)) {
            UavDeviceDTO uavDeviceDTO = new UavDeviceDTO();
            uavDeviceDTO.setDeviceCodeList(uavDeviceCodeList);
            uavDeviceDTO.setOrderStatus(Constants.ZERO_NUM);
            List<UavDeviceQueryVO> uavDeviceQueryVOList = uavDeviceService.queryUavListByCondition(uavDeviceDTO);
            uavDeviceQueryVOList.forEach(item -> {
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getDeviceCode(), Constants.ELEVEN_FLAG, item.getDeviceName(), item.getLongitude(), item.getLatitude(), String.valueOf(item.getDeviceStatus()));
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;
    }

    /**
     * 查询物联设备
     * @param list
     * @return
     */
    private List<UserCollectionFolderVO> queryIotDevice(List<UserDeviceCollectionQueryVO> list) {
        return getIotDeviceList(list, Constants.TWO_NUM, Constants.SEVEN_FLAG);
    }

    /**
     * 查询雷达设备
     * @param list
     * @return
     */
    private List<UserCollectionFolderVO> queryRadarDevice(List<UserDeviceCollectionQueryVO> list) {
        return getIotDeviceList(list, Constants.ONE_NUM, Constants.EIGHT_FLAG);
    }

    /**
     * 查询喇叭设备
     * @param list
     * @return
     */
    private List<UserCollectionFolderVO> queryHornDevice(List<UserDeviceCollectionQueryVO> list) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> hornDeviceCodeList = getCollObjCodeList(list);
        if (!CollectionUtils.isEmpty(hornDeviceCodeList)) {
            HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
            List<String> onlineNum = Arrays.asList(Constants.ONE, Constants.TWO_FLAG, Constants.THREE_FLAG);
            hornDeviceDTO.setDeviceCodeList(hornDeviceCodeList);
            List<HornDeviceVo> hornDeviceVoList = hornDeviceRepository.queryHornDeviceListByCondition(hornDeviceDTO);
            hornDeviceVoList.forEach(item -> {
                String deviceStatus = onlineNum.contains(item.getDeviceStatus()) ? Constants.ZERO : Constants.ONE;
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getDeviceCode(), Constants.TEN_FLAG, item.getDeviceName(), item.getLongitude(), item.getLatitude(), deviceStatus);
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;
    }

    /**
     * 查询执法记录仪
     * @param list
     * @return
     */
    private List<UserCollectionFolderVO> queryRecorderDevice(List<UserDeviceCollectionQueryVO> list) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> recorderDeviceCodeList = getCollObjCodeList(list);
        if (!CollectionUtils.isEmpty(recorderDeviceCodeList)) {
            RecorderDeviceVO recorderDeviceVO = new RecorderDeviceVO();
            recorderDeviceVO.setDeviceCodeList(recorderDeviceCodeList);
            recorderDeviceVO.setOrderStatus(Constants.ZERO_NUM);
            List<RecorderDeviceDTO> recorderDeviceDTOList = recorderDeviceService.queryRecorderListByCondition(recorderDeviceVO);
            recorderDeviceDTOList.forEach(item -> {
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getDeviceCode(), Constants.TWELVE_FLAG, item.getDeviceName(), item.getLongitude(), item.getLatitude(), String.valueOf(item.getDeviceStatus()));
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;
    }

    /**
     * 查询资源
     * @return
     */
    private List<UserCollectionFolderVO> queryResource(List<UserDeviceCollectionQueryVO> list) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> resourceIdList = getCollObjCodeList(list);
        if (!CollectionUtils.isEmpty(resourceIdList)) {
            ResourceConfVO resourceConfVO = new ResourceConfVO();
            resourceConfVO.setResourceIdList(resourceIdList);
            List<ResourceConfVO> resourceConfVOList = resourceConfService.selectResourceList(resourceConfVO);
            resourceConfVOList.forEach(item -> {
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getResourceId(), Constants.FOURTEEN_FLAG, item.getResourceName(), item.getLongitude(), item.getLatitude(), String.valueOf(item.getStatus()));
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;

    }

    /**
     * 获取收藏的编码
     * @param list
     * @return
     */
    private List<String> getCollObjCodeList(List<UserDeviceCollectionQueryVO> list) {
        List<String> collObjCodeList = list.stream().filter(item -> StringUtils.isNotBlank(item.getCollObjCode())).map(UserDeviceCollectionQueryVO::getCollObjCode).distinct().collect(Collectors.toList());
        return collObjCodeList;
    }

    /**
     * 组装查询到的设备信息
     * @return
     */
    private UserCollectionFolderVO getUserCollectionFolderVO(String collObjCode, String collObjType, String collObjName,
                                                             String longitude, String latitude, String status) {
        UserCollectionFolderVO vo = new UserCollectionFolderVO();
        vo.setCollObjCode(collObjCode);
        vo.setCollObjType(collObjType);
        vo.setCollObjName(collObjName);
        vo.setLatitude(latitude);
        vo.setLongitude(longitude);
        vo.setStatus(status);
        return vo;
    }

    /**
     * 获取物联设备雷达的数据
     * @param list
     * @param categoryCode
     * @param collObjType
     * @return
     */
    private List<UserCollectionFolderVO> getIotDeviceList(List<UserDeviceCollectionQueryVO> list, int categoryCode, String collObjType) {
        List<UserCollectionFolderVO> folderVOList = Lists.newArrayList();
        List<String> iotDeviceCodeList = getCollObjCodeList(list);
        if (!CollectionUtils.isEmpty(iotDeviceCodeList)) {
            IotDeviceVO deviceInfoVO = new IotDeviceVO();
            deviceInfoVO.setDeviceCodeList(iotDeviceCodeList);
            deviceInfoVO.setOrderStatus(Constants.ONE);
            deviceInfoVO.setCategoryCode(categoryCode);
            List<IotDeviceVO> iotDeviceVOList = iotDeviceRepository.queryIotRadarList(deviceInfoVO);
            iotDeviceVOList.forEach(item -> {
                //组装查询的设备信息返回
                UserCollectionFolderVO folderVO = getUserCollectionFolderVO(item.getDeviceCode(), collObjType, item.getDeviceName(), item.getLongitude(), item.getLatitude(), String.valueOf(item.getDeviceStatus()));
                folderVOList.add(folderVO);
            });
        }
        return folderVOList;
    }

    /**
     * 新增收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    @Override
    public Result<String> addCollectFolder(UserCollectionFolderDTO userCollectionFolderDTO) {
        if (StringUtils.isBlank(userCollectionFolderDTO.getFolderName())) {
            throw new CustomException("收藏夹名称不能为空");
        }
        //获取登录信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(loginUser.getUserName());
        userCollectionFolderDTO.setUserId(user.getUserId());
        userCollectionFolderDTO.setTenantId(loginUser.getTenantId());
        userCollectionFolderDTO.setIndustryCode(loginUser.getIndustryCode());
        userCollectionFolderDTO.setAppVerCode(loginUser.getAppVerCode());
        userCollectionFolderDTO.setCreator(loginUser.getUserName());
        //校验收藏夹文件名是否存在
        List<UserCollectionFolderDTO> userCollectionFolderDTOList = userCollectionFolderMapper.getListByCondition(userCollectionFolderDTO);
        /**
         * 如果不为空，有同名的文件夹,不允许新增
         */
        if (!CollectionUtils.isEmpty(userCollectionFolderDTOList)) {
            throw new CustomException("该文件夹已存在，请修改收藏夹名称后再保存!");
        }
        //保存收藏夹
        userCollectionFolderMapper.addFolder(userCollectionFolderDTO);
        return Result.success("新增成功!");
    }

    /**
     * 批量编辑收藏夹
     * @param list
     * @return
     */
    @Override
    public Result<String> batchModifyCollectFolder(List<UserCollectionFolderDTO> list) {
        //校验数据格式
        List<UserCollectionFolderDTO> noIdList = list.stream().filter(item -> Objects.isNull(item.getCollectFolderId())).collect(Collectors.toList());
        //入参中没有传入收藏夹id的
        if (!CollectionUtils.isEmpty(noIdList)) {
            throw new CustomException("编辑失败，请检查入参。");
        }
        //获取登录信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(loginUser.getUserName());
        //批量编辑收藏夹
        userCollectionFolderMapper.batchUpdateFolder(list, user);
        return Result.success("编辑成功!");
    }

    /**
     * 批量编辑收藏的数据
     * @param list
     * @return
     */
    @Override
    public Result<String> batchModifyCollectData(List<UserCollectionFolderVO> list) {
        //校验数据格式
        List<UserCollectionFolderVO> noIdList = list.stream().filter(item -> Objects.isNull(item.getUserDeviceCollId())).collect(Collectors.toList());
        //入参中没有传入文件id的
        if (!CollectionUtils.isEmpty(noIdList)) {
            throw new CustomException("编辑失败，请检查入参。");
        }
        //获取登录信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(loginUser.getUserName());
        userCollectionFolderMapper.batchModifyCollectData(list, user);
        return Result.success("编辑成功!");
    }

    /**
     * 删除收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteFolder(UserCollectionFolderDTO userCollectionFolderDTO) {
        if (Objects.isNull(userCollectionFolderDTO.getCollectFolderId())) {
            throw new CustomException("收藏夹id不能为空!");
        }
        //获取登录信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
        SysUser user = deviceInfoMapper.selectUserOnlyByUserName(loginUser.getUserName());
        userCollectionFolderDTO.setUserId(user.getUserId());
        List<UserCollectionFolderDTO> list = userCollectionFolderMapper.getListByCondition(userCollectionFolderDTO);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException("删除失败，文件夹不存在！");
        }
        //把收藏表里的收藏夹id批量更新掉
        userCollectionFolderMapper.updateCollectFolder(userCollectionFolderDTO);
        //删除收藏夹
        int count = userCollectionFolderMapper.deleteCollectFolder(userCollectionFolderDTO);
        if (count <= 0) {
            throw new CustomException("删除失败");
        }
        return Result.success("删除成功！");
    }
}
