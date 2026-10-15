package com.chinatower.cascade.modules.config.service;

import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.cascade.api.entity.PlatformDataVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.dto.ColonyRelationDto;
import com.chinatower.platform.model.CascadeResult;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;

import java.util.List;

/**
 * 功能说明：平台配置信息service
 *
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
public interface ICascadeConfigService {

    CascadeResult sync(ColonyRelationDto colonyRelationDto);

    Result<List<PlatformDataConfigVO>> getInfo(List<String> list);

    Result<List<TreeSelect>> getPlatformTree();

    Result<List<PlatformDataVO>> queryListByKey(String key);

    String queryAll(String token);

    Boolean queryByPlatformCode(String platformCode);
}
