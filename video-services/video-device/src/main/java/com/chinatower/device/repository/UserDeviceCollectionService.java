package com.chinatower.device.repository;

import com.chinatower.device.dataobject.UserDeviceCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;

import java.util.List;

/**
* @author trk
* @description 针对表【vm_user_device_collection_t(用户设备收藏表)】的数据库操作Service
* @createDate 2022-03-25 14:23:52
*/
public interface UserDeviceCollectionService extends IService<UserDeviceCollection> {
    /**
     * 新增收藏
     * @param collObjCode,collObjType
     */
    void addCollections(List<String> collObjCode,String collObjType);

    /**
     * 取消收藏
     * @param collObjCode,collObjType
     */
    void cancelCollections(List<String> collObjCode,String collObjType);

    /**
     * 查询收藏信息列表
     * @param param 参数
     * @return 收藏信息列表
     */
    List<UserDeviceCollectionQueryVO> queryCollections(UserDeviceCollectionQueryDTO param);

    void addJNCollections(List<String> collObjCode,String collObjType);

    void cancelJNCollections(List<String> collObjCode,String collObjType);
}
