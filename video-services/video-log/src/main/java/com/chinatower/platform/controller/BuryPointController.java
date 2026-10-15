package com.chinatower.platform.controller;

import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.elasticsearch.service.BuryPointService;
import com.chinatower.platform.entity.BuryPointEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * tower-log
 */
@Slf4j
@RestController
@RequestMapping("/burypoint")
public class BuryPointController extends TowerBaseController {

    @Autowired
    private BuryPointService buryPointService;

    // 默认不使用 ES
    @Value("${spring.elasticsearch.enabled:}")
    private Boolean esEnabled;

    /**
     * 保存日志
     * @param buryPointDTO
     * @return
     */
    @PostMapping("/saveOne")
    public Result<Boolean> saveOne(@RequestBody BuryPointDTO buryPointDTO){
        try {
            log.info("BuryPointController saveOne buryPointDTO:{}", buryPointDTO);
            if (Objects.nonNull(esEnabled) && esEnabled) {
                BuryPointEntity buryPointEntity = buryPointService.save(buryPointDTO);
                log.info("save buryPointEntity:{}", buryPointEntity);
                return Result.data(true);
            } else {
                return Result.data(false);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("es存储异常：{}", e);
        }
        return Result.data(false);
    }

    /**
     * 测试验证
     * @return
     */
    @GetMapping("/pageable")
    public Result<Page<BuryPointEntity>> pageable(){
        try {
            return Result.data(buryPointService.pageable());
        } catch (Exception e){
            e.printStackTrace();
            log.error("es查询异常：{}", e);
        }
        return Result.data(null);
    }
}
