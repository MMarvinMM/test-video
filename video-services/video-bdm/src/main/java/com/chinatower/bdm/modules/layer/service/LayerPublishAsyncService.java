package com.chinatower.bdm.modules.layer.service;



public interface LayerPublishAsyncService {

    void publishAndSaveResult(String layerId, String fileUrl, String fileName);
}
