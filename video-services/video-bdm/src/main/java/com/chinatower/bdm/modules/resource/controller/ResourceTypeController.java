package com.chinatower.bdm.modules.resource.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.bdm.modules.resource.common.Constants;
import com.chinatower.bdm.modules.resource.service.IResourceConfService;
import com.chinatower.bdm.modules.resource.service.IResourceTypeService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * video-bdm
 * 资源类型管理controller
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Api(tags = "资源类型")
@RestController
@RequestMapping("/resourceType")
@Slf4j
public class ResourceTypeController extends TowerBaseController {
    @Autowired
    IResourceTypeService resourceTypeService;
    @Autowired
    IResourceConfService resourceConfService;
    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 获取资源类型顶级列表
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 资源列表数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源类型列表")
    @GetMapping("/selectResourceTypeList")
    public Result selectResourceTypeList(ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        resourceType.setQueryType(Constants.ONE_STR);
        List<ResourceType> list = resourceTypeService.selectResourceTypeList(resourceType);
        return Result.data(list);
    }

    
    /**
     *   获取资源类型顶级列表数
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 资源列表数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源类型顶级列表数")
    @PostMapping("/selectResourceTypeCount")
    public Result selectResourceTypeCount(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        resourceType.setQueryType(Constants.TWO_STR);
        List<ResourceType> list = resourceTypeService.selectResourceTypeList(resourceType);
        return Result.data(list.size());
    }
    
    /**
     *   根据资源类型编号获取详细信息
     * @author zhaol
     * @param id 资源类型编号
     * @return 资源列表数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "根据资源类型编号获取详细信息")
    @GetMapping(value = "/detail/{id}")
    public Result getInfo(@PathVariable Long id) {
        return Result.data(resourceTypeService.getInfo(id));
    }

    /**
     *   新增资源类型
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 新增结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "新增资源类型")
    @TowerLog(title = "资源类型类型", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        if (resourceTypeService.checkResourceTypeNameUnique(resourceType)) {
            return Result.fail(Constants.CHECK_TYEP_MSG);
        }
        resourceType.setCreator(SecurityUtils.getOperator());
        String icon = resourceType.getIcon();
        if(icon.indexOf(Constants.WENHAO)> Constants.fu_ONE_NUMBER){
            resourceType.setIcon(icon.substring(Constants.ZERO_NUMBER,icon.indexOf(Constants.WENHAO)));
        }
        return Result.data(resourceTypeService.insertResourceType(resourceType));
    }

    /**
     *   修改资源类型
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 修改结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "修改资源类型")
    @TowerLog(title = "资源类型管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        if(!Constants.ONE_STR.equals(resourceType.getApproveFlag())){
            if (resourceTypeService.checkResourceTypeNameUnique(resourceType)) {
                return Result.fail(Constants.CHECK_TYEP_MSG);
            }
            //资源管理优化需求 add by liuxt start
            ResourceConf resourceConf = new ResourceConf();
            resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
            resourceConf.setTenantId(loginUser.getUser().getTenantId());
            resourceConf.setResourceTypeId(resourceType.getResourceTypeId());
            List<ResourceConf> resourceConflist = resourceConfService.selectResourceList(resourceConf);
            if(!CollectionUtils.isEmpty(resourceConflist)){
                if(!resourceType.getResourceProperty().equals(resourceConflist.get(0).getResourceProperty())){
                    //判断数据库的资源属性和入参资源属性是否一致，若不一致，则返回失败提示
                    return Result.fail(Constants.CHECK_RESOURCE_PROPERTY);
                }
            }
            //资源管理优化需求 add by liuxt end
            String icon = resourceType.getIcon();
            if(icon.indexOf(Constants.WENHAO)> Constants.fu_ONE_NUMBER){
                resourceType.setIcon(icon.substring(Constants.ZERO_NUMBER,icon.indexOf(Constants.WENHAO)));
            }
        }
        resourceType.setModifier(SecurityUtils.getOperator());
        return Result.data(resourceTypeService.updateResourceType(resourceType));
    }
    
    /**
     *   删除资源类型
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 删除结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "删除资源类型")
    @TowerLog(title = "资源类型管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/del")
    public Result remove(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (resourceTypeService.hasChildByResourceTypeId(resourceType)) {
            return Result.fail(Constants.CHECK_TYEP_DEL_MSG);
        }else if(resourceTypeService.checkResourceUnique(resourceType)){
            return Result.fail(Constants.CHECK_TYEP_DEL_MSG);
        }
        resourceType.setDeleteFlag(Constants.ONE_NUMBER);
        resourceType.setModifier(SecurityUtils.getOperator());
        return Result.data(resourceTypeService.deleteResourceTypeById(resourceType));
    }

    
    /**
     *   启用停用资源类型
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 启用停用操作结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "启用停用资源类型")
    @TowerLog(title = "启用停用资源类型", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateStatus")
    public Result updateStatus(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (Constants.ONE_STR.equals(resourceType.getStatus()) && resourceTypeService.hasChildByResourceTypeId(resourceType)) {
            return Result.fail(Constants.CHECK_TYEP_CHILD_MSG_2);
        }else if(Constants.ONE_STR.equals(resourceType.getStatus()) && resourceTypeService.checkResourceUnique(resourceType)) {
            return Result.fail(Constants.CHECK_TYEP_REOSURCE_MSG_2);
        }
        resourceType.setModifier(SecurityUtils.getOperator());
        return Result.data(resourceTypeService.deleteResourceTypeById(resourceType));
    }

    /**
     *   获取资源类型树(铁塔视角也调用该接口，需注意)
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 资源类型数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源类型顶级列表(顶级结构)")
    @GetMapping("/selectResourceTypeTree")
    public Result selectResourceTypeTree(ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(StringUtils.isEmpty(resourceType.getTenantId())){//铁塔视角传租户id
            resourceType.setTenantId(loginUser.getUser().getTenantId());
        }
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())){
            resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
            //add by liuxt 增加行业版本编码
            resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        }
        resourceType.setParentId(Constants.ZERO_STR);
        //资源管理优化需求 add by liuxt
        List<String> resourceIndustry = getDict(Constants.RESOURCE_INDUSTRY);
        //铁塔视角正常返回线资源，面资源，其余行业需在字典表配置才正常返回线资源，面资源
        if(!Constants.TOWER_INDUSTRY.equals(loginUser.getUser().getIndustryCode()) && !CollectionUtils.isEmpty(resourceIndustry) && !resourceIndustry.contains(resourceType.getIndustryCode())){
            resourceType.setResourceProperty(Constants.ONE_STR);
        }
        List<ResourceType> list = resourceTypeService.selectResourceTypeListNew(resourceType);
        return Result.data(resourceTypeService.buildResourceTypeTreeSelect(list));
    }


    /**
     *   获取资源类型树(铁塔视角也调用该接口，需注意),所有资源类型数据均返回
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 资源类型数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源类型顶级列表(顶级结构)")
    @GetMapping("/selectResourceTypeTreeNew")
    public Result selectResourceTypeTreeNew(ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(StringUtils.isEmpty(resourceType.getTenantId())){//铁塔视角传租户id
            resourceType.setTenantId(loginUser.getUser().getTenantId());
        }
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())){
            resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
            //add by liuxt 增加行业版本编码
            resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        }
        resourceType.setParentId(Constants.ZERO_STR);
        List<ResourceType> list = resourceTypeService.selectResourceTypeListNew(resourceType);
        return Result.data(resourceTypeService.buildResourceTypeTreeSelect(list));
    }


    /**
     * 资源类型查询接口
     * @author zhaol
     * @param resourceType 资源类型信息，接收前端参数以供查询使用。
     * @return 资源类型数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "资源类型查询接口")
        @PostMapping("/selectResourceType")
    public Result selectResourceType(@RequestBody ResourceType resourceType) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceType.setTenantId(loginUser.getUser().getTenantId());
        resourceType.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceType.setAppVerCode(loginUser.getUser().getAppVerCode());
        //资源管理优化需求 add by liuxt
        List<String> resourceIndustry = getDict(Constants.RESOURCE_INDUSTRY);
        if(!CollectionUtils.isEmpty(resourceIndustry) && !resourceIndustry.contains(resourceType.getIndustryCode())){
            resourceType.setResourceProperty(Constants.ONE_STR);
        }
        List<ResourceType> list = resourceTypeService.selectResourceTypeList(resourceType);
        return Result.data(list);
    }

    /**
     * 初始化资源类型
     * @author zhaol
     * @param resourceTypeInt 资源类型初始化信息
     * @return 资源类型数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "初始化资源类型")
    @TowerLog(title = "初始化资源类型", businessType = BusinessType.INSERT)
    @PostMapping(value = "/initResourceType")
    public Result initResourceType(@RequestBody ResourceTypeInt resourceTypeInt) {
        return resourceTypeService.initResourceType(resourceTypeInt);
    }


    /**
     * 获取可调资源接口的行业列表（共通方法）
     *
     * @param dataType
     * @return
     */
    public List<String> getDict(String dataType) {
        List<String> dictList = new ArrayList<>();
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(dataType);
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            List<SysDictDataResponse> data = sysResult.getData();
            if(!CollectionUtils.isEmpty(data)){
                for (int i = 0; i < data.size(); i++) {
                    dictList.add(data.get(i).getDictValue());
                }
            }
        }
        return dictList;
    }
}
