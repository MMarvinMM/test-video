package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.project.system.domain.vo.IndustryQueryVO;
import com.chinatower.platform.project.system.domain.vo.IndustrySelectVO;
import com.chinatower.platform.project.system.service.ISysIndustyService;
import com.chinatower.system.api.model.IndustryConfDto;
import com.chinatower.system.api.model.IndustryTypeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * video-system
 * 功能说明: 行业应用模块
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/3/18 9:13
 * @requestID  需求单号及需求名称
 */
@Api(tags = "行业应用")
@RestController
@RequestMapping("/system/industry")
public class SysIndustyController extends TowerBaseController
{
    @Autowired
    private ISysIndustyService industyService;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 功能说明: 所属行业查询接口:生效的行业
     * @param:
     * @return:
     * @author: 王梦迪
     * @date: 2022/3/17 9:27
     * @requestID  需求单号及需求名称
     */
    @ApiOperation(value = "行业查询列表")
    @GetMapping("/list")
    public Result<List<SysIndustryConf>> list()
    {
        List<SysIndustryConf> list = industyService.selectIndustryConf();
        if(CollectionUtils.isEmpty(list)){
            return result(new ArrayList<>());
        }
        List<String> urls = list.stream().map(SysIndustryConf::getImgUrl).filter(StringUtils::hasText).collect(Collectors.toList());
        Map<String, String> fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(urls);
        // 处理Obs文件URL加密返回
        list.forEach(sysIndustryConf -> sysIndustryConf.setImgUrl(fileUrlMap.get(sysIndustryConf.getImgUrl())));
        return result(list);
    }

    /**
     * 功能说明: 行业子表行业应用版本查询:生效的行业版本
     * @param:
     * @return:
     * @author: 王梦迪
     * @date: 2022/3/17 9:27
     * @requestID  需求单号及需求名称
     */
    @ApiOperation(value = "行业应用版本查询")
    @GetMapping("/industryVer/{industryCode}")
    public Result<List<SysIndustryViewRel>> list(@PathVariable String industryCode)
    {
        List<SysIndustryViewRel> list = industyService.selectIndustryViewRel(industryCode);
        return result(list);
    }

    /**
     * 行业应用版本列表查询
     */
    @ApiOperation(value = "行业应用版本列表查询")
    @GetMapping("/allIndustryVer")
    public Result<List<SysIndustryViewRel>> selectAllIndustryViewRel()
    {
        List<SysIndustryViewRel> list = industyService.selectAllIndustryViewRel();
        return result(list);
    }

    /**
     * 根据用户id查询行业权限
     */
    @ApiOperation(value = "根据用户id查询行业权限")
    @GetMapping("/selectIndustryByUserId")
    public Result<List<IndustrySelectVO>> selectIndustryByUserId(@RequestParam("userId") String userId){
        return result(industyService.selectIndustryByUserId(userId));
    }

    /**
     * 根据用户查询所有的订购行业和未订购行业,app/小程序
     */
    @ApiOperation(value = "根据用户查询所有的订购行业和未订购行业,app/小程序")
    @GetMapping("/allByUser")
    public Result<List<IndustryConfDto>> allByUser(){
        return result(industyService.allByUser());
    }

    /**
     * 根据用户查询所有的订购行业和未订购行业,pc
     */
    @ApiOperation(value = "根据用户查询所有的订购行业和未订购行业,pc")
    @GetMapping("/allByUserPc")
    public Result<List<IndustryTypeDto>> allByUserPc(){
        return result(industyService.allByUserPc());
    }

    @ApiOperation(value = "查询配置用户的行业数量")
    @GetMapping("/allByUserCount")
    public Result<Integer> allByUserCount(){
        return Result.data(industyService.allByUserCount());
    }

    /**
     * 根据用户查询所有的订购行业和未订购行业
     */
    @ApiOperation(value = "根据用户查询所有的订购行业和未订购行业")
    @GetMapping("/detail/{industryCode}")
    public Result<IndustryConfDto> detailByIndustryCode(@PathVariable String industryCode){
        return Result.data(industyService.detailByIndustryCode(industryCode));
    }


    /**
     * 根据关键字 查询行业信息
     * @param industryQueryVO keyword
     * @return
     */
    @PostMapping("/queryIndustryByKeyWord")
    public Result<List<IndustrySelectVO>> queryIndustryByKeyWord(@RequestBody IndustryQueryVO industryQueryVO) {
        return Result.data(industyService.queryIndustryByKeyWord(industryQueryVO));
    }

    /**
     * 根据关键字 查询行业信息
     * @param industryQueryVO keyword
     * @return
     */
    @PostMapping("/initIndustryByKeyWord")
    public Result<List<IndustrySelectVO>> initIndustryByKeyWord(@RequestBody IndustryQueryVO industryQueryVO) {
        return Result.data(industyService.initIndustryByKeyWord(industryQueryVO));
    }

    /**
     * 根据关键字 查询版本信息
     * @param industryQueryVO keyword
     * @return
     */
    @PostMapping("/queryAppVerCodeByKeyWord")
    public Result<List<IndustrySelectVO>> queryAppVerCodeByKeyWord(@RequestBody IndustryQueryVO industryQueryVO) {
        return Result.data(industyService.queryAppVerCodeByKeyWord(industryQueryVO));
    }

}
