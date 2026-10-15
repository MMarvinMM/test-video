package com.chinatower.platform.elasticsearch.service;

import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.platform.entity.BuryPointEntity;
import org.springframework.data.domain.Page;

public interface BuryPointService {

    BuryPointEntity save(BuryPointDTO buryPointDTO);


    Page<BuryPointEntity> pageable();
}
