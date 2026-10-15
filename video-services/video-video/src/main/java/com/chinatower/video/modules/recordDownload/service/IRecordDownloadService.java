package com.chinatower.video.modules.recordDownload.service;

import com.chinatower.mongodb.request.Page;
import com.chinatower.video.api.entity.RecordDownloadDTO;
import com.chinatower.video.api.entity.RecordDownloadVO;

import java.util.List;

public interface IRecordDownloadService {

    void batchSave(List<RecordDownloadDTO> list);

    Page<RecordDownloadDTO> list(RecordDownloadVO recordDownload);

    List<String> existDownloadFinishByBatchIds(List<RecordDownloadDTO> recordDownloadList);

    void delete(String id);

    void batchUpdate(List<RecordDownloadDTO> recordDownloadList);

    void batchDelete(List<String> deleteList);

    void batchUpdateDownLoadFail(List<String> list);
}
