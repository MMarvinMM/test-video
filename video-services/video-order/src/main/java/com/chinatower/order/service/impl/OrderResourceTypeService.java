package com.chinatower.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.dto.ResourceTypeInitDTO;
import com.chinatower.order.mapper.ResourceTypeMapper;
import com.chinatower.order.service.IOrderResourceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName IOrderResourceType
 * @Deacription 资源类型数据服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
@Slf4j
@Service
public class OrderResourceTypeService implements IOrderResourceTypeService {

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;

    /**
     * 资源类型数据初始化
     *
     * @return void
     * @param: resourceTypeInt
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void initResourceType(List<ResourceTypeInitDTO> resourceTypeInitList) {

        //获取当前租户下数据
        resourceTypeInitList.forEach(initInfo -> {
            ResourceType resourceType = new ResourceType();
            resourceType.setTenantId(initInfo.getTenantId());
            resourceType.setIndustryCode(initInfo.getIndustryCode());
            resourceType.setAppVerCode(initInfo.getAppVerCode());
            List<ResourceType> oldList = resourceTypeMapper.selectResourceTypeList(resourceType);
            if (CollectionUtils.isEmpty(oldList)) {
                //获取初始化数据
                List<ResourceTypeInt> intList = resourceTypeMapper.getResourceTypeInt(initInfo.getIndustryCode(), initInfo.getAppVerCode());
                List<ResourceTypeInt> addIntList = new ArrayList<>();
                for (ResourceTypeInt initVo : intList) {
                    // 一级资源父节点默认给个0
                    if (StringUtils.isEmpty(initVo.getParentId())) {
                        initVo.setParentId("0");
                    }
                    initVo.setTenantId(initInfo.getTenantId());
                    initVo.setCreator(initInfo.getCreator());
                    addIntList.add(initVo);
                }
                if (CollUtil.isNotEmpty(addIntList)) {
                    resourceTypeMapper.insertResourceTypeInitData(addIntList);
                }
            }
        });
    }
}
