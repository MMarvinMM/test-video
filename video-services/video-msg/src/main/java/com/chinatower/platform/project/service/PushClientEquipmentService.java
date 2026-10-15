package com.chinatower.platform.project.service;

import com.chinatower.platform.entity.request.JgPushRequest;

import java.util.List;

public interface PushClientEquipmentService {

    /**
     * 极光推送指定用户
     *
     * @param pushNews
     * @return
     */
    int pushPersonByUserId(JgPushRequest pushNews);


    /**
     * 推送所有的Android或者Ios用户
     *
     * @param pushNews
     * @param type     Android/Ios
     * @return
     */
    int pushToAllAndroidOrIos(JgPushRequest pushNews, String type);

    void batchPushNews(List<JgPushRequest> pushNews);
}
