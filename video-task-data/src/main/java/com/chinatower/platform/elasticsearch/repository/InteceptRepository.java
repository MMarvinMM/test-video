package com.chinatower.platform.elasticsearch.repository;

import com.chinatower.platform.easy.dto.InterceptOrderEntityVo;
import com.chinatower.platform.modules.InterceptOrderEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/7 14:35
 */
public interface InteceptRepository extends ElasticsearchRepository<InterceptOrderEntityVo,String> {
}
