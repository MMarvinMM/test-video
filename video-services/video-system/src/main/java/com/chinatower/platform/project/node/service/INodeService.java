package com.chinatower.platform.project.node.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.node.entity.PlatFormVO;
import com.chinatower.system.api.model.ReplaceTokenVO;

import java.util.List;

public interface INodeService {

    List<PlatFormVO> selectNodeByTenantId(PlatFormVO platFormVO);

    Result<String> replaceToken(ReplaceTokenVO replaceTokenVO);

}
