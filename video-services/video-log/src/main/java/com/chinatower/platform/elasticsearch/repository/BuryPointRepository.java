package com.chinatower.platform.elasticsearch.repository;

import com.chinatower.platform.entity.BuryPointEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BuryPointRepository extends ElasticsearchRepository<BuryPointEntity, String> {
}
