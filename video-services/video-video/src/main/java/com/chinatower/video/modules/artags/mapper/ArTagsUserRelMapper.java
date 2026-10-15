package com.chinatower.video.modules.artags.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.video.api.entity.ArTagsInfoReq;
import com.chinatower.video.modules.artags.dataobject.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArTagsUserRelMapper extends BaseMapper<ArTagsTreeUserRel> {

    int getChanCollInfo(@Param("list") Set<String> list, @Param("userId") String userId,@Param("collType") String collType);

    List<ChannelInfoDO> getChanInfoByChanCode(String channelCode);

    List<String> getDeviceByChanCode(@Param("list") List<String> list);

    void batchSave(@Param("list") List<ArTagsTreeUserRel> list);
}
