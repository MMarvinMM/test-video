package com.chinatower.device.mapper;


import com.chinatower.device.dto.UserCollectionFolderDTO;
import com.chinatower.device.dto.UserCollectionFolderVO;
import com.chinatower.device.entity.vo.UserDeviceCollectionQueryVO;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户收藏夹mapper
 */
@Mapper
public interface UserCollectionFolderMapper {

    /**
     * 根据条件查询文件夹列表
     * @param userCollectionFolderDTO
     * @return
     */
    List<UserCollectionFolderDTO> getListByCondition(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 新增收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    int addFolder(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 批量编辑
     * @param list
     * @return
     */
    int batchUpdateFolder(@Param("list") List<UserCollectionFolderDTO> list, @Param("user") SysUser loginUser);

    /**
     * 批量修改收藏夹下文件
     * @param list
     * @param loginUser
     * @return
     */
    int batchModifyCollectData(@Param("list") List<UserCollectionFolderVO> list, @Param("user") SysUser loginUser);

    /**
     * 更新收藏表里的文件夹id
     * @param userCollectionFolderDTO
     * @return
     */
    int updateCollectFolder(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 删除收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    int deleteCollectFolder(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 获取收藏列表
     * @param userId
     * @param list
     * @return
     */
    List<UserDeviceCollectionQueryVO> getCollectList(@Param("userId") String userId, @Param("list") List<String> list);
}
