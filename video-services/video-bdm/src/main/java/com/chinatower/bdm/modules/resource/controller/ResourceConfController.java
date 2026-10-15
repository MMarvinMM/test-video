package com.chinatower.bdm.modules.resource.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.resource.common.Constants;
import com.chinatower.bdm.modules.resource.mapper.ResourceConfMapper;
import com.chinatower.bdm.modules.resource.mapper.ResourceTypeMapper;
import com.chinatower.bdm.modules.resource.service.IResourceConfService;
import com.chinatower.bdm.modules.resource.service.IResourceTypeService;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.bdm.common.Constants.TIME_LIME_CONSTANT.LANDLINE_REGEX;
import static com.chinatower.bdm.common.Constants.TIME_LIME_CONSTANT.PHONE_REGEX;

/**
 * video-bdm
 * 资源管理controller
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Api(tags = "资源")
@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceConfController  extends TowerBaseController {
    @Autowired
    IResourceConfService resourceConfService;
    @Autowired
    IResourceTypeService resourceTypeService;
    @Autowired
    private ISysImportService importService;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private ResourceTypeMapper resourceTypeMapper;
    @Autowired
    private ResourceConfMapper resourceConfMapper;
    @Autowired
    private ResourceTypeController resourceTypeController;

    /**
     * 获取资源列表
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数以供查询使用。
     * @return 资源列表数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源列表")
    @PostMapping("/selectResourceConfList")
    public TableDataInfo selectResourceConfList(@RequestBody ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        startPage();
        //资源管理优化需求 add by liuxt
        List<String> resourceIndustry = resourceTypeController.getDict(Constants.RESOURCE_INDUSTRY);
        if(!CollectionUtils.isEmpty(resourceIndustry) && !resourceIndustry.contains(resourceConf.getIndustryCode())){
            resourceConf.setResourceProperty(Constants.ONE_STR);
        }
        List<ResourceConf> list = resourceConfService.selectResourceList(resourceConf);
        //整改for循环 modify by liuxt 20240806
        list = dealUrlBatch(list);
        return getDataTable(list);
    }

    /**
     * 获取资源列表
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数以供查询使用。
     * @return 资源列表数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "获取资源列表")
    @PostMapping("/selectResourceConfListNew")
    public TableDataInfo selectResourceConfListNew(@RequestBody ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        startPage();
        List<ResourceConf> list = resourceConfService.selectResourceList(resourceConf);
        //整改for循环 modify by liuxt 20240806
        list = dealUrlBatch(list);
        return getDataTable(list);
    }

    /**
     * 根据资源编号获取详细信息
     * @author zhaol
     * @param id 资源编号
     * @return 资源数据
     * @throws
     * @throws
     * @requestID
     */
    @ApiOperation(value = "根据资源编号获取详细信息")
    @GetMapping(value = "/detail/{id}")
    public Result getInfo(@PathVariable Long id) {
        ResourceConf resourceInfo = resourceConfService.getInfo(id);

        setDesTel(resourceInfo);

        // 处理Obs文件URL加密返回
        String imgUrl = resourceInfo.getImgUrl();
        resourceInfo.setImgUrl(resourceConfService.resourceImgVideo(imgUrl));
        String videoUrl = resourceInfo.getVideoUrl();
        resourceInfo.setVideoUrl(resourceConfService.resourceImgVideo(videoUrl));
        return Result.data(resourceInfo);
    }

    /**
     * 根据资源编号获取详细信息
     * @author zhaol
     * @param resourceId 资源信息编码
     * @return 资源数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "根据资源编码获取详细信息")
    @GetMapping(value = "/getResourceInfo")
    public Result getResourceInfo(String resourceId) {
        ResourceConf resourceInfo = resourceConfService.getResourceInfo(resourceId);
        setDesTel(resourceInfo);
        return Result.data(resourceInfo);
    }


    /**
     * 根据资源编号获取详细信息(行业应用)
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数以供查询使用。
     * @return 资源数据
     * @throws
     * @requestID
     */
    @ApiOperation(value = "根据资源编码获取详细信息")
    @PostMapping(value = "/getResourceInfoDetail")
    public Result getResourceInfoDetail(@RequestBody ResourceConf resourceConf) {
        ResourceConf resourceInfo = resourceConfService.getResourceInfo(resourceConf.getResourceId());
        setDesTel(resourceInfo);
        if(!ObjectUtils.isEmpty(resourceInfo)){
            // 处理Obs文件URL加密返回
            resourceInfo.setIcon(fileUrlEncryptUtil.dealObsFileUrl(resourceInfo.getIcon()));
        }
        return Result.data(resourceInfo);
    }

    /**
     * 新增资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 新增结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "新增资源")
    @TowerLog(title = "新增资源", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@RequestBody @Valid ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        if (resourceConfService.checkResourceNameUnique(resourceConf)) {
            return Result.fail(Constants.CHECK_RESOURCE_MSG);
        }
        //资源管理优化需求 add by liuxt
        String resourceProperty = resourceConf.getResourceProperty();//资源属性
        String geometry = resourceConf.getGeometry();//绘制资源范围
        String color = resourceConf.getColor();//绘制资源颜色
        if(Constants.TWO_STR.equals(resourceProperty) || Constants.THREE_STR.equals(resourceProperty)){
            if(StringUtils.isEmpty(geometry)){
                return Result.fail(Constants.CHECK_GEOMETRY);
            }
            if(StringUtils.isEmpty(color)){
                return Result.fail(Constants.CHECK_COLOR);
            }
        }
        ResourceType record = new ResourceType();
        record.setTenantId(loginUser.getUser().getTenantId());
        record.setResourceTypeId(resourceConf.getResourceTypeId());
        List<ResourceType> resourceTypeList = resourceTypeMapper.selectResourceTypeListNew(record);
        if(CollectionUtils.isEmpty(resourceTypeList)){
            return Result.fail(Constants.CHECK_TYEP_NOT_MSG);
        }

        resourceConf.setCreator(SecurityUtils.getOperator());
        resourceConf.setModifier(SecurityUtils.getOperator());
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        return Result.data(resourceConfService.insertResource(resourceConf));
    }

    /**
     * 修改资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 新增结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "修改资源")
    @TowerLog(title = "资源管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@RequestBody @Valid ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        if (resourceConfService.checkResourceNameUnique(resourceConf)) {
            return Result.fail(Constants.CHECK_RESOURCE_MSG);
        }
        //资源管理优化需求 add by liuxt
        String resourceProperty = resourceConf.getResourceProperty();//资源属性
        String geometry = resourceConf.getGeometry();//绘制资源范围
        String color = resourceConf.getColor();//绘制资源颜色
        if(Constants.TWO_STR.equals(resourceProperty) || Constants.THREE_STR.equals(resourceProperty)){
            if(StringUtils.isEmpty(geometry)){
                return Result.fail(Constants.CHECK_GEOMETRY);
            }
            if(StringUtils.isEmpty(color)){
                return Result.fail(Constants.CHECK_COLOR);
            }
        }
        ResourceType record = new ResourceType();
        record.setTenantId(loginUser.getUser().getTenantId());
        record.setResourceTypeId(resourceConf.getResourceTypeId());
        List<ResourceType> resourceTypeList = resourceTypeMapper.selectResourceTypeListNew(record);
        if(CollectionUtils.isEmpty(resourceTypeList)){
            return Result.fail(Constants.CHECK_TYEP_NOT_MSG);
        }

        resourceConf.setModifier(SecurityUtils.getOperator());
        return Result.data(resourceConfService.updateResource(resourceConf));
    }

    /**
     * 删除资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 删除结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "删除资源")
    @TowerLog(title = "资源管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/del")
    public Result remove(@RequestBody ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setDeleteFlag(Constants.ONE_NUMBER);
        resourceConf.setModifier(SecurityUtils.getOperator());
        resourceConf.setUpdateTime(new Date());
        return Result.data(resourceConfService.deleteResourceById(resourceConf));
    }


    /**
     * 资源搜索接口/周边资源搜索
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 搜索结果
     * @throws
     * @requestID
     */
    @ApiOperation(value = "资源搜索接口/周边资源搜索接口")
    @PostMapping("/selectResource")
    public Result selectResource(@RequestBody ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        //add by liuxt 增加行业版本编码
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        resourceConf.setPageNum(null);
        resourceConf.setPageSize(null);
        resourceConf.setStatus(Constants.ZERO_STR);
        resourceConf.setUserId(loginUser.getUser().getUserId());
        //资源管理优化需求 add by liuxt
        List<String> resourceIndustry = resourceTypeController.getDict(Constants.RESOURCE_INDUSTRY);
        if(!CollectionUtils.isEmpty(resourceIndustry) && !resourceIndustry.contains(resourceConf.getIndustryCode())){
            resourceConf.setResourceProperty(Constants.ONE_STR);
        }
        log.info("test ffff isMonitor:{}",resourceConf.getIsMonitor());
        List<ResourceConf> list = resourceConfService.selectResourceList(resourceConf);
        //add by liuxt 20220826
        if(!CollectionUtils.isEmpty(list)){
            Set<String> fileUrls = list.stream().map(ResourceConf::getIcon).collect(Collectors.toSet());
            // 处理Obs文件URL加密返回
            //add by liuxt video-file改造
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
            String fileUrl;
            for (ResourceConf f : list) {
                fileUrl = f.getIcon();
                if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(fileUrl)) {
                    f.setIcon(result.get(fileUrl));
                }
            }
            List<String> imgUrlList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {//得到要加密的urlList
                imgUrlList.addAll(strToList(list.get(i).getImgUrl()));
            }
            if (!CollectionUtils.isEmpty(imgUrlList)) {
                try {
                    Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(imgUrlList);
                    // 处理Obs文件URL加密返回
                    if (stringStringMap != null && stringStringMap.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setImgUrl(dealStr(list.get(i).getImgUrl(), stringStringMap));
                        }
                    }
                } catch (Exception e) {
                    log.error(com.chinatower.event.constants.Constants.FILE_ERROR, e);
                }
            }

        }
        return Result.data(list);
    }

    /**
     * 资源树
     * @author zhaol
     * @param vo
     * @return 资源数
     * @throws
     * @requestID     *
     */
    @ApiOperation(value = "资源树",notes = "资源树")
    @PostMapping(value = "/getResourceTree")
    public AjaxResult getResourceTree(@RequestBody ResourceSelectVO vo) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null == vo.getTenantId() || vo.getTenantId().isEmpty()) {
            vo.setTenantId(currentUser.getUser().getTenantId());
        }
        if (null == vo.getIndustryCode() || vo.getIndustryCode().isEmpty()) {
            vo.setIndustryCode(currentUser.getUser().getIndustryCode());
        }
        if (null == vo.getAppVerCode() || vo.getAppVerCode().isEmpty()) {
            //add by liuxt 增加行业版本编码
            vo.setAppVerCode(currentUser.getUser().getAppVerCode());
        }
        //资源管理优化需求 add by liuxt
        List<String> resourceIndustry = resourceTypeController.getDict(Constants.RESOURCE_INDUSTRY);
        if(!CollectionUtils.isEmpty(resourceIndustry) && !resourceIndustry.contains(vo.getIndustryCode())){
            vo.setResourceProperty(Constants.ONE_STR);
        }
        List<TreeSelectByBdm> treeSelectByBdmList = resourceConfService.getResourceTree(vo);
        return AjaxResult.success(treeSelectByBdmList);
    }


    /**
     * 资源导入
     * @author zhaol
     * @param file 导入文件输入流
     * @param importType 导入类型
     * @param fileUrl 文件url
     * @param updateSupport
     * @return 导入结果
     * @throws
     * @requestID
     */
    @TowerLog(title = "资源管理", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(MultipartFile file, String importType, String fileUrl, boolean updateSupport) throws Exception {
        List<ResourceVO> list = new ExcelUtil<>(ResourceVO.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            throw new CustomException("导入数据不能为空！");
        }
        ImportParams<ResourceVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put("loginUser", SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> resourceConfService.importResource(p));
    }

    /**
     * 2.1.执法资源统计基础版
     *
     * @return 返回执法资源统计基础版结果
     */
    @PostMapping("/selectResourceForTypeCount")
    public Result<List<AllResourceTypeNumVO>> selectResourceForTypeCount(@RequestBody ResourceConf resourceConf) {
        return resourceConfService.selectResourceForTypeCount(resourceConf);
    }

    public List<ResourceConf> dealUrlBatch(List<ResourceConf> list){
        if(!CollectionUtils.isEmpty(list)){
            Set<String> fileUrls = list.stream().map(ResourceConf::getIcon).collect(Collectors.toSet());
            if(!fileUrls.isEmpty()){
                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                String fileUrl = "";
                for (ResourceConf f : list) {
                    fileUrl = f.getIcon();
                    if (!StringUtils.isEmpty(fileUrl)) {
                        f.setIcon(result.get(fileUrl));
                    }
                }
            }
        }
        return list;
    }


    public List<String> strToList(String url){//逗号拼接的字符串转为list
        List<String> returnList = new ArrayList<>();
        if(!StringUtils.isEmpty(url)){
            String[] arr = url.split(com.chinatower.event.constants.Constants.COMMA);
            List<String> list = Arrays.asList(arr);
            for (int j = 0; j < list.size(); j++) {
                returnList.add(list.get(j));
            }
        }
        return returnList;
    }

    public String dealStr(String url,Map<String, String> stringStringMap){//带逗号的字符串处理成加密的
        String urlString = "";
        // 处理Obs文件URL加密返回
        if (stringStringMap != null && stringStringMap.size() > 0) {
            if(!StringUtils.isEmpty(url)){
                String[] arr = url.split(com.chinatower.event.constants.Constants.COMMA);
                List<String> list = Arrays.asList(arr);
                for (int j = 0; j < list.size(); j++) {
                    urlString += stringStringMap.get(list.get(j)) + com.chinatower.event.constants.Constants.COMMA ;
                }
                urlString = urlString.substring(0,urlString.length()-1);
            }
        }
        return urlString;
    }


    public void setDesTel(ResourceConf resourceInfo) {
        if (StringUtil.isNotEmpty(resourceInfo.getTel())) {
            if (resourceInfo.getTel().matches(PHONE_REGEX)) {
                resourceInfo.setDesTel(DesensitizedUtil.mobilePhone(resourceInfo.getTel()));
            } else if (resourceInfo.getTel().matches(LANDLINE_REGEX)) {
                resourceInfo.setDesTel(DesensitizedUtil.fixedPhone(resourceInfo.getTel()));
            }
        }
    }


}
