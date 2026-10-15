package com.chinatower.event.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.SecondRecogConfigVO;
import com.chinatower.event.repository.SecondRecogConfigService;
import com.chinatower.event.vo.SecondRecogConfigPageVo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * video-event
 * 二次识别配置controller
 * @author cuisj
 * @date 2023-12-26
 * @requestID 二次识别配置
 */
@RestController
@RequestMapping("/secondRecogConfig")
@Slf4j
public class SecondRecogConfigController extends BaseController {

    @Autowired
    private SecondRecogConfigService secondRecogConfigService;

    /**
     * 功能描述：二次识别配置列表查询
     * @param vo
     * @return
     */
    @PostMapping("/findByCondition")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public Result<TableDataInfo> findByCondition(@RequestBody SecondRecogConfigPageVo vo){
        return Result.data(secondRecogConfigService.findByCondition(vo));
    }

    /**
     * 功能描述：二次识别配置新增
     * @param vo
     * @return
     */
    @PostMapping("/saveSecondRecogConfig")
    public Result<Object> saveSecondRecogConfig(@RequestBody SecondRecogConfigVO vo){
        return secondRecogConfigService.saveSecondRecogConfig(vo);
    }

    /**
     * 功能描述：二次识别配置修改
     * @param vo
     * @return
     */
    @PutMapping("/updateSecondRecogConfig")
    public Result<Object> updateSecondRecogConfig(@RequestBody SecondRecogConfigVO vo){
        return secondRecogConfigService.saveSecondRecogConfig(vo);
    }

    /**
     * 功能描述：二次识别配置逻辑删除
     * @param vo
     * @return
     */
    @PostMapping("/deleteSecondRecogConfig")
    public Result deleteSecondRecogConfig(@RequestBody SecondRecogConfigVO vo){
        secondRecogConfigService.deleteSecondRecogConfig(vo);
        return Result.data("删除成功");
    }

    /**
     * 功能描述：获取默认名称“二次识别配置名称+数字”（数字累加即可）
     * @return
     */
    @GetMapping("/getAutoConfigName")
    public Result getAutoConfigName(@RequestParam("tenantId")String tenantId,@RequestParam("industryCode")String industryCode){
        return Result.data(secondRecogConfigService.getAutoConfigName(tenantId,industryCode));
    }

    /**
     * 功能描述：根据id查找对应的二次识别配置
     * @param id
     * @return
     */
    @GetMapping("/findSecondRecogConfigById")
    public Result findSecondRecogConfigById(@RequestParam("id") Integer id){
        return Result.data(secondRecogConfigService.findSecondRecogConfigById(id));
    }

    /**
     * 功能描述：判断二次识别配置中告警来源X告警类型是否已有配置
     * @param vo
     * @return
     */
    @PostMapping("/promptInformation")
    public Result promptInformation(@RequestBody SecondRecogConfigVO vo){
        return Result.data(secondRecogConfigService.promptInformation(vo));
    }

    /**
     * 功能描述：判断二次识别配置名称是否重复
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param secondRecogName 二次识别配置名称
     * @param id id
     * @return
     */
    @GetMapping("/secondRecogNameIsRepeat")
    public Result secondRecogNameIsRepeat(@RequestParam(value = "tenantId",required = false)String tenantId,
                                          @RequestParam(value = "industryCode",required = false)String industryCode,
                                          @RequestParam(value = "secondRecogName",required = false)String secondRecogName,
                                          @RequestParam(value = "id",required = false)Integer id){
        return secondRecogConfigService.secondRecogNameIsRepeat(tenantId,industryCode,secondRecogName,id);
    }
}
