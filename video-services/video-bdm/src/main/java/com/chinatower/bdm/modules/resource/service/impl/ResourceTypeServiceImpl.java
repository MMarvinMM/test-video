package com.chinatower.bdm.modules.resource.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.ResourceTreeSelect;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.bdm.common.NonceUtil;
import com.chinatower.bdm.modules.resource.common.Constants;

import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.modules.resource.mapper.ResourceTypeMapper;
import com.chinatower.bdm.modules.resource.service.IResourceTypeService;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import com.chinatower.file.deal.FileUrlEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源类型管理service实现类
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Service("resourceTypeService")
@Slf4j
public class ResourceTypeServiceImpl extends TowerBaseController implements IResourceTypeService {
    @Autowired
    ResourceTypeMapper resourceTypeDao;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 获取资源类型顶级列表(顶级结构)
     * @author zhaol
     * @param resourceType 资源类型信息
     * @return 返回资源类型集合
     * @throws
     * @requestID
     */
    @Override
    public List<ResourceType> selectResourceTypeList(ResourceType resourceType) {
        List<ResourceType> list = new ArrayList<>();
        String queryType = resourceType.getQueryType();
        Set<String> resourceTypeIdList;
        if(StringUtils.isNotEmpty(resourceType.getResourceTypeName()) || StringUtils.isNotEmpty(resourceType.getApprove())){
            List<ResourceType> resourceTypes = resourceTypeDao.selectResourceTypeId(resourceType);
            resourceTypeIdList = resourceTypes.stream().map(item ->
                    ((Constants.ZERO_STR).equals(item.getParentId())||StringUtils.isEmpty(item.getParentId()))?item.getResourceTypeId():item.getParentId())
                    .collect(Collectors.toSet());
            log.info("resourceTypeIdList=" + JSONObject.toJSON(resourceTypeIdList));
            if(!CollectionUtils.isEmpty(resourceTypeIdList)){
                resourceType.setResourceTypeIdList(resourceTypeIdList);
            }else{
                return list;
            }
        }
        if((Constants.ONE_STR).equals(queryType)){
            startPage();
        }
        list = resourceTypeDao.queryResourceTypeList(resourceType);

        if(!(Constants.TWO_STR).equals(queryType)){
            if(!CollectionUtils.isEmpty(list)){
                //父级url集合
                List<String> fileUrlList = new ArrayList<>();
                //构造父节点id集合
                List<String> parentIdList = new ArrayList<>();

                for (ResourceType type : list) {
                    parentIdList.add(type.getResourceTypeId());
                    if(!StringUtils.isEmpty(type.getIcon())){
                        fileUrlList.add(type.getIcon());
                    }
                }

                //批量查询子节点
                resourceType.setResourceTypeIdList(null);
                resourceType.setParentIdList(parentIdList);
                List<ResourceType> childList = resourceTypeDao.queryResourceTypeList(resourceType);
                //添加子级url集合
                fileUrlList.addAll(
                        childList.stream().filter(item -> !StringUtils.isEmpty(item.getIcon())).map(ResourceType::getIcon).collect(Collectors.toList())
                );

                if(!CollectionUtils.isEmpty(fileUrlList)){
                    //批量加密url
                    Map<String, String> stringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(fileUrlList);
                    //set加密后的url
                    list.stream().forEach(item -> item.setIcon(stringMap.get(item.getIcon())));
                    childList.stream().forEach(item -> item.setIcon(stringMap.get(item.getIcon())));
                }

                //整合父级与子级的关系
                list.stream().map(item -> {
                    List<ResourceType> children = new ArrayList<>();
                    childList.stream().filter(area-> (Objects.equals(item.getResourceTypeId(),area.getParentId())
                            && Objects.equals(item.getTenantId(),area.getTenantId())
                            && Objects.equals(item.getIndustryCode(),area.getIndustryCode()))
                    ).forEach(area-> {
                        children.add(area);
                    });
                    item.setChildren(children);
                    return item;
                }).collect(Collectors.toList());
            }
        }
        return list;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<ResourceType> list, ResourceType t) {
        // 得到子节点列表
        List<ResourceType> childList = getChildList(list, t);
        t.setChildren(childList);
        for (ResourceType tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<ResourceType> it = childList.iterator();
                while (it.hasNext()) {
                    ResourceType n = (ResourceType) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    //得到子节点列表
    private List<ResourceType> getChildList(List<ResourceType> list, ResourceType t) {
        List<ResourceType> tlist = new ArrayList<ResourceType>();
        Iterator<ResourceType> it = list.iterator();
        while (it.hasNext()) {
            ResourceType n = (ResourceType) it.next();
            if (n.getParentId().equals(t.getResourceTypeId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    //判断是否有子节点
    private boolean hasChild(List<ResourceType> list, ResourceType t) {
        return getChildList(list, t).size() > 0;
    }
    
    /**
     * 新增资源类型
     * @param resourceType 资源类型
     * @return 类型详情
     * @throws
     * @requestID
     */
    @Override
    public int insertResourceType(ResourceType resourceType) {
        resourceType.setCreateTime(new Date());
        String resourceTypeId = NonceUtil.getNonceStr();
        String parentId = resourceType.getParentId();
        resourceType.setResourceTypeId(resourceTypeId);
        if(StringUtils.isEmpty(parentId)){
            resourceType.setParentId(Constants.ZERO_STR);
            resourceType.setTypeLevelUrl(resourceTypeId);
        }else{
            ResourceType vo = resourceTypeDao.selectByTypeId(parentId);
            if(vo!=null){
                resourceType.setTypeLevelUrl(vo.getTypeLevelUrl()+Constants.COMMA_STR+resourceTypeId);
            }
        }

        return resourceTypeDao.insertSelective(resourceType);
    }
    
    /**
     * 更新资源类型
     * @param resourceType 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    @Override
    public int updateResourceType(ResourceType resourceType) {
        resourceType.setUpdateTime(new Date());
        return resourceTypeDao.updateByPrimaryKeySelective(resourceType);
    }
    
    /**
     * 删除资源类型
     * @param resourceType 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    @Override
    public int deleteResourceTypeById(ResourceType resourceType) {
        resourceType.setUpdateTime(new Date());
        return resourceTypeDao.updateByResourceTypeId(resourceType);
    }
    
    /**
     * 是否存在资源类型子节点
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    @Override
    public boolean hasChildByResourceTypeId(ResourceType resourceType) {
        return resourceTypeDao.hasChildByResourceTypeId(resourceType) > Constants.ZERO_NUMBER;
    }
    
    /**
     * 是否存在资源类型名称相同
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    @Override
    public boolean checkResourceTypeNameUnique(ResourceType resourceType) {
        return resourceTypeDao.checkResourceTypeNameUnique(resourceType) > Constants.ZERO_NUMBER;
    }
    
    /**
     * 是否存在资源
     * @param  resourceType 资源类型
     * @return 结果 true 存在 false 不存在
     * @throws
     * @requestID
     */
    @Override
    public boolean checkResourceUnique(ResourceType resourceType) {
        return resourceTypeDao.checkResourceUnique(resourceType) > Constants.ZERO_NUMBER;
    }
    
    /**
     * 构建前端所需要树结构
     * @author zhaol
     * @param list 资源类型列表
     * @return 树结构列表
     * @throws
     * @requestID
     */
    @Override
    public List<ResourceTreeSelect> buildResourceTypeTreeSelect(List<ResourceType> list) {
        return list.stream().map(ResourceTreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<ResourceType> buildResourceTypeTree(List<ResourceType> list){
        List<ResourceType> returnList = new ArrayList<>();
        for (Iterator<ResourceType> iterator = list.iterator(); iterator.hasNext();) {
            ResourceType t = iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId().equals(Constants.RESOURCE_TYPE_ROOT_ID)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }
    
    /**
     * 根据id获取资源类型详情
     * @author zhaol
     * @param id 资源类型id
     * @return 类型详情
     * @throws
     * @requestID
     */
    @Override
    public ResourceType getInfo(Long id){
        //add by liuxt 20220826
        ResourceType resourceType = resourceTypeDao.selectByPrimaryKey(id);
        if(!ObjectUtils.isEmpty(resourceType) ){
            //add by liuxt video-file改造
            resourceType.setIcon(fileUrlEncryptUtil.dealObsFileUrl(resourceType.getIcon()));
        }
        return resourceType;
    }
    
    /**
     * 新增保存菜单信息
     * @param resourceTypeInt 资源类型
     * @return 结果
     * @throws
     * @requestID
     */
    @Override
    public Result initResourceType(ResourceTypeInt resourceTypeInt) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = resourceTypeInt.getTenantId();
        if(StringUtils.isEmpty(tenantId)){
            return Result.fail(Constants.CHECK_TENANTID_MSG);
        }
        String userName = "admin";
        if (loginUser!=null && loginUser.getUser()!=null && SecurityUtils.getOperator()!=null) {
            userName = SecurityUtils.getOperator();
        }

        String parentId = Constants.ZERO_STR;
        //获取初始化数据
        List<ResourceTypeInt> intList = resourceTypeDao.getResourceTypeInt(resourceTypeInt);
        //获取当前租户下数据
        ResourceType resourceType = new ResourceType();
        resourceType.setTenantId(tenantId);
        resourceType.setParentId(parentId);
        List<ResourceType> oldList = resourceTypeDao.selectResourceTypeList(resourceType);
        List<ResourceTypeInt> removeIntList = new ArrayList<>();
        for(ResourceTypeInt initVo : intList){
            Boolean flag = true;
            for(ResourceType oldVo  :oldList){
                if(initVo.getResourceTypeName().equals(oldVo.getResourceTypeName())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                String resourceTypeId = NonceUtil.getNonceStr();
                initVo.setResourceTypeId(NonceUtil.getNonceStr());
                initVo.setTenantId(tenantId);
                initVo.setParentId(parentId);
                initVo.setCreator(userName);
                initVo.setModifier(userName);
                initVo.setTypeLevelUrl(resourceTypeId);
            }else{
                removeIntList.add(initVo);
            }
        }
        intList.removeAll(removeIntList);
        return Result.data(CollUtil.isNotEmpty(intList) ?
                resourceTypeDao.insertResourceTypeInitData(intList) : Constants.ZERO_NUMBER);
    }


    /**
     * 获取资源类型顶级列表(顶级结构)下拉框优化
     * @author zhaol
     * @param resourceType 资源类型信息
     * @return 返回资源类型集合
     * @throws
     * @requestID
     */
    @Override
    public List<ResourceType> selectResourceTypeListNew(ResourceType resourceType) {
        List<ResourceType> list =  resourceTypeDao.selectResourceTypeList(resourceType);
        if(!CollectionUtils.isEmpty(list)){
            List<ResourceType> listChild = resourceTypeDao.selectResourceTypeList1New(list,resourceType.getTenantId(),resourceType.getIndustryCode(),resourceType.getResourceProperty(),resourceType.getAppVerCode());
            for (ResourceType parent : list) {
                List<ResourceType> childList = new ArrayList();
                for (ResourceType child : listChild){
                    if (parent.getResourceTypeId().equals(child.getParentId())) {
                        childList.add(child);
                    }
                }
                parent.setChildren(childList);
            }
        }
        return list;
    }
}
