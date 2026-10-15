package com.chinatower.platform.elasticsearch.repository;

import com.chinatower.platform.easy.dto.OperateOrderEntityVo;
import com.chinatower.platform.modules.OperateOrderEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/7 14:33
 */
public interface OperateRepository extends ElasticsearchRepository<OperateOrderEntityVo,String> {
}
