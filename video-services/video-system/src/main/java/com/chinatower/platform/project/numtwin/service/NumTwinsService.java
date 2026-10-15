package com.chinatower.platform.project.numtwin.service;

import com.chinatower.platform.project.numtwin.entity.NumTwinsConfigDto;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.numtwin.entity.NumTwinsUser;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OfflinUserListReq;
import com.chinatower.platform.project.numtwin.entity.numtwinsvo.OnlineUserListDto;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:39
 */
public interface NumTwinsService {

    public Result addOrUpdate(NumTwinsConfigDto req);

    Result getInfo(NumTwinsConfigDto req);

    Result getUserLevelList(NumTwinsUser req);

    Result offLineUserList(OfflinUserListReq req);

    Result getOnlineUserList(OnlineUserListDto req);
}
