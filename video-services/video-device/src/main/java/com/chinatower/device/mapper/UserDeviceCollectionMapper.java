package com.chinatower.device.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.dataobject.UserDeviceCollection;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* @author trk
* @description 针对表【vm_user_device_collection_t(用户设备收藏表)】的数据库操作Mapper
* @createDate 2022-03-25 14:23:52
* @Entity generator.domain.UserDeviceCollection
*/
public interface UserDeviceCollectionMapper extends BaseMapper<UserDeviceCollection> {

    List<ChannelInfoDO> getChanInfoByChanCode(String channelCode);

    List<String> getDeviceByChanCode(@Param("list") List<String> list);

    int getChanCollInfo(@Param("list") Set<String> list, @Param("userId") String userId,@Param("collType") String collType);

    /**
     * 根据用户id和类型查询收藏信息列表
     * @param userId 用户id
     * @param collObjTypeList 收藏类型
     * @return 收藏信息列表
     */
    List<UserDeviceCollectionQueryVO> queryCollections(@Param("userId") String userId,
                                                       @Param("collObjTypeList")List<String> collObjTypeList);
}




