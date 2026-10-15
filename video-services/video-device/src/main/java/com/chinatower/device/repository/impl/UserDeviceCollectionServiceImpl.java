package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.dataobject.UserDeviceCollection;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;
import com.chinatower.device.mapper.RecorderDeviceMapper;
import com.chinatower.device.mapper.UavDeviceMapper;
import com.chinatower.device.mapper.UserDeviceCollectionMapper;
import com.chinatower.device.repository.IChannelInfoRepository;
import com.chinatower.device.repository.UserDeviceCollectionService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author trk
* @description 针对表【vm_user_device_collection_t(用户设备收藏表)】的数据库操作Service实现
* @createDate 2022-03-25 14:23:52
*/
@Service
public class UserDeviceCollectionServiceImpl extends ServiceImpl<UserDeviceCollectionMapper, UserDeviceCollection>
    implements UserDeviceCollectionService {

	@Autowired
	private IChannelInfoRepository channelInfoRepository;

    @Autowired
    private UserDeviceCollectionMapper userDeviceCollectionMapper;

    @Resource
    private UavDeviceMapper uavDeviceMapper;

    @Resource
    private RecorderDeviceMapper recorderDeviceMapper;

    /**
     * 新增收藏
     *
     * @param collObjCode
     * @author trk
     * @date 2022/3/25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void addCollections(List<String> collObjCode ,String collObjType) {
        LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        wrapper.select(UserDeviceCollection::getCollObjCode,UserDeviceCollection::getUserId)
                .in(UserDeviceCollection::getCollObjCode,collObjCode).eq(UserDeviceCollection::getUserId,userId).eq(UserDeviceCollection::getCollObjType, collObjType);

        List<UserDeviceCollection> list = list(wrapper);
        ArrayList<String> strings = new ArrayList<>();
        for (UserDeviceCollection userDevice : list) {
            strings.add(userDevice.getCollObjCode());
        }
        List<UserDeviceCollection> insertList = new ArrayList<>();
        for (String code : collObjCode) {
            if(!strings.contains(code)){
                UserDeviceCollection deviceCollection = new UserDeviceCollection();
                deviceCollection.setUserId(userId);
                deviceCollection.setCollObjCode(code);
                deviceCollection.setCollObjType(collObjType);
                insertList.add(deviceCollection);
            }
        }
        saveBatch(insertList);

        // 收藏设备顺便把设备下的通道一块收藏
        collChannel(collObjCode, collObjType, userId,DeviceConstants.ONE_NUM);
        //如果该通道的设备下所有通道都已经收藏，则其设备也收藏
        this.collDevice(collObjCode, collObjType,userId, DeviceConstants.ONE_NUM);
    }
    /**
     * 取消收藏
     *
     * @param collObjCode
     * @author trk
     * @date 2022/3/25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelCollections(List<String> collObjCode,String collObjType) {
        LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        wrapper.in(UserDeviceCollection::getCollObjCode,collObjCode).eq(UserDeviceCollection::getCollObjType,collObjType).eq(UserDeviceCollection::getUserId,userId);
        remove(wrapper);
        // 取消收藏设备顺便把设备下的通道一块取消
        collChannel(collObjCode, collObjType, userId,DeviceConstants.ZERO_NUM);
        //如果该通道的设备下所有通道都已经取消收藏，则其设备也取消收藏
        this.collDevice(collObjCode, collObjType,userId, DeviceConstants.ZERO_NUM);
    }

    @Override
    public List<UserDeviceCollectionQueryVO> queryCollections(UserDeviceCollectionQueryDTO param) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return userDeviceCollectionMapper.queryCollections(userId, param.getCollObjTypeList());
    }

    public void collChannel(List<String> collObjCode, String collObjType,String userId,int optType)
    {
    	if( DeviceConstants.FIVE_FLAG.equals(collObjType) )
    	{
    		// 查询设备下的通道
	    	LambdaQueryWrapper<ChannelInfoDO> wrapperCh = new LambdaQueryWrapper<>();
	        wrapperCh.select(ChannelInfoDO::getChannelCode)
	        	.in(ChannelInfoDO::getDeviceCode,collObjCode).eq(ChannelInfoDO::getDeleteFlag,DeviceConstants.DELETE_FLAG_NORMAL);
	        List<String> chanList = channelInfoRepository.listObjs(wrapperCh, Object::toString);

	        this.addOrRemoveCollectChannel(chanList, optType, userId, Constants.SIX);
    	}
        else if (Objects.equals(String.valueOf(DeviceConstants.DEVICETYPE_12), collObjType)) {
            List<ChannelInfoVO> list = recorderDeviceMapper.getChannelByDeviceCodes(collObjCode);
            if (!CollectionUtils.isEmpty(list)) {
                List<String> chanList = list.stream().map(ChannelInfoVO::getChannelCode).collect(Collectors.toList());
                this.addOrRemoveCollectChannel(chanList, optType, userId, String.valueOf(DeviceConstants.DEVICETYPE_13));
            }
        }
    }

    /**
     * 收藏设备，同时收藏设备下的通道
     * @param chanList
     * @param optType
     * @param userId
     */
    public void addOrRemoveCollectChannel(List<String> chanList, int optType, String userId, String collType) {
        if( optType == DeviceConstants.ONE_NUM ){
            LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(UserDeviceCollection::getCollObjCode,chanList).
                    eq(UserDeviceCollection::getCollObjType, collType).
                    eq(UserDeviceCollection::getUserId,userId);
            List<UserDeviceCollection> list = list(wrapper);
            Set<String> strings = list.stream().map(UserDeviceCollection::getCollObjCode).collect(Collectors.toSet());
            List<UserDeviceCollection> insertList = new ArrayList<>();
            for (String code : chanList) {
                if(!strings.contains(code)){
                    UserDeviceCollection deviceCollection = new UserDeviceCollection();
                    deviceCollection.setUserId(userId);
                    deviceCollection.setCollObjCode(code);
                    deviceCollection.setCollObjType(collType);
                    insertList.add(deviceCollection);
                }
            }
            saveBatch(insertList);
        } else {
            LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(UserDeviceCollection::getCollObjCode,chanList).
                    eq(UserDeviceCollection::getCollObjType,collType).
                    eq(UserDeviceCollection::getUserId,userId);
            remove(wrapper);
        }
    }

    /**
     * 取消收藏通道时，如果该通道的设备下所有通道都已经取消收藏，则其设备也取消收藏
     * @param collObjCode
     * @param collObjType
     * @param
     */
    public void collDevice(List<String> collObjCode, String collObjType,String userId,int optType)
    {
        if( DeviceConstants.SIX_FLAG.equals(collObjType) )
        {
            this.dealCollectDevice(collObjCode, userId, optType, Constants.FIVE);
        }
        //执法记录仪通道收藏
        else if (Objects.equals(String.valueOf(DeviceConstants.DEVICETYPE_13), collObjType)) {
            this.dealCollectRecorderDevice(collObjCode, userId, optType, String.valueOf(DeviceConstants.DEVICETYPE_12));
        }
    }

    /**
     * 处理摄像机收藏
     * @param collObjCode
     * @param userId
     * @param optType
     */
    public void dealCollectDevice(List<String> collObjCode, String userId,int optType, String collType) {
        //收藏/取消收藏，单通道场景
        if(collObjCode.size() == Constants.ONE_NUM){
            // 根据通道编码查询设备下的通道
            List<ChannelInfoDO> chanInfoByChanCode = userDeviceCollectionMapper.getChanInfoByChanCode(collObjCode.get(Constants.ZERO_NUM));
            if(!CollectionUtils.isEmpty(chanInfoByChanCode)){
                Set<String> channelList = chanInfoByChanCode.stream().map(ChannelInfoDO::getChannelCode).collect(Collectors.toSet());
                String deviceCode = chanInfoByChanCode.get(Constants.ZERO_NUM).getDeviceCode();
                this.addOrCancelCollectDevice(channelList, deviceCode, userId, optType, collType, Constants.SIX);
            }
        }
        //收藏/取消收藏，通道以上级别场景
        else{
            // 根据通道编码查询设备
            List<String> deviceList = userDeviceCollectionMapper.getDeviceByChanCode(collObjCode);
            if(!CollectionUtils.isEmpty(deviceList)){
                this.addOrCancelCollectDeviceList(deviceList, userId, optType, collType);
            }
        }
    }

    /**
     * 处理执法记录仪收藏
     * @param collObjCode
     * @param userId
     * @param optType
     */
    public void dealCollectRecorderDevice(List<String> collObjCode, String userId,int optType, String collType) {
        // 根据通道编码查询设备下的通道

        //收藏/取消收藏，单通道场景
        if(collObjCode.size() == Constants.ONE_NUM){
            List<ChannelInfoVO> channelInfoVOList = recorderDeviceMapper.getAllDeviceChannelByChannelCodeList(collObjCode);
            Set<String> channelList = channelInfoVOList.stream().map(ChannelInfoVO::getChannelCode).collect(Collectors.toSet());
            String deviceCode = channelInfoVOList.get(Constants.ZERO_NUM).getParentDeviceCode();
            this.addOrCancelCollectDevice(channelList, deviceCode, userId, optType, collType, String.valueOf(DeviceConstants.DEVICETYPE_13));
        }
        //收藏/取消收藏，通道以上级别场景
        else{
            // 根据通道编码查询设备
            List<ChannelInfoVO> channelInfoVOList = recorderDeviceMapper.getChannelByChannelCodeList(collObjCode);
            if(!CollectionUtils.isEmpty(channelInfoVOList)){
                List<String> deviceList = channelInfoVOList.stream().map(ChannelInfoVO::getParentDeviceCode).collect(Collectors.toList());
                this.addOrCancelCollectDeviceList(deviceList, userId, optType, collType);
            }
        }
    }

    /**
     * 收藏/取消收藏，通道以上级别场景
     * @param deviceList
     * @param userId
     * @param optType
     */
    public void addOrCancelCollectDeviceList(List<String> deviceList, String userId, int optType, String collType) {
        //add
        if( optType == DeviceConstants.ONE_NUM ){
            //新增设备收藏数据前，先查询insertDeviceList是否在数据库已存在，存在则不新增
            LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(UserDeviceCollection::getCollObjCode,deviceList).
                    eq(UserDeviceCollection::getCollObjType,collType).
                    eq(UserDeviceCollection::getUserId,userId);
            List<UserDeviceCollection> list = list(wrapper);
            Set<String> collDeviceList = list.stream().map(UserDeviceCollection::getCollObjCode).collect(Collectors.toSet());

            List<UserDeviceCollection> insertList = new ArrayList<>();
            for (String s : deviceList) {
                if(!collDeviceList.contains(s)){
                    UserDeviceCollection deviceCollection = new UserDeviceCollection();
                    deviceCollection.setUserId(userId);
                    deviceCollection.setCollObjCode(s);
                    deviceCollection.setCollObjType(collType);
                    insertList.add(deviceCollection);
                }
            }
            saveBatch(insertList);
        }
        //remove
        else{
            LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(UserDeviceCollection::getCollObjCode,deviceList).
                    eq(UserDeviceCollection::getCollObjType,collType).
                    eq(UserDeviceCollection::getUserId,userId);
            remove(wrapper);
        }
    }

    /**
     * 处理收藏设备信息
     */
    public void addOrCancelCollectDevice(Set<String> channelList, String deviceCode, String userId, int optType, String collType, String queryCollType) {
        //构造通道编码集合

        //查询该用户是否收藏通道集合中的数据
        int chanCollInfo = userDeviceCollectionMapper.getChanCollInfo(channelList, userId, queryCollType);

        //add
        if( optType == DeviceConstants.ONE_NUM ){
            //如果收藏数量=通道数量（通道均已收藏），则新增其设备的收藏数据
            if(chanCollInfo == channelList.size()){
                LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserDeviceCollection::getCollObjCode,deviceCode).
                        eq(UserDeviceCollection::getCollObjType, collType).
                        eq(UserDeviceCollection::getUserId,userId);
                List<UserDeviceCollection> list = list(wrapper);
                if(list.size() == Constants.ZERO_NUM){
                    UserDeviceCollection deviceCollection = new UserDeviceCollection();
                    deviceCollection.setUserId(userId);
                    deviceCollection.setCollObjCode(deviceCode);
                    deviceCollection.setCollObjType(collType);
                    save(deviceCollection);
                }
            }
        }
        //remove
        else{
            LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDeviceCollection::getCollObjCode,deviceCode).
                    eq(UserDeviceCollection::getCollObjType,collType).
                    eq(UserDeviceCollection::getUserId,userId);
            remove(wrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addJNCollections(List<String> collObjCode, String collObjType) {
        LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        wrapper.select(UserDeviceCollection::getCollObjCode, UserDeviceCollection::getUserId)
                .in(UserDeviceCollection::getCollObjCode, collObjCode).eq(UserDeviceCollection::getUserId, userId);
        List<UserDeviceCollection> list = list(wrapper);
        ArrayList<String> strings = new ArrayList<>();
        for (UserDeviceCollection userDevice : list) {
            strings.add(userDevice.getCollObjCode());
        }
        //优化for循环
        List<UserDeviceCollection> deviceCollectionsToSave = new ArrayList<>();
        for (String code : collObjCode) {
            if (!strings.contains(code)) {
                UserDeviceCollection userDeviceCollection = new UserDeviceCollection();
                userDeviceCollection.setCollObjCode(code);
                userDeviceCollection.setCollObjType(collObjType);
                userDeviceCollection.setUserId(userId);
                deviceCollectionsToSave.add(userDeviceCollection);
            }
        }
        if (!deviceCollectionsToSave.isEmpty()) {
            saveBatch(deviceCollectionsToSave);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelJNCollections(List<String> collObjCode, String collObjType) {
        LambdaQueryWrapper<UserDeviceCollection> wrapper = new LambdaQueryWrapper<>();
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        wrapper.in(UserDeviceCollection::getCollObjCode, collObjCode)
                .eq(UserDeviceCollection::getCollObjType, collObjType)
                .eq(UserDeviceCollection::getUserId, userId);
        remove(wrapper);
    }
}




