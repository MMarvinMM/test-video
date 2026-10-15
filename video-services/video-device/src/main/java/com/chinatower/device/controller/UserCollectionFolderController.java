package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.UserCollectionFolderDTO;
import com.chinatower.device.dto.UserCollectionFolderVO;
import com.chinatower.device.service.IUserCollectionFolderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户收藏夹相关")
@RestController
@RequestMapping("/collect/folder")
public class UserCollectionFolderController {

    @Autowired
    private IUserCollectionFolderService userCollectionFolderService;

    /**
     * 获取用户收藏夹列表
     * @return
     */
    @PostMapping("/getCollectionFolderList")
    public Result<List<UserCollectionFolderVO>> getCollectionFolderList(@RequestBody UserCollectionFolderDTO userCollectionFolderDTO) {
        return Result.data(userCollectionFolderService.getCollectionFolderList(userCollectionFolderDTO));
    }

    /**
     * 新增收藏夹
     * @return
     */
    @PostMapping("/addCollectFolder")
    public Result<String> addCollectFolder(@RequestBody UserCollectionFolderDTO userCollectionFolderDTO) {
        return userCollectionFolderService.addCollectFolder(userCollectionFolderDTO);
    }

    /**
     * 批量编辑收藏夹
     * @param list
     * @return
     */
    @PostMapping("/batchModifyCollectFolder")
    public Result<String> batchModifyCollectFolder(@RequestBody List<UserCollectionFolderDTO> list) {
        return userCollectionFolderService.batchModifyCollectFolder(list);
    }

    /**
     * 批量编辑收藏数据
     * @return
     */
    @PostMapping("/batchModifyCollectData")
    public Result<String> batchModifyCollectData(@RequestBody List<UserCollectionFolderVO> list) {
        return userCollectionFolderService.batchModifyCollectData(list);
    }

    /**
     * 删除收藏夹
     * @return
     */
    @PostMapping("/deleteFolder")
    public Result<String> deleteFolder(@RequestBody UserCollectionFolderDTO userCollectionFolderDTO) {
        return userCollectionFolderService.deleteFolder(userCollectionFolderDTO);
    }

}
