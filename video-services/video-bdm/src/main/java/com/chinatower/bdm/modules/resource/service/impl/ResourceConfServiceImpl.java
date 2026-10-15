package com.chinatower.bdm.modules.resource.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.common.NonceUtil;
import com.chinatower.bdm.common.TreeByBdmUtils;
import com.chinatower.bdm.modules.resource.mapper.ResourceConfMapper;
import com.chinatower.bdm.modules.resource.mapper.ResourceTypeMapper;
import com.chinatower.bdm.modules.resource.service.IResourceConfService;
import com.chinatower.common.entity.Result;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.gis.api.GisAPI;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 资源管理service实现类
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Service("resourceConfService")
@Slf4j
public class ResourceConfServiceImpl implements IResourceConfService {
    @Autowired
    ResourceConfMapper resourceConfDao;
    @Autowired
    ResourceTypeMapper resourceTypeDao;
    @Autowired
    private GisAPI gisAPI;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private IVideoManageRemoteService videoManageRemoteService;

    public static final String RESOURCE_TYPE_1 = "1";


    /**
     * 新增资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 新增结果
     * @throws
     * @requestID
     */
    @Override
    public int insertResource(ResourceConf resourceConf) {
        String resourceTypeId = NonceUtil.getNonceStr();
        resourceConf.setResourceId(resourceTypeId);
        resourceConf.setUpdateTime(new Date());
        resourceConf.setCreateTime(new Date());
        // 同步大屏配置
        syncScreenDataConfig(resourceConf);
        return resourceConfDao.insertSelective(resourceConf);
    }

    private void syncScreenDataConfig(ResourceConf resourceConf){
        log.info("新增资源同步大屏配置入参："+ JSONObject.toJSONString(resourceConf));
        List<ScreenDataConfigVO> screenDataConfigVOList = new ArrayList<>();
        ScreenDataConfigVO configVO = new ScreenDataConfigVO();
        List<String> resourceCodeList = new ArrayList<>();
        configVO.setTenantId(resourceConf.getTenantId());
        configVO.setIndustryCode(resourceConf.getIndustryCode());
        resourceCodeList.add(resourceConf.getResourceId().toString());
        configVO.setResourcesCodeList(resourceCodeList);
        screenDataConfigVOList.add(configVO);
        videoManageRemoteService.addConfig(screenDataConfigVOList);
    }

    /**
     * 修改资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 修改结果
     * @throws
     * @requestID
     */
    @Override
    public int updateResource(ResourceConf resourceConf) {
        resourceConf.setUpdateTime(new Date());
        return resourceConfDao.updateByPrimaryKeySelective(resourceConf);
    }

    /**
     * 根据id获取资源详情
     * @author zhaol
     * @param id 资源id
     * @return 类型详情
     * @throws
     * @requestID
     */
    @Override
    public ResourceConf getInfo(Long id){
        return resourceConfDao.selectByPrimaryKey(id);
    }

    /**
     * 根据resourceId获取资源详情
     * @author zhaol
     * @param resourceId 资源信息编码
     * @return 类型详情
     * @throws
     * @requestID
     */
    @Override
    public ResourceConf getResourceInfo(String resourceId){
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        ResourceConf resourceConf = resourceConfDao.selectByResourceId(resourceId, userId);
        if(!ObjectUtils.isEmpty(resourceConf)){
            resourceConf.setIcon(fileUrlEncryptUtil.dealObsFileUrl(resourceConf.getIcon()));
            String imgUrl = resourceConf.getImgUrl();
            resourceConf.setImgUrl(resourceImgVideo(imgUrl));
            String videoUrl = resourceConf.getVideoUrl();
            resourceConf.setVideoUrl(resourceImgVideo(videoUrl));
        }
        return resourceConf;
    }

    /**
     * 删除资源
     * @author zhaol
     * @param resourceConf 资源信息，接收前端参数。
     * @return 删除结果
     * @throws
     * @requestID
     */
    @Override
    public int deleteResourceById(ResourceConf resourceConf) {
        return resourceConfDao.updateByResourceId(resourceConf);
    }

    /**
     * 是否存在资源名称相同
     * @author zhaol
     * @param  resourceConf 资源
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    @Override
    public boolean checkResourceNameUnique(ResourceConf resourceConf) {
        return resourceConfDao.checkResourceNameUnique(resourceConf) > 0;
    }

    /**
     * 获取资源顶级列表(顶级结构)
     * @author zhaol
     * @param resourceConf 资源信息
     * @return 返回资源集合
     * @throws
     * @requestID
     */
    @Override
    @DataScope(resourceColumn = "d.RESOURCE_ID")
    public List<ResourceConf> selectResourceList(ResourceConf resourceConf) {
        return resourceConfDao.selectResourceList(resourceConf);
    }



