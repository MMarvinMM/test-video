package com.chinatower.algorithm.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.service.IAlgorithmConfigInfoService;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * video-device
 * @author yy
 */
@RestController
@Slf4j
@RequestMapping("/industry/algorithm")
@Api(value = "行业算法相关sdk", tags = "算法模块sdk")
public class IndustryAlgorithmController {


    @Resource
    private IAlgorithmConfigInfoService iAlgorithmConfigInfoService;

    /**
     * 算法分页查询
     * 新增算法查询接口，根据租户ID，查询该租户下所订购的算法总数及明细；按照摄像机，查询摄像机订购的算法所对应的告警类型。
     *
     * @param industryAlgorithmQueryVo 行业编码、租户编码、用户id、摄像机编码(非必传)
     * @return 如果摄像机字段不传，则查询  对应租户订购的算法总数、算法明细列表（算法名称、算法ID、算法对应的告警类型及告警编码)
     *         如果传了摄像机字段，则查询该摄像机绑定的算法 的 告警类型及告警编码 （算法名称、算法ID、算法对应的告警类型及告警编码）
     */
    @PostMapping("/queryByTenantId")
    public TableDataInfo queryByTenantId(@RequestBody IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        List<IndustryAlgorithmDTO> industryAlgorithmCount = iAlgorithmConfigInfoService.queryByTenantId(industryAlgorithmQueryVo);
        TableDataInfo res = new TableDataInfo();
        res.setCode(ResultCode.SUCCESS.getCode());
        res.setRows(industryAlgorithmCount);
        res.setTotal((int)(new PageInfo(industryAlgorithmCount)).getTotal());
        return res;
    }

    /**
     * 查询摄像机算法详情
     * @param industryAlgorithmQueryVo
     * @return
     */
    @PostMapping("/queryByTenantIdAndDevices")
    public Result<Map<String, List<IndustryAlgorithmDTO>>> queryByTenantIdAndDevices(@RequestBody @Valid IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        Map<String, List<IndustryAlgorithmDTO>> industryAlgorithmCount = iAlgorithmConfigInfoService.queryByTenantIdAndDevices(industryAlgorithmQueryVo);
        return Result.data(industryAlgorithmCount);
    }

    /**
     * 摄像机自身绑定的后端算法
     *
     * 查询摄像机自身绑定的后端算法
     * 入参：行业应用版本编码、租户编码，用户ID、摄像机list(非必填)
     */
    @PostMapping("/queryConfigByTenantId")
    public Result<List<IndustryAlgorithmDTO>> queryConfigByTenantId(@RequestBody  IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        List<IndustryAlgorithmDTO> industryAlgorithmCount = iAlgorithmConfigInfoService.queryConfigByTenantId(industryAlgorithmQueryVo);
        return Result.data(industryAlgorithmCount);
    }

    /**
     * 查询遥感算法
     * @param industryAlgorithmQueryVo
     * @return
     */
    @PostMapping("/querySensingAlgorithm")
    public TableDataInfo querySensingAlgorithm(@RequestBody IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        List<IndustryAlgorithmDTO> industryAlgorithmCount = iAlgorithmConfigInfoService.querySensingAlgorithm(industryAlgorithmQueryVo);
        TableDataInfo res = new TableDataInfo();
        res.setCode(ResultCode.SUCCESS.getCode());
        res.setRows(industryAlgorithmCount);
        res.setTotal((int)(new PageInfo(industryAlgorithmCount)).getTotal());
        return res;
    }


}
