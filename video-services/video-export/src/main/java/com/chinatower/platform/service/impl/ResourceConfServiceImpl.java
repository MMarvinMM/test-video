package com.chinatower.platform.service.impl;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.platform.service.IResourceConfService;
import com.chinatower.platform.mapper.ResourceConfExportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 功能说明：资源管理service实现类
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Service("resourceConfService")
@Slf4j
public class ResourceConfServiceImpl implements IResourceConfService {
    @Autowired
    ResourceConfExportMapper resourceConfDao;


    /**
     * 功能说明: 获取资源顶级列表(顶级结构)
     * @author zhaol
     * @param resourceConf 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID     *
     */
    @Override
    public List<ResourceVO> selectResourceExportList(ResourceConf resourceConf) {
        return resourceConfDao.selectResourceExportList(resourceConf);
    }

}
