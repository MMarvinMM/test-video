package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.consumer.dao.VmOrganDeviceRelTDAO;
import com.chinatower.platform.consumer.dao.VmOrganDeviceRelTPlatfromDAO;
import com.chinatower.platform.consumer.item.CanalBean;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.consumer.item.OrganDeviceRelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织与设备关系表数据监听
 */
@Slf4j
@Service
public class OrganDeviceRelService {

    @Autowired
    private VmOrganDeviceRelTDAO vmOrganDeviceRelTDAO;

    @Autowired
    private VmOrganDeviceRelTPlatfromDAO vmOrganDeviceRelTPlatfromDAO;

    public void dealMsg(String message){
        log.info("开始同步vm_organ_device_rel_t数据");
        CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
        List<OrganDeviceRelDTO> insertList = new ArrayList<>();
        List<OrganDeviceRelDTO> updateList = new ArrayList<>();

        for(Object obj : canalBeans.getData()){
            if(obj == null){
                continue;
            }
            OrganDeviceRelDTO orgDevRel = JSON.parseObject(JSON.toJSONString(obj), OrganDeviceRelDTO.class);
            if (Constants.DELETE.equals(canalBeans.getType())) {
                //逻辑删除
                orgDevRel.setDeleteFlag("1");
                updateList.add(orgDevRel);
            }else if(Constants.INSERT.equals(canalBeans.getType()) || Constants.UPDATE.equals(canalBeans.getType())){
                //插入操作
                if(orgDevRel.getId() == null || vmOrganDeviceRelTDAO.countById(orgDevRel.getId()) <= 0 ){
                    insertList.add(orgDevRel);
                }else{
                    updateList.add(orgDevRel);
                }
            }else{
                log.warn("仅支持表的dml语句同步");
            }
        }
        // 批量操作
        if(!CollectionUtils.isEmpty(updateList)){
            vmOrganDeviceRelTDAO.batchUpdate(updateList);
        }
        if(!CollectionUtils.isEmpty(insertList)){
            vmOrganDeviceRelTDAO.batchInsert(insertList);
        }

        try{
            log.info("同步业务库组织设备关联表");
            // 批量操作
            if(!CollectionUtils.isEmpty(updateList)){
                vmOrganDeviceRelTPlatfromDAO.batchUpdate(updateList);
            }
            if(!CollectionUtils.isEmpty(insertList)){
                vmOrganDeviceRelTPlatfromDAO.batchInsert(insertList);
            }
        }catch (Exception e){
            log.error("同步业务库组织设备关联表异常:"+e);
        }
    }
}
