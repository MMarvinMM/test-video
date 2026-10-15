package com.chinatower.device.service.impl;

import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.device.dto.ResourceConfVO;
import com.chinatower.device.mapper.ResourceConfMapper;
import com.chinatower.device.service.IResourceConfService;
import com.chinatower.platform.annotation.DataScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceConfServiceImpl implements IResourceConfService {

    @Resource
    private ResourceConfMapper resourceConfMapper;

    /**
     * 查询资源
     * @param resourceConfVO
     * @return
     */
    @Override
    @DataScope(resourceColumn = "d.RESOURCE_ID", tenantColumn = "d.tenant_id", industryCode = "d.industry_code")
    public List<ResourceConfVO> selectResourceList(ResourceConfVO resourceConfVO) {
        return resourceConfMapper.selectResourceList(resourceConfVO);
    }
}
