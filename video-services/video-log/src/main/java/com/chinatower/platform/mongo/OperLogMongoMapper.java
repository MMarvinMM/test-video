package com.chinatower.platform.mongo;

import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.entity.SysOperLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OperLogMongoMapper extends AbstractBaseMongoDAO<SysOperLogEntity> {

}