    /**
     * 获取租户资源集合(tree格式用)
     * @param voIn 资源信息
     * @return 返回资源集合
     * @author zhaol
     * @throws
     * @requestID       *
     */
    @DataScope(resourceColumn = "d.RESOURCE_ID")
    @Override
    public List<TreeSelectByBdm> getResourceTree(ResourceSelectVO voIn) {
        List<TreeSelectByBdm> tree= new ArrayList<>();
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        voIn.setUserId(userId);
        List<TreeSelectByBdm> list = resourceConfDao.getResourceTree(voIn);

        //add by liuxt 20220826
        if(!CollectionUtils.isEmpty(list)){
            Set<String> fileUrls = list.stream().map(TreeSelectByBdm::getIcon).collect(Collectors.toSet());
            // 处理Obs文件URL加密返回
            //add by liuxt video-file改造
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
            String fileUrl;
            for (TreeSelectByBdm f : list) {
                fileUrl = f.getIcon();
                if (StringUtils.isNotEmpty(fileUrl)) {
                    // 截取第一个，因为前面加密是截取了
                    fileUrl = fileUrl.split(",")[0];
                    f.setIcon(result.get(fileUrl));
                }
            }
        }

        for(TreeSelectByBdm item: list){
            ResourceConfTree vo = new ResourceConfTree();
            vo.setIcon(item.getIcon());
            vo.setId(item.getId());
            vo.setPid(item.getPid());
            vo.setLabel(item.getLabel());
            vo.setType(item.getType());
            vo.setResourceProperty(item.getResourceProperty());
            vo.setIsMonitor(item.getIsMonitor());
            vo.setIsEmergency(item.getIsEmergency());
            tree.add(new TreeSelectByBdm(vo));
        }
        List<TreeSelectByBdm> resourceTree = TreeByBdmUtils.listToTreeSelect(tree);
        resourceTree.forEach(ResourceConfServiceImpl::updateParentMonitor);
        return resourceTree;
    }



    /**
     * 更新父节点的monitor状态。
     * 如果所有资源子节点的monitor都为1，则父节点的monitor也设为1。
     * @param node 父节点
     */

    private static void updateParentMonitor(TreeSelectByBdm node) {

        if (RESOURCE_TYPE_1.equals(node.getType())) {
            return;
        }

        List<Boolean> flagStr = new ArrayList<>();

        if (null != node.getChildren()) {
            node.getChildren().forEach(child1 -> {
                //资源
                if (RESOURCE_TYPE_1.equals(child1.getType())) {
                    flagStr.add(child1.getIsMonitor() == 1);
                } else {
                    //资源类型
                    boolean flag = true;
                    if (null != child1.getChildren()) {
                        for (TreeSelectByBdm child2 : child1.getChildren()) {
                            if (RESOURCE_TYPE_1.equals(child2.getType())) {
                                if (1 != child2.getIsMonitor()) {
                                    flag = false;
                                }
                                flagStr.add(flag);
                            }
                        }
                        child1.setIsMonitor(flag ? 1 : 0);
                    }
                }
            });
            if (!flagStr.isEmpty()) {
                if (flagStr.stream().allMatch(Boolean.TRUE::equals)) {
                    node.setIsMonitor(1);
                }
            }
        }
    }


