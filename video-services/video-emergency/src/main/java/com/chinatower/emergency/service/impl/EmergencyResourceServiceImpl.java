package com.chinatower.emergency.service.impl;

import com.chinatower.bdm.api.entity.ResourceConfTree;
import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.emergency.dto.Constant;
import com.chinatower.emergency.dto.EmergencyResourceVO;
import com.chinatower.emergency.mapper.EmergencyResourceMapper;
import com.chinatower.emergency.service.EmergencyResourceService;
import com.chinatower.emergency.utils.TreeByBdmUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源管理service实现类
 *
 * @author zhaol
 * @date 2022/05/03
 * @requestID
 */
@Service
@Slf4j
public class EmergencyResourceServiceImpl implements EmergencyResourceService {

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    EmergencyResourceMapper emergencyResourceMapper;



    /**
     * 获取应急资源树(tree格式用)
     * @param voIn 资源信息
     * @return 返回资源集合
     * @author liuxt
     * @throws
     * @requestID       *
     */
    @DataScope(resourceColumn = "d.RESOURCE_ID")
    @Override
    public List<TreeSelectByBdm> getEmergencyResourceTree(EmergencyResourceVO voIn) {
        List<TreeSelectByBdm> tree= new ArrayList<>();
        List<TreeSelectByBdm> list = emergencyResourceMapper.getEmergencyResourceTree(voIn);

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
                    fileUrl = fileUrl.split(Constant.COMMA)[Constant.ZERO];
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
            tree.add(new TreeSelectByBdm(vo));
        }
        List<TreeSelectByBdm> resourceTree = TreeByBdmUtils.listToTreeSelect(tree);
        //删除没有资源的资源类型
        deleteNode(resourceTree);
        return resourceTree;
    }


    /**
     * 删除节点
     * @param resourceTree
     */

    private static void deleteNode(List<TreeSelectByBdm> resourceTree) {
        // 创建一个迭代器来遍历列表
        Iterator<TreeSelectByBdm> iterator = resourceTree.iterator();
        // 迭代并标记删除条件的元素
        while (iterator.hasNext()) {
            TreeSelectByBdm node = iterator.next();
            if(Constant.ZERO_STR.equals(node.getType()) && CollectionUtils.isEmpty(node.getChildren())){
                iterator.remove(); // 使用iterator的remove方法来删除元素
            }else{
                List a = node.getChildren();
                // 创建一个迭代器来遍历列表
                Iterator<TreeSelectByBdm> iterator1 = a.iterator();
                while (iterator1.hasNext()) {
                    TreeSelectByBdm node1 = iterator1.next();
                    if(Constant.ZERO_STR.equals(node1.getType()) && CollectionUtils.isEmpty(node1.getChildren())){
                        iterator1.remove(); // 使用iterator的remove方法来删除元素
                    }
                }
                //判断删除完后的list是否有children为空的 若有则删除
                if(Constant.ZERO_STR.equals(node.getType()) && CollectionUtils.isEmpty(node.getChildren())){
                    iterator.remove(); // 使用iterator的remove方法来删除元素
                }
            }
        }
    }


    /**
     * 资源调派
     * @author liuxt
     * @param list
     * @return 新增结果
     * @throws
     * @requestID
     */
    @Override
    public int addEmergencyResource(List<EmergencyResourceVO> list) {
        return emergencyResourceMapper.addEmergencyResource(list);
    }

    /**
     * 删除调派资源
     * @author liuxt
     * @param emergencyResourceVO
     * @return 删除结果
     * @throws
     * @requestID
     */
    @Override
    public int deleteEmergencyResource(EmergencyResourceVO emergencyResourceVO) {
        return emergencyResourceMapper.deleteEmergencyResource(emergencyResourceVO);
    }

    public int deleteEmergencyResourceByEventCodes(EmergencyResourceVO emergencyResourceVO) {
        return emergencyResourceMapper.deleteEmergencyResourceByEventCodes(emergencyResourceVO);
    }


    public void updateResourceStatus(EmergencyResourceVO emergencyResourceVO) {
         emergencyResourceMapper.updateResourceStatus(emergencyResourceVO);
    }
}
