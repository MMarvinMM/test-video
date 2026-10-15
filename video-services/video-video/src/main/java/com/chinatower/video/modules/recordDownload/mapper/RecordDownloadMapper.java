package com.chinatower.video.modules.recordDownload.mapper;

import com.chinatower.video.api.entity.ChannelsTenantDTO;
import com.chinatower.video.api.entity.RecordDownloadDTO;
import com.chinatower.video.api.entity.RecordDownloadVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RecordDownloadMapper {

    void batchSave(List<RecordDownloadDTO> list);

    List<RecordDownloadDTO> list(RecordDownloadVO recordDownload);

    List<String> existByBatchIds(List<RecordDownloadDTO> list);

    void updateById(String id);

    void batchUpdate(List<RecordDownloadDTO> list);

    void batchDelete(List<String> list);

    void batchUpdateDownLoadFail(List<String> list);

    List<RecordDownloadDTO> findDevChanNameByCode(ChannelsTenantDTO channelsTenantDTO);
}