    /**
     * 资源导入
     * @author zhaol
     * @param params 导入参数
     * @return 结果
     * @throws
     * @requestID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysImportDetail> importResource(ImportParams<ResourceVO> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        int count = 0;
        ResourceVO loginUser = MapUtil.get(params.getExtraParams(),"loginUser", ResourceVO.class);
        ResourceType recordType = new ResourceType();
        recordType.setTenantId(loginUser.getTenantId());
        recordType.setIndustryCode(loginUser.getIndustryCode());
        //add by liuxt 增加行业版本编码
        recordType.setAppVerCode(loginUser.getAppVerCode());
        List<ResourceType> resourceTypelist = resourceTypeDao.selectResourceTypeList(recordType);
        ResourceConf paramResource = new ResourceConf();
        paramResource.setTenantId(loginUser.getTenantId());
        paramResource.setIndustryCode(loginUser.getIndustryCode());
        //add by liuxt 增加行业版本编码
        paramResource.setAppVerCode(loginUser.getAppVerCode());
        paramResource.setPageNum(null);
        paramResource.setPageSize(null);
        List<ResourceConf> resourcelist = resourceConfDao.selectResourceList(paramResource);
        List<String> resourceNamelist = resourcelist.stream().map(ResourceConf::getResourceName).collect(Collectors.toList());
        List<ResourceVO> resourcesToInsert = new ArrayList<>();
        for (ResourceVO resource : params.getList()) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(),rowNum);
                // 新增资源属性验证必须是点资源
                if(StrUtil.isBlank(resource.getResourceProperty()) || !Constants.CHECK_EXEL_MSG_10.equals(resource.getResourceProperty())){
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_9);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                if(StrUtil.isNotBlank(resource.getResourceTypeName())){
                    for(ResourceType resourceType:resourceTypelist){
                        if(resourceType.getResourceTypeName().equals(resource.getResourceTypeName()) && "1".equals(resourceType.getResourceProperty())){
                            resource.setResourceTypeId(resourceType.getResourceTypeId());
                            //资源管理优化需求 add by liuxt
                            resource.setResourceProperty(resourceType.getResourceProperty());
                        }
                    }
                }else {
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_5);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                if(StrUtil.isEmpty(resource.getResourceTypeId())){
                    detail.setFailReason(resource.getResourceTypeName()+Constants.CHECK_EXEL_MSG_6);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                if(StrUtil.isNotBlank(resource.getResourceName())){
                    if(resourceNamelist.contains(resource.getResourceName())){
                        detail.setFailReason(resource.getResourceName()+Constants.CHECK_EXEL_MSG_4);
                        details.add(detail);
                        rowNum++;
                        continue;
                    }
                }else {
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_3);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                //资源管理优化需求 add by liuxt 点资源1时，经纬度为必填
                if(Constants.ONE.equals(resource.getResourceProperty()) && StrUtil.isBlank(resource.getLongitude())){
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_1);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                //资源管理优化需求 add by liuxt 点资源1时，经纬度为必填
                if(Constants.ONE.equals(resource.getResourceProperty()) && StrUtil.isBlank(resource.getLatitude())){
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_2);
                    details.add(detail);
                    rowNum++;
                    continue;
                }
                if(!Constants.ONE.equals(resource.getResourceProperty())){//不为点资源，经纬度,高程置为空 add by liuxt
                    resource.setLongitude("");
                    resource.setLatitude("");
                    resource.setElevation("");
                }
                if(StrUtil.isBlank(resource.getAddr())){
                    detail.setFailReason(Constants.CHECK_EXEL_MSG_8);
                    details.add(detail);
                    rowNum++;
                    continue;
                }

                if(!StrUtil.isBlank(resource.getRemark())){
                    if (200 < resource.getRemark().length()) {
                        detail.setFailReason(Constants.CHECK_EXCEL_REMARK_ERR_MSG);
                        details.add(detail);
                        rowNum++;
                        continue;
                    }
                }

                if(!StrUtil.isBlank(resource.getAdmin())){
                    if (20 < resource.getAdmin().length()) {
                        detail.setFailReason(Constants.CHECK_EXCEL_ADMIN_LENGTH_ERR_MSG);
                        details.add(detail);
                        rowNum++;
                        continue;
                    }
                }


                    // 正则表达式
                String PHONE_REGEX = "^1[3-9]\\d{9}$";
                String LANDLINE_REGEX = "^(0\\d{2,3}-)?\\d{7,8}$";

                if(!StrUtil.isBlank(resource.getTel())){
                    if (!resource.getTel().matches(PHONE_REGEX) && !resource.getTel().matches(LANDLINE_REGEX)) {
                        detail.setFailReason(Constants.CHECK_EXCEL_TEL_ERR_MSG);
                        details.add(detail);
                        rowNum++;
                        continue;
                    }
                }
                //增加行数
                rowNum++;
                detail.setHandleResult(com.chinatower.platform.common.constant.Constants.S);

                resource.setResourceId(UUID.randomUUID().toString().replace(Constants.LINE_CENTER, Constants.EMPTY_STR));
                resource.setTenantId(loginUser.getTenantId());
                resource.setDeleteFlag(Constants.NUMBER_ZERO);
                resource.setStatus(Constants.ZERO_STR);
                resource.setCreateTime(new Date());
                resource.setUpdateTime(new Date());
                resource.setCreator(loginUser.getCreator());
                resource.setModifier(loginUser.getCreator());
                resource.setIndustryCode(loginUser.getIndustryCode());
                resource.setAppVerCode(loginUser.getAppVerCode());
                resource.setLatitude(this.formatLonLat(resource.getLatitude(),9));
                resource.setLongitude(this.formatLonLat(resource.getLongitude(),9));
                details.add(detail);
                resourcesToInsert.add(resource);
        }
        //优化for循环调用
        try {
            if(StringUtils.isNotEmpty(resourcesToInsert)){
                count = resourceConfDao.insertResourceExportBatch(resourcesToInsert);
                if (count <= Constants.NUMBER_ZERO) {
                    details.forEach(item -> item.setFailReason(Constants.DATA_ERROR_MSG));
                }
            }
        }catch (Exception e) {
            log.error("批量资源导入失败：", e);
            details.get(0).setFailReason(Constants.CHECK_ERROR_MSG + ExceptionUtil.stacktraceToString(e, Constants.ONE_THOUSAND));
        }
        return details;
    }

    /**
     * 保留scale位小数,不够补0，多了四舍五入
     * @author hued
     * @param str 经/纬度
     * @date 2022年9月6日19:18:55
     * @return
     * @throws
     * @requestID
     */
    private String formatLonLat(String str,int scale){
        if(!StringUtils.isEmpty(str)){
            double v = Double.parseDouble(str);
            if(scale > 0) {
                String formatStr = "0.";
                for(int i = 0;i < scale;i++){
                    formatStr = formatStr + Constants.ZERO;
                }
                str = new DecimalFormat(formatStr).format(v);
            }
        }
        return str;
    }

