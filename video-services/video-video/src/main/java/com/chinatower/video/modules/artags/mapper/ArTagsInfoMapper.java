package com.chinatower.video.modules.artags.mapper;

import java.util.List;
import java.util.Set;

import com.chinatower.video.modules.artags.dataobject.*;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.video.api.entity.ArTagsInfoReq;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AR标签信息表 Mapper 接口
 * </p>
 *
 * @author songcl
 * @since 2023-04-13
 */
@Repository
public interface ArTagsInfoMapper extends BaseMapper<ArTagsInfo> {

    int countMark(@Param("tagId") String tagId, @Param("userId") String userId);

    void addMark(@Param("tagId") String tagId, @Param("userId") String userId);

    void delMark(@Param("tagId") String tagId, @Param("userId") String userId);

    void deleteMarks(@Param("tagId") String tagId);

    List<String> listMark(@Param("userId") String userId);

    void addMarks(@Param("tagIds") List<String> tagIds, @Param("userId") String userId);

    void delMarks(@Param("tagIds") List<String> tagIds, @Param("userId") String userId);
    List<ArDeviceInfo> getArDeviceInfo(@Param("deviceCodeList") Set<String> deviceCodeList);
    List<ArChannelInfo> getArChannelInfo(@Param("deviceCodeList") Set<String> deviceCodeList);

    List<DeviceIndDTO> getDeviceRegion(@Param("deviceCodeList") List<String> deviceCodeList);

    List<DeviceIndDTO> getIotDeviceRegion(@Param("deviceCodeList") List<String> deviceCodeList);
    
    List<DeviceIndDTO> getHornDeviceRegion(@Param("deviceCodeList") List<String> deviceCodeList);

    List<DeviceIndDTO> getUavDeviceRegion(@Param("deviceCodeList") List<String> deviceCodeList);
    
    List<ArTagTree> listTree(ArTagsInfoReq arTagsInfoReq);

    List<ArTagsInfo> pageList(ArTagsInfoReq arTagsInfoReq);

    void updateArTagsInfo(ArTagsInfo arTagsInfo);

    void add(ArTagsInfo arTagsInfo);

    List<BusinessDictType> arTagsTypeTree(BusinessDictType businessDictType);

    List<ArTagsInfo> queryAll(ArTagsInfoReq arTagsInfoReq);

    void batchAddLabelIcon(ArLabelIconGallery arLabelIconGallery);

    List<ArLabelIconGallery> getLabelIconList(ArLabelIconGallery arLabelIconGallery);

    int checkLabelIcon(ArLabelIconGallery arLabelIconGallery);

    void delLabelIcon(ArLabelIconGallery arLabelIconGallery);

    void addMapTags(@Param("deviceCodes") List<String> deviceCodes,@Param("userId") String userId,@Param("collObjType") String collObjType);

    List<String> listMapTags(@Param("userId") String userId);

    void delMapTags(@Param("deviceCodes") List<String> deviceCodes, String userId,@Param("collObjType") String collObjType);

    List<TagLocationVO> queryTagLocation(TagLocationVO arTagsInfoReq);
}
