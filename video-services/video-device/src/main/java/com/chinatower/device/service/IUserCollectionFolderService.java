package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.UserCollectionFolderDTO;
import com.chinatower.device.dto.UserCollectionFolderVO;

import java.util.List;

public interface IUserCollectionFolderService {

    /**
     * 获取列表
     * @param userCollectionFolderDTO
     * @return
     */
    List<UserCollectionFolderVO> getCollectionFolderList(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 新增，编辑收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    Result<String> addCollectFolder(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 批量编辑收藏夹
     * @param list
     * @return
     */
    Result<String> batchModifyCollectFolder(List<UserCollectionFolderDTO> list);

    /**
     * 删除收藏夹
     * @param userCollectionFolderDTO
     * @return
     */
    Result<String> deleteFolder(UserCollectionFolderDTO userCollectionFolderDTO);

    /**
     * 批量编辑收藏下数据
     * @param list
     * @return
     */
    Result<String> batchModifyCollectData(List<UserCollectionFolderVO> list);
}
