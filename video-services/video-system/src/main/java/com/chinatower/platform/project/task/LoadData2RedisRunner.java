package com.chinatower.platform.project.task;

import com.chinatower.common.core.domain.R;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisLock;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date
 * @requestID 启动时加载数据到redis
 * 修改为定时任务
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoadData2RedisRunner implements ApplicationRunner {

    private final ISysTenantInfoService sysTenantInfoService;

    private static final String lockKey = "loadData";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //多个服务共同启动时,采用分布式锁，保证只有一个服务加载数据
//        RedisLock redisLock = new RedisLock();
//        try {
//            if (redisLock.lock(lockKey, RedisDBType.PRIMARY)) {
//                List<SysTenantInfoResponse> sysTenantInfoResponses =
//                        sysTenantInfoService.selectAllSysTenant();
//                Map<String, List<String>> listMap = sysTenantInfoResponses.stream().collect(Collectors.groupingBy(SysTenantInfoResponse::getDeptId,
//                        Collectors.mapping(SysTenantInfoResponse::getTenantId, Collectors.toList())));
//                for (Map.Entry<String,List<String>> entry: listMap.entrySet()
//                ) {
//                    String key = entry.getKey();
//                    List<String> value = entry.getValue();
//                    RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + key, value, RedisDBType.PRIMARY);
//                }
//            }
//        } catch (Exception e) {
//            //发生异常时释放锁,其他服务执行
//            if (!redisLock.isExpired(lockKey,RedisDBType.PRIMARY)) {
//                redisLock.unlock(lockKey,RedisDBType.PRIMARY);
//            }
//            log.error("加载铁塔组织对应租户集合数据失败:{}", e.getMessage());
//        }
    }

}
