package com.chinatower.video.modules.artags.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.ArTagsInfoReq;
import com.chinatower.video.modules.artags.dataobject.*;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import com.chinatower.video.modules.query.entity.ArTagFilterVO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;

import java.util.List;

/**
 * <p>
 * AR标签信息表 服务类
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
public interface IArTagsInfoService extends IService<ArTagsInfo> {


    void update(ArTagsInfo arTagsInfo);

    int countMark(String id, String userId);

    void addMark(String id, String userId);

    void delMark(String id, String userId);

    void deleteMarks(String id);

    List<String> listMark(String userId);

    void addMarks(List<String> arTagsInfoListMarkNew, String userId);

    void delMarks(List<String> arTagsInfoListMark, String userId);

    Result weatherInfoList(ArDeviceDTO arDeviceDTO);

    List<ArTagTree> listTree(ArTagsInfoReq arTagsInfoReq);

    List<ArTagsInfo> pageList(ArTagsInfoReq arTagsInfoReq);

    Result<Long> add(ArTagsInfo arTagsInfo);

    List<BusinessDictType> arTagsTypeTree(Integer dictType);

    List<ArDeviceInfo> queryAll(ArTagsInfoReq arTagsInfoReq);

    Result<String> batchAddLabelIcon(ArLabelIconGallery arLabelIconGallery);

    Result<List<ArLabelIconGallery>> getLabelIconList(ArLabelIconGallery arLabelIconGallery);

    Result<String> delLabelIcon(ArLabelIconGallery arLabelIconGallery);

    Result<Boolean> checkLabelIcon(ArLabelIconGallery arLabelIconGallery);

    List<ArTagMoveRes> listForCruise(ArTagFilterVO arTagFilterVO);

    Result<List<ArTagMoveRes>> queryMapAllTags(ArTagFilterVO tagFilterVO);
}