    /**
     * 资源详情附件obs加密
     * @author wangshn
     * @param url 逗号间隔url
     * @Data 2023年1月4日17:43:55
     * @return
     * @throws
     * @requestID
     */
    @Override
    public String resourceImgVideo(String url){
        String urlString = "";
        if(!StringUtil.isEmpty(url)){
            List<String> urlArray = Arrays.asList(url.split(com.chinatower.bdm.modules.resource.common.Constants.COMMA_STR));
            //add by liuxt video-file改造
            Map<String, String> urlList = fileUrlEncryptUtil.dealObsFileUrlBatch(urlArray);
            if (!CollectionUtils.isEmpty(urlList)) {
                for(String i : urlList.keySet()){
                    urlString +=urlList.get(i) + com.chinatower.bdm.modules.resource.common.Constants.COMMA_STR;
                }
            }
            urlString = urlString.substring(0,urlString.length()-1);
        }
        return urlString;
    }

    @Override
    @DataScope(resourceColumn = "d.RESOURCE_ID")
    public Result<List<ResourceConf>> getResourceByPeriphery(AroundAnalysisParamVO aroundAnalysisParamVO) {
        ResourceConf resourceConf = new ResourceConf();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceConf.setLongitude(aroundAnalysisParamVO.getLongitude());
        resourceConf.setLatitude(aroundAnalysisParamVO.getLatitude());
        resourceConf.setRadius(aroundAnalysisParamVO.getRadius());
        resourceConf.setDataScope(aroundAnalysisParamVO.getDataScope());
        List<ResourceConf> list = selectResourceList(resourceConf);
        if(!CollectionUtils.isEmpty(list)) {
            List<String> iconList = list.stream().map(ResourceConf::getIcon).collect(Collectors.toList());
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(iconList);
            // 处理Obs文件URL加密返回
            list.stream().forEach(item -> item.setIcon(stringStringMap.get(item.getIcon())));
        }
        return Result.data(list);
    }

    @Override
    @DataScope(resourceColumn = "d.RESOURCE_ID")
    public Result<List<AllResourceTypeNumVO>> selectResourceForTypeCount(ResourceConf resourceConf) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        resourceConf.setTenantId(loginUser.getUser().getTenantId());
        resourceConf.setIndustryCode(loginUser.getUser().getIndustryCode());
        resourceConf.setAppVerCode(loginUser.getUser().getAppVerCode());
        resourceConf.setPageNum(null);
        resourceConf.setPageSize(null);
        log.info("selectResourceForTypeCount resourceConf:{} screenId:{} screenType:{}", JSONObject.toJSONString(resourceConf), resourceConf.getScreenId(), resourceConf.getScreenType());
        List<ResourceConf> resourceConfList = selectResourceList(resourceConf);
        List<AllResourceTypeNumVO> result = resourceConfList.stream()
                .collect(Collectors.groupingBy(ResourceConf::getResourceTypeName, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(19)
                .map(entry -> new AllResourceTypeNumVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        long otherCount = resourceConfList.stream()
                .map(ResourceConf::getResourceTypeName)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> result.stream().noneMatch(rc -> rc.getResourceTypeName().equals(entry.getKey())))
                .mapToLong(Map.Entry::getValue)
                .sum();
        if (otherCount > 0) {
            result.add(new AllResourceTypeNumVO("其他", otherCount));
        }
        return Result.data(result);
    }
}
