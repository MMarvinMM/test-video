package com.chinatower.platform.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.project.system.domain.SystemRiskManagement;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemRiskManagementMapper extends BaseMapper<SystemRiskManagement> {

    void batchInsert(List<SystemRiskManagement> systemRiskManagements);
}