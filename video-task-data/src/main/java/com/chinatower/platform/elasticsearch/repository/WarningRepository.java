package com.chinatower.platform.elasticsearch.repository;

import com.chinatower.platform.easy.dto.WarningOrderEntityVo;
import com.chinatower.platform.modules.WarningOrderEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/7 14:32
 */
public interface WarningRepository extends ElasticsearchRepository<WarningOrderEntityVo,String> {
}
